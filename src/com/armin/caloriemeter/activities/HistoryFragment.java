package com.armin.caloriemeter.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.armin.caloriemeter.Constants;
import com.armin.caloriemeter.Date;
import com.armin.caloriemeter.MealConsumption;
import com.armin.caloriemeter.R;
import com.armin.caloriemeter.Time;
import com.armin.caloriemeter.adapters.MealHistoryAdapter;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.db.ConsumptionContract.ConsumptionEntry;
import com.armin.caloriemeter.db.ConsumptionContract.DailyConsumptionEntry;
import com.fourmob.datetimepicker.date.PersianCalendar;

public class HistoryFragment extends Fragment
{
	static PersianCalendar calendar = new PersianCalendar();
	static Calendar cal = Calendar.getInstance();

	private ConsumptionDatabaseHelper mDbHelper = new ConsumptionDatabaseHelper(getActivity());

	private View rootView = null;
	private MealHistoryAdapter adapter = null;
	
	private ArrayList<MealConsumption> mealsArray = new ArrayList<MealConsumption>();

	public HistoryFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, container, false);
		mDbHelper = new ConsumptionDatabaseHelper(getActivity());

		LinearLayout targetLayout = (LinearLayout) rootView.findViewById(R.id.target_layout);
		targetLayout.setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(), ProfileActivity.class));
					}
				});

		ListView meals = (ListView) rootView.findViewById(R.id.history_list_view);
		
		adapter = new MealHistoryAdapter(getActivity(), mealsArray);
		meals.setAdapter(adapter);
		if(isConnected())
			getDataFromServer(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
		else
			getDataFromDevice(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
		return rootView;
	}

	private void getDataFromDevice(int year, int month, int day) {
		new Query().execute(new Date(year, month, day));
	}

	private void getDataFromServer(int year, int month, int date) {
		// TODO Auto-generated method stub
		return;
	}

	private boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public void update(int year, int month, int day)
	{
		if(isConnected())
			getDataFromServer(year, month, day);
		else
			getDataFromDevice(year, month, day);
//		adapter.setMeals(mealsArray);
	}
	
	private class Query extends AsyncTask<Date, Void, Result>
	{
		@Override
		protected void onPostExecute(Result result) {
			adapter.notifyDataSetChanged();
			((TextView) rootView.findViewById(R.id.date_label)).setText(result.date.getString());
			((TextView) rootView.findViewById(R.id.target_calorie_amount)).setText(result.target+"");
			int remaining = result.target-result.consumed;
			if(remaining < 0)
				remaining = 0;
			((TextView) rootView.findViewById(R.id.remaining_calorie_amount)).setText(""+remaining);
			((TextView) rootView.findViewById(R.id.consumed_calorie)).setText(result.consumed+"");

			super.onPostExecute(result);
		}

		@Override
		protected Result doInBackground(Date... params) {
			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			String[] projection1 = {
					DailyConsumptionEntry.COLUMN_NAME_TARGET,
					DailyConsumptionEntry.COLUMN_NAME_CONSUMED
			};

			Date date = params[0];
			
			String today = date.getSTDString();
			String selection = DailyConsumptionEntry.COLUMN_NAME_DATE+" like '"+today+"'";

			Cursor cursor = db.query(
					DailyConsumptionEntry.TABLE_NAME,		// The table to query
					projection1,                            // The columns to return
					selection,                              // The columns for the WHERE clause
					null,                         			// The values for the WHERE clause
					null,                                   // don't group the rows
					null,                                   // don't filter by row groups
					null									// The sort order
					);

			int target = 0, consumed = 0;
			if(cursor.getCount() == 0)
			{
				SQLiteDatabase dbw = mDbHelper.getWritableDatabase();
				SharedPreferences userData = getActivity().getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
				target = userData.getInt(Constants.DAILY_TARGET, 0);
				ContentValues values = new ContentValues();
				values.put(DailyConsumptionEntry.COLUMN_NAME_TARGET, target);
				values.put(DailyConsumptionEntry.COLUMN_NAME_CONSUMED, 0);
				dbw.insert(DailyConsumptionEntry.TABLE_NAME, null, values);
			}
			else
			{
				cursor.moveToFirst();
				consumed = cursor.getInt(
						cursor.getColumnIndexOrThrow(DailyConsumptionEntry.COLUMN_NAME_CONSUMED));
				target = cursor.getInt(
						cursor.getColumnIndexOrThrow(DailyConsumptionEntry.COLUMN_NAME_TARGET));
			}
			
			String[] projection = {
					ConsumptionEntry.COLUMN_NAME_DATE,
					ConsumptionEntry.COLUMN_NAME_TIME,
					ConsumptionEntry.COLUMN_NAME_FOOD_NAME,
					ConsumptionEntry.COLUMN_NAME_MEAL,
					ConsumptionEntry.COLUMN_NAME_ENERGY,
					ConsumptionEntry.COLUMN_NAME_AMOUNT,
					ConsumptionEntry.COLUMN_NAME_UNIT,
			};

			selection = ConsumptionEntry.COLUMN_NAME_DATE+" like '"+today+"'";
			String sortOrder =
					ConsumptionEntry.COLUMN_NAME_TIME + " ASC";

			cursor = db.query(
					ConsumptionEntry.TABLE_NAME,  			  // The table to query
					projection,                               // The columns to return
					selection,                                // The columns for the WHERE clause
					null,                            // The values for the WHERE clause
					null,                                     // don't group the rows
					null,                                     // don't filter by row groups
					sortOrder                                 // The sort order
					);

			cursor.moveToFirst();

			mealsArray.clear();
			if(cursor.getCount() == 0)
				return new Result(date,target,consumed);

			do
			{
				String name = cursor.getString(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_FOOD_NAME));
				int energy = cursor.getInt(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_ENERGY));
				int amount = cursor.getInt(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_AMOUNT));
				String unit = cursor.getString(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_UNIT));
				int meal = cursor.getInt(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_MEAL));
				int minute = Integer.parseInt(cursor.getString(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_TIME)).substring(2));
				int hour = Integer.parseInt(cursor.getString(
						cursor.getColumnIndexOrThrow(ConsumptionEntry.COLUMN_NAME_TIME)).substring(0,2));
				Time time = new Time(hour, minute);

				mealsArray.add(new MealConsumption(name, energy, amount, unit, meal, time, date));
			}while(cursor.moveToNext());
			
			return new Result(date, target, consumed);
		}
	}
	
	private class Result
	{
		public Result(Date date, int target, int consumed)
		{
			this.date = date;
			this.target = target;
			this.consumed = consumed;
		}
		Date date;
		int target;
		int consumed;
	}
}
