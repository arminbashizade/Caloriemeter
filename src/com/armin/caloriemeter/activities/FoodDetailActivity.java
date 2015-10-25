package com.armin.caloriemeter.activities;

import java.util.ArrayList;
import java.util.Calendar;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.db.ConsumptionContract.ConsumptionEntry;
import com.armin.caloriemeter.db.ConsumptionContract.DailyConsumptionEntry;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.MealConsumption;
import com.armin.caloriemeter.util.Time;
import com.armin.caloriemeter.util.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.PersianTimePickerDialog;
import com.sleepbot.datetimepicker.time.PersianTimePickerDialog.OnTimeSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class FoodDetailActivity extends FragmentActivity 
implements TextWatcher, OnItemSelectedListener, OnClickListener, OnDateSetListener, OnTimeSetListener{

	private ConsumptionDatabaseHelper mDbHelper = new ConsumptionDatabaseHelper(this);
	
	private EditText amountEditText;
	private Spinner unitSpinner;
	private TextView energyTextView;
	private Spinner mealSpinner;
	private TextView weekdayTextView;
	private TextView monthDayTextView;
	private TextView yearMonthTextView;
	private TextView timeTextView;
	private TextView amPmTextView;

	private PersianCalendar calendar = new PersianCalendar();
//	private Calendar cal = Calendar.getInstance();

	PersianDatePickerDialog persianDatePickerDialog = PersianDatePickerDialog.newInstance(this, calendar.get(PersianCalendar.YEAR), calendar.get(PersianCalendar.MONTH), calendar.get(PersianCalendar.DAY_OF_MONTH), true, PersianDatePickerDialog.PERSIAN);
//	PersianDatePickerDialog datePickerDialog = PersianDatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), true, PersianDatePickerDialog.GREGORIAN);

//	PersianTimePickerDialog timePickerDialog = PersianTimePickerDialog.newInstance(this, cal.get(Calendar.HOUR_OF_DAY) ,cal.get(Calendar.MINUTE), false, false, PersianTimePickerDialog.ENGLISH);
	PersianTimePickerDialog persianTimePickerDialog = PersianTimePickerDialog.newInstance(this, calendar.get(PersianCalendar.HOUR_OF_DAY) ,calendar.get(PersianCalendar.MINUTE), false, false, PersianTimePickerDialog.PERSIAN);
	
	private String foodName = null;
	private float[] amount;
	private String[] unit = null;
	private float[] energy;
	private Date date;
	private Time time;

	private MealConsumption mealConsumption = new MealConsumption();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			foodName = extras.getString(Constants.FOOD_NAME_KEY);
			amount = extras.getFloatArray(Constants.AMOUNT_KEY);
		    unit = extras.getStringArray(Constants.UNIT_KEY);
		    energy = extras.getFloatArray(Constants.ENERGY_KEY);
		    date = (Date)extras.getSerializable(Constants.DATE_KEY);
		    time = (Time)extras.getSerializable(Constants.TIME_KEY);
		}

		mealConsumption.setName(foodName);
		setTitle(foodName+"");
		
		amountEditText = (EditText) findViewById(R.id.amount_edit_text);
		unitSpinner = (Spinner) findViewById(R.id.unit_spinner);
		energyTextView = (TextView) findViewById(R.id.energy_text_view);
		mealSpinner = (Spinner) findViewById(R.id.meal_spinner);
		weekdayTextView = (TextView) findViewById(R.id.weekday_text_view);
		monthDayTextView = (TextView) findViewById(R.id.month_day_text_view);
		yearMonthTextView = (TextView) findViewById(R.id.year_month_text_view);
		timeTextView = (TextView) findViewById(R.id.time_text_view);
		amPmTextView = (TextView) findViewById(R.id.am_pm_text_view);

		energyTextView.setText(Utils.toPersianNumbers("0"));
		amountEditText.addTextChangedListener(this);
		unitSpinner.setOnItemSelectedListener(this);
		
		mealSpinner.setOnItemSelectedListener(this);
		
		weekdayTextView.setOnClickListener(this);
		monthDayTextView.setOnClickListener(this);
		yearMonthTextView.setOnClickListener(this);
		timeTextView.setOnClickListener(this);
		amPmTextView.setOnClickListener(this);
		
		calendar.set(Calendar.DATE, date.day);
		calendar.set(Calendar.MONTH, date.month-1);
		calendar.set(Calendar.YEAR, date.year);
		calendar.set(Calendar.HOUR_OF_DAY, time.hour);
		calendar.set(Calendar.MINUTE, time.minute);

		setDateAndTime(date.year, date.month, date.day, time.hour, time.minute);
		
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
			    this, R.layout.spinner_rtl_item, getResources().getStringArray(R.array.meal_items));

		((Spinner) findViewById(R.id.meal_spinner)).setAdapter(adapter1);

		
		ArrayList<String> spinnerArray =  new ArrayList<String>();
		for(String s: unit)
			spinnerArray.add(s);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    this, R.layout.spinner_rtl_item, spinnerArray);

		adapter.setDropDownViewResource(R.layout.spinner_rtl_item);
		unitSpinner.setAdapter(adapter);
		unitSpinner.setSelection(0);
	}

	// zero-based month as input
	private void setDateAndTime(int year, int month, int day, int hourOfDay, int minute)
	{
		if(year != -1)
			mealConsumption.setDate(new Date(year, month, day));
		if(hourOfDay != -1)
			mealConsumption.setTime(new Time(hourOfDay, minute));
		weekdayTextView.setText(PersianCalendar.weekdayFullNames[calendar.get(Calendar.DAY_OF_WEEK)]+"");
		monthDayTextView.setText(Utils.toPersianNumbers(calendar.get(Calendar.DAY_OF_MONTH)+"")+"");
		yearMonthTextView.setText(PersianCalendar.months[calendar.get(Calendar.MONTH)]+" "+Utils.toPersianNumbers(calendar.get(Calendar.YEAR)+""));
		
		int hour = calendar.get(Calendar.HOUR);
		if(hour == 0)
			hour = 12;
		timeTextView.setText(Utils.toPersianNumbers(String.format("%d:%02d", hour, calendar.get(Calendar.MINUTE)))+"");
		amPmTextView.setText(calendar.get(Calendar.AM_PM)==Calendar.AM?"ق‌‌ظ":"ب‌ظ");
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.food_detail, menu);
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
		case R.id.action_add_food:
			if(TextUtils.isEmpty(amountEditText.getText()))
			{
				amountEditText.setError(getResources().getString(R.string.error_enter_amount));
				amountEditText.requestFocus();
				return true;
			}
			
			mealConsumption.setAmount(Integer.parseInt(Utils.toEnglishNumbers(amountEditText.getText().toString())));
			mealConsumption.setUnit(unit[unitSpinner.getSelectedItemPosition()]);
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(ConsumptionEntry.COLUMN_NAME_FOOD_NAME, mealConsumption.getName());
			values.put(ConsumptionEntry.COLUMN_NAME_AMOUNT, mealConsumption.getAmount());
			values.put(ConsumptionEntry.COLUMN_NAME_DATE, mealConsumption.getDate().getSTDString());
			values.put(ConsumptionEntry.COLUMN_NAME_ENERGY, mealConsumption.getEnergy());
			values.put(ConsumptionEntry.COLUMN_NAME_MEAL, mealConsumption.getMealNumeral());
			values.put(ConsumptionEntry.COLUMN_NAME_TIME, mealConsumption.getTime().getSTDString());
			values.put(ConsumptionEntry.COLUMN_NAME_UNIT, mealConsumption.getUnit());
			db.insert(ConsumptionEntry.TABLE_NAME, null, values);
			
			values = new ContentValues();
			String[] projection = {DailyConsumptionEntry.COLUMN_NAME_CONSUMED};
			String selection = DailyConsumptionEntry.COLUMN_NAME_DATE + " = ?";
			String[] selectionArgs = { mealConsumption.getDate().getSTDString() };

			Cursor cursor = db.query(
					DailyConsumptionEntry.TABLE_NAME,		// The table to query
					projection,                            // The columns to return
					selection,                              // The columns for the WHERE clause
					selectionArgs,                         			// The values for the WHERE clause
					null,                                   // don't group the rows
					null,                                   // don't filter by row groups
					null									// The sort order
					);
			
			cursor.moveToFirst();
			if(cursor.getCount() == 0) //this date has no target set and no consumption
			{
				SharedPreferences userData = getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
				int target = userData.getInt(Constants.DAILY_TARGET, 0);
				values = new ContentValues();
				values.put(DailyConsumptionEntry.COLUMN_NAME_TARGET, target);
				values.put(DailyConsumptionEntry.COLUMN_NAME_DATE, mealConsumption.getDate().getSTDString());
				values.put(DailyConsumptionEntry.COLUMN_NAME_CONSUMED, mealConsumption.getEnergy());
				
				db.insert(DailyConsumptionEntry.TABLE_NAME, null, values);
			}
			else
			{
				int consumed = cursor.getInt(
						cursor.getColumnIndexOrThrow(DailyConsumptionEntry.COLUMN_NAME_CONSUMED));
				consumed += mealConsumption.getEnergy();
				values = new ContentValues();
				values.put(DailyConsumptionEntry.COLUMN_NAME_CONSUMED, consumed);
				db.update(
						DailyConsumptionEntry.TABLE_NAME,
						values,
						selection,
						selectionArgs);
			}
			db.close();
		}
		finish();
		Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(Constants.DATE_KEY, mealConsumption.getDate());
		intent.putExtra(Constants.TIME_KEY, (Time)getIntent().getExtras().getSerializable(Constants.TIME_KEY));
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		if(s.toString().length() == 0)
			return;
		int unitIndex = unitSpinner.getSelectedItemPosition();
		float enteredAmount = Float.parseFloat(s.toString());
		mealConsumption.setEnergy((int) (enteredAmount * energy[unitIndex] / amount[unitIndex]));
		energyTextView.setText(Utils.toPersianNumbers(mealConsumption.getEnergy()+"")+"");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		if(parent.getId() == R.id.unit_spinner)
		{
			if(TextUtils.isEmpty(amountEditText.getText()))
				return;
			int enteredAmount = Integer.parseInt(Utils.toEnglishNumbers(amountEditText.getText().toString()));
			mealConsumption.setEnergy((int) (enteredAmount * energy[position] / amount[position]));
			energyTextView.setText(Utils.toPersianNumbers(mealConsumption.getEnergy()+"")+"");
		}
		
		if(parent.getId() == R.id.meal_spinner)
		{
			mealConsumption.setMeal(mealSpinner.getSelectedItemPosition());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.weekday_text_view:
		case R.id.month_day_text_view:
		case R.id.year_month_text_view:
			persianDatePickerDialog.setYearRange(Constants.FIRST_YEAR_FOOD, calendar.get(Calendar.YEAR));
			persianDatePickerDialog.setVibrate(false);
			persianDatePickerDialog.setCloseOnSingleTapDay(false);
			persianDatePickerDialog.show(getSupportFragmentManager(), "datepicker_food_detail");			
			return;

		case R.id.time_text_view:
		case R.id.am_pm_text_view:
			persianTimePickerDialog.setInitialTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
			persianTimePickerDialog.setVibrate(false);
			persianTimePickerDialog.setCloseOnSingleTapMinute(false);
			persianTimePickerDialog.show(getSupportFragmentManager(), "timepicker");
		}
	}

	@Override
	public void onDateSet(PersianDatePickerDialog datePickerDialog, int year,
			int month, int day)
	{
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		
		setDateAndTime(year, month, day, -1, -1);
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
	{
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		
		setDateAndTime(-1, -1, -1, hourOfDay, minute);
	}

}
