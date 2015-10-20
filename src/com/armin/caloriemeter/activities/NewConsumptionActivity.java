package com.armin.caloriemeter.activities;

import java.util.ArrayList;

import com.armin.caloriemeter.Constants;
import com.armin.caloriemeter.Date;
import com.armin.caloriemeter.R;
import com.armin.caloriemeter.Time;
import com.armin.caloriemeter.adapters.ResultsAdapter;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.db.ConsumptionContract.FoodEntry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class NewConsumptionActivity extends Activity implements TextWatcher, OnItemClickListener{

	private ConsumptionDatabaseHelper mDbHelper = new ConsumptionDatabaseHelper(this);

	private EditText search;
	private	ListView resultsListView;
	private Query query = new Query();
	private ArrayList<String> results = new ArrayList<String>();
	private ResultsAdapter adapter;

	private Date date;
	private Time time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_consumption);

		resultsListView = (ListView) findViewById(R.id.new_food_search_results_list_view);
		adapter = new ResultsAdapter(this, results);
		resultsListView.setAdapter(adapter);
		resultsListView.setOnItemClickListener(this);

		search = (EditText) findViewById(R.id.search_edit_text);
		search.addTextChangedListener(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    date = (Date)extras.getSerializable(Constants.DATE_KEY);
		    time = (Time)extras.getSerializable(Constants.TIME_KEY);		    
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_consumption, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_add_new_food)
		{
			//TODO
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO involve server too or just simply update foods on deveice
		// it won't take much space (< 2MB)
		if(s.length() < 3)
		{
			results.clear();
			adapter.notifyDataSetChanged();
			return;
		}

		query =  new Query();
		query.execute(s);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		String foodName = results.get(position);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * FROM "+FoodEntry.TABLE_NAME+
				" WHERE "+FoodEntry.COLUMN_NAME_FOOD_NAME+" = '" + foodName+"'", null);

		cursor.moveToFirst();
		float[] amount = new float[cursor.getCount()];
		float[] energy = new float[cursor.getCount()];
		String[] unit = new String[cursor.getCount()];
		int i = 0;
		do{
			amount[i] = cursor.getFloat(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_AMOUNT));
			energy[i] = cursor.getFloat(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_ENERGY));
			unit[i] = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_UNIT));
			i++;
		}while(cursor.moveToNext());

		Intent intent = new Intent(getBaseContext(), FoodDetailActivity.class);

		intent.putExtra(Constants.FOOD_NAME_KEY, foodName);
		intent.putExtra(Constants.AMOUNT_KEY, amount);
		intent.putExtra(Constants.UNIT_KEY, unit);
		intent.putExtra(Constants.ENERGY_KEY, energy);
		intent.putExtra(Constants.DATE_KEY, date);
		intent.putExtra(Constants.TIME_KEY, time);

		startActivity(intent);
	}

	class Query extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... params) {

			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			String[] projection1 = {FoodEntry.COLUMN_NAME_FOOD_NAME};

			String selection = FoodEntry.COLUMN_NAME_FOOD_NAME+" like '%"+((CharSequence)params[0]).toString()+"%'";

			Cursor cursor = db.query(
					true,
					FoodEntry.TABLE_NAME,		// The table to query
					projection1,                             // The columns to return
					selection,                              // The columns for the WHERE clause
					null,                          // The values for the WHERE clause
					null,                                   // don't group the rows
					null,                                   // don't filter by row groups
					null,									// The sort order
					Constants.RESULTS_LIMIT
					);

			if(cursor.getCount() == 0)
			{
				results.clear();
				return null;
			}

			results.clear();
			cursor.moveToFirst();
			do{
				results.add(cursor.getString(
						cursor.getColumnIndexOrThrow(
								FoodEntry.COLUMN_NAME_FOOD_NAME))); 
			}while(cursor.moveToNext());

			return null;
		}
		@Override
		protected void onPostExecute(Object result) {
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

}

