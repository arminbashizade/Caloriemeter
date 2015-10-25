package com.armin.caloriemeter.activities;

import java.util.Calendar;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.db.ConsumptionContract.DailyConsumptionEntry;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Date;
import com.armin.caloriemeter.util.User;
import com.armin.caloriemeter.util.Utils;
import com.fourmob.datetimepicker.date.PersianCalendar;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog;
import com.fourmob.datetimepicker.date.PersianDatePickerDialog.OnDateSetListener;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class ProfileActivity extends FragmentActivity implements OnDateSetListener {

	private static String[] bmiExplanations = null;
	private Spinner genderSpinner;
	private EditText dateOfBirthEditText;
	private EditText heightEditText;
	private EditText weightEditText;
	private TextView dailyTargetTextView;
	private TextView bmiNumberTextView;
	private TextView bmiExplanationTextView;
	private ImageButton editImageButton;

	PersianCalendar calendar = new PersianCalendar();
//	Calendar cal = Calendar.getInstance();

	PersianDatePickerDialog persianDatePickerDialog = PersianDatePickerDialog.newInstance(this, calendar.get(PersianCalendar.YEAR), calendar.get(PersianCalendar.MONTH), calendar.get(PersianCalendar.DAY_OF_MONTH), true, PersianDatePickerDialog.PERSIAN);
//	PersianDatePickerDialog datePickerDialog = PersianDatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), true, PersianDatePickerDialog.GREGORIAN);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		bmiExplanations = getResources().getStringArray(R.array.bmi_explanations);
		
		genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
		dateOfBirthEditText = (EditText) findViewById(R.id.date_of_birth_edit_text);
		heightEditText = (EditText) findViewById(R.id.height_edit_text);
		weightEditText = (EditText) findViewById(R.id.weight_edit_text);
		dailyTargetTextView = (TextView) findViewById(R.id.calorie_goal_text_view_profile);
		bmiNumberTextView = (TextView) findViewById(R.id.bmi_number);
		bmiExplanationTextView = (TextView) findViewById(R.id.bmi_explanation);
		editImageButton = (ImageButton) findViewById(R.id.calorie_target_edit_button);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			    this, R.layout.spinner_rtl_item, getResources().getStringArray(R.array.gender_list));

		((Spinner) findViewById(R.id.gender_spinner)).setAdapter(adapter);
		
		loadUserDataFromDevice();
		
		if(connectedToNetwork())
			loadUserDataFromServer();
		
		genderSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						User.setGender(genderSpinner.getSelectedItemPosition());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		
		dateOfBirthEditText.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						persianDatePickerDialog.setYearRange(Constants.FIRST_YEAR_BIRTHDAY, calendar.get(Calendar.YEAR));
						persianDatePickerDialog.setVibrate(false);
						persianDatePickerDialog.setCloseOnSingleTapDay(false);
						persianDatePickerDialog.show(getSupportFragmentManager(), "date");						
					}
				});
		
		heightEditText.addTextChangedListener(
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
						if(!TextUtils.isEmpty(s))
						{
							User.setHeight(Integer.parseInt(s.toString()));
							if(User.getHeight() != 0)
							{
								float bmi = (float)User.getWeight()/(float)User.getHeight()/(float)User.getHeight()*10000;
								bmiNumberTextView.setText(Utils.toPersianNumbers(String.format("%.1f", bmi)));
								bmiExplanationTextView.setText(bmiExplanations[getBmiStringInex(bmi)]);
							}
						}
					}
				});
		
		weightEditText.addTextChangedListener(
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
						if(!TextUtils.isEmpty(s))
						{
							User.setWeight(Integer.parseInt(s.toString()));
							float bmi = (float)User.getWeight()/(float)User.getHeight()/(float)User.getHeight()*10000;
							bmiNumberTextView.setText(Utils.toPersianNumbers(String.format("%.1f", bmi)));
							bmiExplanationTextView.setText(bmiExplanations[getBmiStringInex(bmi)]);
						}
					}
				});

		editImageButton.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v)
					{
						if(checkForIncompleteFields())
							return;

						startActivity(new Intent(ProfileActivity.this, TargetCalculatorActivity.class));
						overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					}
				});
		dailyTargetTextView.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v)
					{
						startActivity(new Intent(ProfileActivity.this, TargetCalculatorActivity.class));
						overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					}
				});
	}

	@Override
	protected void onResume()
	{
		dailyTargetTextView.setText(Utils.toPersianNumbers(""+User.getDailyTarget())+"");
		super.onResume();
	}
	private boolean checkForIncompleteFields()
	{
		if(TextUtils.isEmpty(dateOfBirthEditText.getText()))
		{
			dateOfBirthEditText.setError(getString(R.string.error_enter_date_of_birth));
			dateOfBirthEditText.requestFocus();
			return true;
		}
		dateOfBirthEditText.setError(null);
		if(TextUtils.isEmpty(heightEditText.getText()))
		{
			heightEditText.setError(getString(R.string.error_enter_height));
			heightEditText.requestFocus();
			return true;
		}
		heightEditText.setError(null);
		if(TextUtils.isEmpty(weightEditText.getText()))
		{
			weightEditText.setError(getString(R.string.error_enter_weight));
			weightEditText.requestFocus();
			return true;
		}
		weightEditText.setError(null);
		return false;
	}

	private void loadUserDataFromDevice()
	{
		SharedPreferences userData = getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
		int day, month, year, height, weight, gender, dailyTarget;
		day = userData.getInt(Constants.DAY_OF_BIRTH_KEY, 0);
		month = userData.getInt(Constants.MONTH_OF_BIRTH_KEY, 0);
		year = userData.getInt(Constants.YEAR_OF_BIRTH_KEY, 0);
		gender = "female".equalsIgnoreCase(userData.getString(Constants.GENDER_KEY, ""))?1:0; 
		height = userData.getInt(Constants.HEIGHT_KEY, 0);
		weight = userData.getInt(Constants.WEIGHT_KEY, 0);
		dailyTarget = userData.getInt(Constants.DAILY_TARGET, 0);
		
		genderSpinner.setSelection(gender);
		if(year != 0)
			dateOfBirthEditText.setText(new Date(year, month, day).getString());
		heightEditText.setText(Utils.toPersianNumbers(height==0?"":height+"")+"");
		weightEditText.setText(Utils.toPersianNumbers(weight==0?"":weight+"")+"");
		dailyTargetTextView.setText(Utils.toPersianNumbers(dailyTarget==0?"":dailyTarget+"")+"");
		if(weight == 0 || height == 0)
		{
			bmiNumberTextView.setText(Utils.toPersianNumbers("0.0"));
			bmiExplanationTextView.setText("");
		}
		else
		{
			float bmi = (float)weight/(float)height/(float)height*10000;
			bmiNumberTextView.setText(Utils.toPersianNumbers(String.format("%.1f", bmi))+"");
			bmiExplanationTextView.setText(bmiExplanations[getBmiStringInex(bmi)]);
		}
		
		User.setDailyTarget(dailyTarget);
		User.setDayOfBirth(day);
//		User.setEmail(userData.getString(Constants.EMAIL_KEY, ""));
		User.setGender(gender);
		User.setHeight(height);
		User.setWeight(weight);
		User.setMonthOfbirth(month);
		User.setYearOfBirth(year);
	}

	protected int getBmiStringInex(float bmi) {
		if(bmi < 15)
			return 0;
		else if(bmi < 16)
			return 1;
		else if(bmi < 18.5)
			return 2;
		else if(bmi < 25)
			return 3;
		else if(bmi < 30)
			return 4;
		else if(bmi < 35)
			return 5;
		else if(bmi < 40)
			return 6;
		else if(bmi >= 45)
			return 7;
		return 0;
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@SuppressLint("NewApi") @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			if(checkForIncompleteFields())
				return true;
			saveUserDataOnDevice();
			saveUserDataOnServer();
			finish();
			Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDateSet(PersianDatePickerDialog datePickerDialog, int year,
			int month, int day) {
		User.setDayOfBirth(day);
		User.setMonthOfbirth(month+1);
		User.setYearOfBirth(year);
		
		dateOfBirthEditText.setText(new Date(year, month+1, day).getString());
	}
	
//	private String dateString(int day, int month, int year)
//	{
//		if(day == 0 && month == 0 && year == 0)
//			return "";
//		return String.format("%02d", day)+"/"+String.format("%02d", month+1)+"/"+String.format("%4d", year);
//	}

	private void saveUserDataOnDevice()
	{
		SharedPreferences userData = getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
		SharedPreferences.Editor editor = userData.edit();
		
		editor.putString(Constants.EMAIL_KEY, User.getEmail());
		editor.putInt(Constants.HEIGHT_KEY, User.getHeight());
		editor.putInt(Constants.WEIGHT_KEY, User.getWeight());
		editor.putInt(Constants.DAY_OF_BIRTH_KEY, User.getDayOfBirth());
		editor.putInt(Constants.MONTH_OF_BIRTH_KEY, User.getMonthOfbirth());
		editor.putInt(Constants.YEAR_OF_BIRTH_KEY, User.getYearOfBirth());
		editor.putInt(Constants.DAILY_TARGET, User.getDailyTarget());
		editor.putString(Constants.GENDER_KEY, User.getGender().toString());

		editor.commit();
		
		ConsumptionDatabaseHelper dbHelper = new ConsumptionDatabaseHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DailyConsumptionEntry.COLUMN_NAME_TARGET, User.getDailyTarget());
		
		String today = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE)).getSTDString();
		String selection = DailyConsumptionEntry.COLUMN_NAME_DATE + " = ?";
		String[] selectionArgs = { today };

		int count = db.update(
		    DailyConsumptionEntry.TABLE_NAME,
		    values,
		    selection,
		    selectionArgs);

		if(count == 0)
		{
			db = dbHelper.getWritableDatabase();
			values = new ContentValues();
			values.put(DailyConsumptionEntry.COLUMN_NAME_TARGET, User.getDailyTarget());
			values.put(DailyConsumptionEntry.COLUMN_NAME_DATE, today);
			values.put(DailyConsumptionEntry.COLUMN_NAME_CONSUMED, 0);
			
			db.insert(DailyConsumptionEntry.TABLE_NAME, null, values);
		}
		db.close();
	}
	
	private void saveUserDataOnServer()
	{
		//TODO
	}

	private void loadUserDataFromServer()
	{
		// TODO Auto-generated method stub
	}

	private boolean connectedToNetwork()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
