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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.adapters.MealHistoryAdapter;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.db.ConsumptionContract.ConsumptionEntry;
import com.armin.caloriemeter.db.ConsumptionContract.DailyConsumptionEntry;
import com.armin.caloriemeter.dialogs.DeleteConsumptionDialog;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.MealConsumption;
import com.armin.caloriemeter.util.Time;
import com.armin.caloriemeter.util.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;

public class HistoryFragment extends Fragment
{
	private TextView consumedTextView;
	private TextView remainingTextView;
	private TextView targetTextView;
	
	static PersianCalendar calendar = new PersianCalendar();
//	static Calendar cal = Calendar.getInstance();
	
	private ConsumptionDatabaseHelper mDbHelper = new ConsumptionDatabaseHelper(getActivity());

	private View rootView = null;
	private MealHistoryAdapter adapter = null;
	
	private ArrayList<MealConsumption> mealsArray = new ArrayList<MealConsumption>();

	public HistoryFragment()
	{
	}

	public void setDate(int year, int month, int day)
	{
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, container, false);
		mDbHelper = new ConsumptionDatabaseHelper(getActivity());

		remainingTextView = (TextView) rootView.findViewById(R.id.remaining_calorie_amount);
		consumedTextView = (TextView) rootView.findViewById(R.id.consumed_calorie);
		targetTextView = (TextView) rootView.findViewById(R.id.target_calorie_amount);
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
		meals.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3)
					{
						DeleteConsumptionDialog dialog = new DeleteConsumptionDialog();
						dialog.setItemPosition(position);
						dialog.setArray(mealsArray);
						dialog.show(getActivity().getSupportFragmentManager(), "delete_dialog");
					}
				});
		meals.setAdapter(adapter);
		if(isConnected())
			getDataFromServer(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
		else
			getDataFromDevice(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
		return rootView;
	}

	private void getDataFromDevice(int year, int month, int day) {
		new HistoryQuery().execute(new Date(year, month, day));
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
	}
	
	private class HistoryQuery extends AsyncTask<Date, Void, Result>
	{
		@Override
		protected void onPostExecute(Result result) {
			adapter.notifyDataSetChanged();
			PersianCalendar pcal = new PersianCalendar(result.date.year, result.date.month-1, result.date.day);

			((TextView) rootView.findViewById(R.id.date_label))
			.setText(PersianCalendar.weekdayFullNames[pcal.get(PersianCalendar.DAY_OF_WEEK)]
					+" "+result.date.getString());
			
			targetTextView.setText(Utils.toPersianNumbers(result.target+"")+"");
			int remaining = result.target-result.consumed;
			if(remaining < 0)
				remaining = 0;
			remainingTextView.setText(Utils.toPersianNumbers(""+remaining)+"");
			consumedTextView.setText(Utils.toPersianNumbers(result.consumed+"")+"");

			PersianCalendar tmpCal = new PersianCalendar();
			int d = tmpCal.get(PersianCalendar.DAY_OF_MONTH);
			int m = tmpCal.get(PersianCalendar.MONTH);
			int y = tmpCal.get(PersianCalendar.YEAR);
			if(result.date.day == d && result.date.month == m && result.date.year == y)
			{
				SharedPreferences userData = getActivity().getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
				SharedPreferences.Editor editor = userData.edit();
				editor.putInt(Constants.CONSUMED_KEY, result.consumed);
				editor.commit();
			}
			
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
			String selection = DailyConsumptionEntry.COLUMN_NAME_DATE+" LIKE '"+today+"'";

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
			
			db.close();
			
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

	class DeleteConsumptionQuery extends AsyncTask<MealConsumption, Object, Integer>{

		@Override
		protected Integer doInBackground(MealConsumption... params) {

			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			MealConsumption selectedItem = params[0];
			String selection = 
					ConsumptionEntry.COLUMN_NAME_FOOD_NAME+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_AMOUNT+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_DATE+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_ENERGY+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_MEAL+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_TIME+" = ?" +
			" AND "+ConsumptionEntry.COLUMN_NAME_UNIT+" = ?";

			String[] selectionArgs = {
					selectedItem.getName(),
					Integer.toString(selectedItem.getAmount()),
					selectedItem.getDate().getSTDString(),
					Integer.toString(selectedItem.getEnergy()),
					Integer.toString(selectedItem.getMealNumeral()),
					selectedItem.getTime().getSTDString(),
					selectedItem.getUnit()
			};
			
			db.delete(ConsumptionEntry.TABLE_NAME, selection, selectionArgs);
			
			ContentValues values = new ContentValues();
			int currentConsumed = Integer.parseInt(Utils.toEnglishNumbers(consumedTextView.getText().toString()));
			values.put(DailyConsumptionEntry.COLUMN_NAME_CONSUMED, currentConsumed-selectedItem.getEnergy());
			selection = DailyConsumptionEntry.COLUMN_NAME_DATE + " = ?";
			String[] selectionArgs1 = { selectedItem.getDate().getSTDString() };
			
			db.update(DailyConsumptionEntry.TABLE_NAME, values, selection, selectionArgs1);

			db.close();
			
			return currentConsumed-selectedItem.getEnergy();
		}
		@Override
		protected void onPostExecute(Integer result) {
			consumedTextView.setText(Utils.toPersianNumbers(""+result)+"");
			int target = Integer.parseInt(Utils.toEnglishNumbers(targetTextView.getText().toString()));
			int remaining = target-result;
			if(remaining < 0)
				remaining = 0;
			remainingTextView.setText(Utils.toPersianNumbers(""+remaining)+"");
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
}
