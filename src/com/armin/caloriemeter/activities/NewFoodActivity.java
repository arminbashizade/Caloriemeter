package com.armin.caloriemeter.activities;

import java.util.ArrayList;
import com.armin.caloriemeter.R;
import com.armin.caloriemeter.adapters.IngredientsAdapter;
import com.armin.caloriemeter.db.ConsumptionContract.FoodEntry;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.MealConsumption;
import com.armin.caloriemeter.util.Time;
import com.armin.caloriemeter.util.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class NewFoodActivity extends Activity {

	private EditText nameEditText;
	private EditText amountEditText;
	private EditText unitEditText;
	private TextView unitTextView;
	private EditText energyEditText;
	private TextView energyTextView;
	private ListView ingridients;

	private String[] ingridientNames;
	private int[] ingridientAmounts;
	private String[] ingridientUnits;
	private int[] ingridientEnergy;

	private ArrayList<MealConsumption> ingridientsList = new ArrayList<MealConsumption>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_food);
		// Show the Up button in the action bar.
		setupActionBar();

		nameEditText = (EditText) findViewById(R.id.new_food_name_edit_text);
		amountEditText = (EditText) findViewById(R.id.new_food_serving_size_edit_text);
		unitEditText = (EditText) findViewById(R.id.new_food_serving_unit_edit_text);
		energyEditText = (EditText) findViewById(R.id.new_food_energy_edit_text);
		unitTextView = (TextView) findViewById(R.id.new_food_unit_text_view);
		ingridients = (ListView) findViewById(R.id.new_food_ingridients);
		energyTextView = (TextView)findViewById(R.id.new_food_energy_text_view); 

		energyTextView.addTextChangedListener(
				new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
					}					

					@Override
					public void afterTextChanged(Editable s) {
						if(Float.parseFloat(Utils.toEnglishNumbers(s.toString())) == 0)
						{
							energyTextView.setVisibility(View.GONE);
							energyEditText.setVisibility(View.VISIBLE);
						}
					}
				});
		

		Bundle extras = getIntent().getExtras();
		nameEditText.setText(extras.getString(Constants.FOOD_NAME_KEY));
		if(extras.getString(Constants.ENTERED_AMOUNT_KEY) != null)
		{
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			amountEditText.setText(extras.getString(Constants.ENTERED_AMOUNT_KEY));
			unitEditText.setText(extras.getString(Constants.ENTERED_UNIT_KEY));
			unitTextView.setText(extras.getString(Constants.ENTERED_UNIT_KEY));

			ingridientAmounts = extras.getIntArray(Constants.AMOUNT_KEY);
			ingridientNames = extras.getStringArray(Constants.INGRIDIENTS_KEY);
			ingridientUnits = extras.getStringArray(Constants.UNIT_KEY);
			ingridientEnergy = extras.getIntArray(Constants.ENERGY_KEY);
			float newEnergy = extras.getFloat(Constants.ENERGY_TOTAL_KEY);
			float totalEnergy = Float.parseFloat(Utils.toEnglishNumbers(TextUtils.isEmpty(extras.getString(Constants.PREVIOUSLY_CALCULATED_ENERGY))?"0":extras.getString(Constants.PREVIOUSLY_CALCULATED_ENERGY)));
			totalEnergy += newEnergy;

			if(totalEnergy != 0)
			{
				energyTextView.setVisibility(View.VISIBLE);
				energyEditText.setVisibility(View.GONE);
				energyTextView.setText(Utils.toPersianNumbers(""+totalEnergy)+"");
			}
			else
			{
				energyTextView.setVisibility(View.GONE);
				energyEditText.setVisibility(View.VISIBLE);
			}

			ingridientsList = (ArrayList<MealConsumption>) extras.getSerializable(Constants.INGRIDIENTS_LIST_KEY);
			if(ingridientsList == null)
				ingridientsList = new ArrayList<MealConsumption>();
			for(int i = 0; i < ingridientAmounts.length; i++)
			{
				for(MealConsumption m: ingridientsList)
				{
					if(m.getName().equals(ingridientNames[i].substring(1)) &&
							m.getUnit().equals(ingridientUnits[i]))
					{
						ingridientsList.remove(m);
						break;
					}
				}
				ingridientsList.add(new MealConsumption(ingridientNames[i].substring(1), ingridientEnergy[i], ingridientAmounts[i], ingridientUnits[i], 0, null, null));
			}
		}
		else
		{
			nameEditText.requestFocus();
			energyTextView.setVisibility(View.GONE);
			energyEditText.setVisibility(View.VISIBLE);
		}

		unitEditText.addTextChangedListener(
				new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
					}
					@Override
					public void afterTextChanged(Editable s) {
						unitTextView.setText(s.toString());
					}
				});

		IngredientsAdapter adapter = new IngredientsAdapter(this, ingridientsList, energyTextView);
		ingridients.setAdapter(adapter);

		((RelativeLayout) findViewById(R.id.add_new_ingredient_button)).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(NewFoodActivity.this, SearchActivity.class);

						intent.putExtra(Constants.INGRIDIENTS_LIST_KEY, ingridientsList);
						intent.putExtra(Constants.PREVIOUSLY_CALCULATED_ENERGY, energyTextView.getText().toString());
						intent.putExtra(Constants.FOOD_NAME_KEY, nameEditText.getText().toString());
						intent.putExtra(Constants.ENTERED_AMOUNT_KEY, amountEditText.getText().toString());
						intent.putExtra(Constants.ENTERED_UNIT_KEY, unitTextView.getText().toString());
						intent.putExtra(Constants.DATE_KEY, (Date)getIntent().getExtras().getSerializable(Constants.DATE_KEY));
						intent.putExtra(Constants.TIME_KEY, (Time)getIntent().getExtras().getSerializable(Constants.TIME_KEY));
						finish();
						startActivity(intent);						
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_food, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_create_new_food:
			if(checkForEmptyFields(getIntent().getExtras().getString(Constants.FOOD_NAME_KEY) != null))
				return true;

			ConsumptionDatabaseHelper dbHelper = new ConsumptionDatabaseHelper(this);
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			ContentValues values = new ContentValues();
			values.put(FoodEntry.COLUMN_NAME_FOOD_NAME, nameEditText.getText().toString());
			values.put(FoodEntry.COLUMN_NAME_AMOUNT, Float.parseFloat(Utils.toEnglishNumbers(amountEditText.getText().toString())));
			values.put(FoodEntry.COLUMN_NAME_UNIT, unitEditText.getText().toString());
			if(getIntent().getExtras().getString(Constants.FOOD_NAME_KEY) != null)
				values.put(FoodEntry.COLUMN_NAME_ENERGY, Float.parseFloat(Utils.toEnglishNumbers(energyTextView.getText().toString())));
			else
				values.put(FoodEntry.COLUMN_NAME_ENERGY, Float.parseFloat(Utils.toEnglishNumbers(energyEditText.getText().toString())));

			Cursor cursor = db.rawQuery("SELECT *" +
					" FROM " + FoodEntry.TABLE_NAME +
					" WHERE " + FoodEntry.COLUMN_NAME_FOOD_NAME + " like '" + nameEditText.getText().toString() + "'" +
					" AND " + FoodEntry.COLUMN_NAME_AMOUNT + " = " + Utils.toEnglishNumbers(amountEditText.getText().toString()), null);

			if(cursor.getCount() != 0)
			{
				nameEditText.setError(getResources().getString(R.string.error_food_exists));
				nameEditText.requestFocus();
				return true;
			}

			db.insert(FoodEntry.TABLE_NAME, null, values);

			db.close();
			
			finish();
			Intent intent = new Intent(NewFoodActivity.this, HistoryActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			Date date = (Date) getIntent().getExtras().getSerializable(Constants.DATE_KEY);
			Time time = (Time) getIntent().getExtras().getSerializable(Constants.TIME_KEY);
			intent.putExtra(Constants.DATE_KEY, date);
			intent.putExtra(Constants.TIME_KEY, time);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean checkForEmptyFields(boolean ingredient)
	{
		if(TextUtils.isEmpty(nameEditText.getText()))
		{
			nameEditText.setError(getResources().getString(R.string.error_enter_food_name));
			nameEditText.requestFocus();
			return true;
		}

		if(TextUtils.isEmpty(unitEditText.getText()))
		{
			unitEditText.setError(getResources().getString(R.string.error_enter_serving_unit));
			unitEditText.requestFocus();
			return true;
		}

		if(TextUtils.isEmpty(amountEditText.getText()))
		{
			amountEditText.setError(getResources().getString(R.string.error_enter_serving_amount));
			amountEditText.requestFocus();
			return true;
		}

		if(!ingredient && TextUtils.isEmpty(energyEditText.getText()))
		{
			energyEditText.setError(getResources().getString(R.string.error_enter_energy));
			energyEditText.requestFocus();
			return true;
		}
		return false;
	}

}
