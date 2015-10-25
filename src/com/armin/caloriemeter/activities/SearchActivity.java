package com.armin.caloriemeter.activities;

import java.util.ArrayList;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.adapters.ResultsAdapter;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.db.ConsumptionContract.FoodEntry;
import com.armin.caloriemeter.dialogs.IngredientDetailDialogFragment;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.Time;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends FragmentActivity implements TextWatcher, OnItemClickListener, IngredientDetailDialogFragment.IngredientDetailDialogListener{

	private static final int MIN_SEARCH_LENGTH = 1;

	private ConsumptionDatabaseHelper mDbHelper = new ConsumptionDatabaseHelper(this);

	private int searchMode = Constants.INGREDIENTS;

	private TextView nothingFoundTextView;
	private EditText searchEditText;
	private	ListView resultsListView;
	private FoodQuery query = new FoodQuery();
	private ArrayList<String> results = new ArrayList<String>();
	private ResultsAdapter adapter;
	
	private ArrayList<MarkedItem> markedItems = new ArrayList<MarkedItem>();

	private Date date;
	private Time time;
	
	private float energyTotal = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			date = (Date)extras.getSerializable(Constants.DATE_KEY);
			time = (Time)extras.getSerializable(Constants.TIME_KEY);
			searchMode = extras.getInt(Constants.SEARCH_MODE_KEY);
		}
		
		resultsListView = (ListView) findViewById(R.id.new_food_search_results_list_view);
		adapter = new ResultsAdapter(this, results);
		resultsListView.setAdapter(adapter);
		resultsListView.setOnItemClickListener(this);

		searchEditText = (EditText) findViewById(R.id.search_edit_text);
		searchEditText.addTextChangedListener(this);
		
		nothingFoundTextView = (TextView) findViewById(R.id.nothing_was_found_text_view);
		nothingFoundTextView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(searchMode == Constants.INGREDIENTS)
							return;
						
						Intent intent = new Intent(SearchActivity.this, NewFoodActivity.class);
						intent.putExtra(Constants.FOOD_NAME_KEY, searchEditText.getText().toString());
						intent.putExtra(Constants.DATE_KEY, date);
						intent.putExtra(Constants.TIME_KEY, time);
						startActivity(intent);
					}
				});
		
		markedItems.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(searchMode == Constants.FOOD)
			getMenuInflater().inflate(R.menu.food_search, menu);
		else
			getMenuInflater().inflate(R.menu.ingridient_search, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		if(searchMode == Constants.INGREDIENTS)
			backToCreateFood();
		super.onBackPressed();
	}
	
	public void backToCreateFood()
	{
		Intent intent = new Intent(SearchActivity.this, NewFoodActivity.class);
		String[] names = new String[markedItems.size()];
		int[] amounts = new int[markedItems.size()];
		String[] units = new String[markedItems.size()];
		int[] energies = new int[markedItems.size()];
		for(int i = 0; i < amounts.length; i++)
		{
			names[i] = markedItems.get(i).name;
			amounts[i] = markedItems.get(i).amount;
			units[i] = markedItems.get(i).unit;
			energies[i] = markedItems.get(i).energy;
		}
		
		intent.putExtra(Constants.FOOD_NAME_KEY, getIntent().getExtras().getString(Constants.FOOD_NAME_KEY));
		intent.putExtra(Constants.ENTERED_AMOUNT_KEY, getIntent().getExtras().getString(Constants.ENTERED_AMOUNT_KEY));
		intent.putExtra(Constants.ENTERED_UNIT_KEY, getIntent().getExtras().getString(Constants.ENTERED_UNIT_KEY));
		intent.putExtra(Constants.PREVIOUSLY_CALCULATED_ENERGY, getIntent().getExtras().getString(Constants.PREVIOUSLY_CALCULATED_ENERGY));
		intent.putExtra(Constants.INGRIDIENTS_LIST_KEY, getIntent().getExtras().getSerializable(Constants.INGRIDIENTS_LIST_KEY));
		intent.putExtra(Constants.INGRIDIENTS_KEY, names);
		intent.putExtra(Constants.AMOUNT_KEY, amounts);
		intent.putExtra(Constants.UNIT_KEY, units);
		intent.putExtra(Constants.ENERGY_KEY, energies);
		intent.putExtra(Constants.ENERGY_TOTAL_KEY, energyTotal);
		intent.putExtra(Constants.DATE_KEY, date);
		intent.putExtra(Constants.TIME_KEY, time);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_add_new_food)
		{
			Intent intent = new Intent(SearchActivity.this, NewFoodActivity.class);
			intent.putExtra(Constants.DATE_KEY, date);
			intent.putExtra(Constants.TIME_KEY, time);
			startActivity(intent);
		}
		else if(item.getItemId() == R.id.action_done_ingridients)
		{
			finish();
			backToCreateFood();
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
		if(s.length() < MIN_SEARCH_LENGTH)
		{
			results.clear();
			for(MarkedItem tmp: markedItems)
				results.add(tmp.name);

			nothingFoundTextView.setVisibility(View.GONE);
			
			adapter.notifyDataSetChanged();
			return;
		}

		query = new FoodQuery();
		query.execute(s);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		String foodName = results.get(position);
		if(foodName.startsWith(Constants.MARKED)) //unmark
		{
			String tmp = foodName.substring(1);
			results.set(position, tmp);
			if(searchEditText.getText().length() < MIN_SEARCH_LENGTH || !tmp.contains(searchEditText.getText().toString())) // item is not match with the searched text and should be removed
				results.remove(position);
			for(MarkedItem p: markedItems)
				if(p.name.equals(foodName))
				{
					energyTotal -= p.energy;
					markedItems.remove(p);
					break;
				}
			adapter.notifyDataSetChanged();
			return;
		}

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

		db.close();
		
		if(searchMode == Constants.INGREDIENTS) // creating new food
		{
			 IngredientDetailDialogFragment dialog = new IngredientDetailDialogFragment();
			 dialog.setArguments(unit, energy, amount);
			 dialog.setListItemPosition(position);
			 dialog.show(getFragmentManager(), "ingridient_detail");
		}
		else //adding new consumption
		{
			Intent intent = new Intent(getBaseContext(), FoodDetailActivity.class);

			intent.putExtra(Constants.FOOD_NAME_KEY, foodName);
			intent.putExtra(Constants.AMOUNT_KEY, amount);
			intent.putExtra(Constants.UNIT_KEY, unit);
			intent.putExtra(Constants.ENERGY_KEY, energy);
			intent.putExtra(Constants.DATE_KEY, date);
			intent.putExtra(Constants.TIME_KEY, time);

			startActivity(intent);
		}
	}

	class FoodQuery extends AsyncTask<Object, Object, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {

			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			String[] projection1 = {FoodEntry.COLUMN_NAME_FOOD_NAME};

			String selection = FoodEntry.COLUMN_NAME_FOOD_NAME+" LIKE '"+((CharSequence)params[0]).toString()+"%' OR "
			+FoodEntry.COLUMN_NAME_FOOD_NAME+" LIKE ' "+((CharSequence)params[0]).toString()+"%'";

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

			results.clear();
			for(MarkedItem s: markedItems)
				results.add(s.name);
			if(cursor.getCount() == 0)
				return false;

			cursor.moveToFirst();
			do{
				String name = cursor.getString(
						cursor.getColumnIndexOrThrow(
								FoodEntry.COLUMN_NAME_FOOD_NAME));
				if(!results.contains(Constants.MARKED+name))
					results.add(name); 
			}while(cursor.moveToNext());

			db.close();
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(!result)
			{
				nothingFoundTextView.setVisibility(View.VISIBLE);
				String s1 = getResources().getString(R.string.nothing_was_found);
				if(searchMode == Constants.INGREDIENTS)
				{
					nothingFoundTextView.setText(s1);
					return;
				}
				String s2 = getResources().getString(R.string.click_here_to);
				String s3 = getResources().getString(R.string.add_this_food);
				String full = s1 +" "+ s2 +" " + searchEditText.getText() +" "+s3;
				nothingFoundTextView.setText(full);
			}
			else
			{
				nothingFoundTextView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}


	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}

	@Override
	public void onDialogPositiveClick(int amount, int energy, String unit, int listItemPosition)
	{
		results.set(listItemPosition, Constants.MARKED+results.get(listItemPosition));
		adapter.notifyDataSetChanged();
		markedItems.add(new MarkedItem(results.get(listItemPosition), amount, unit, energy));
		energyTotal += energy;
	}

	public class MarkedItem
	{
		public MarkedItem(String name, int amount, String unit, int energy)
		{
			this.name = name;
			this.amount = amount;
			this.unit = unit;
			this.energy = energy;
		}
		String name;
		int amount;
		int energy;
		String unit;
	}
}

