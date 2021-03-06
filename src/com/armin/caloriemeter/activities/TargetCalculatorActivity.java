package com.armin.caloriemeter.activities;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.util.User;
import com.armin.caloriemeter.util.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

public class TargetCalculatorActivity extends Activity {

	private RadioGroup radioGroup;
	private TextView lifestyleLabel;
	private TextView dietGoalLabel;
	private EditText dailyTargetEditText;
	private TextView dailyTargetTextView;
	private Spinner lifestyleSpinner;
	private Spinner dietGoalSpinner;
	private TextView manualRadioButtonText;
	private TextView recommendedRadioButtonText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_calculator);
		// Show the Up button in the action bar.
		setupActionBar();

		
		dietGoalLabel = (TextView) findViewById(R.id.diet_goal_label);
		lifestyleLabel = (TextView) findViewById(R.id.lifestyle_label);
		dietGoalSpinner = (Spinner) findViewById(R.id.diet_goal_spinner);
		lifestyleSpinner = (Spinner) findViewById(R.id.lifestyle_spinner);
		dailyTargetEditText = (EditText) findViewById(R.id.daily_target_edit_text);
		dailyTargetTextView = (TextView) findViewById(R.id.daily_target_text_view);
		manualRadioButtonText = (TextView) findViewById(R.id.manual_radio_button_text_view);
		recommendedRadioButtonText = (TextView) findViewById(R.id.recommended_radio_button_text_view);
		
		lifestyleLabel.setVisibility(View.GONE);
		dietGoalLabel.setVisibility(View.GONE);
		lifestyleSpinner.setVisibility(View.GONE);
		dietGoalSpinner.setVisibility(View.GONE);
		dailyTargetTextView.setVisibility(View.GONE);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			    this, R.layout.spinner_rtl_item, getResources().getStringArray(R.array.diet_goal_list));

		dietGoalSpinner.setAdapter(adapter);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
			    this, R.layout.spinner_rtl_item, getResources().getStringArray(R.array.lifestyle_list));

		lifestyleSpinner.setAdapter(adapter1);
		
		User.setLifestyle(0);
		User.setDietGoal(0);
		
		radioGroup = (RadioGroup) findViewById(R.id.set_target_radio_group);
		radioGroup.setOnCheckedChangeListener(
				new RadioGroup.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if(checkedId == R.id.manual_radio_button)
						{
							dailyTargetEditText.setEnabled(true);
							lifestyleLabel.setVisibility(View.GONE);
							dietGoalLabel.setVisibility(View.GONE);
							lifestyleSpinner.setVisibility(View.GONE);
							dietGoalSpinner.setVisibility(View.GONE);
							dailyTargetTextView.setVisibility(View.GONE);
							dailyTargetEditText.setVisibility(View.VISIBLE);
							dailyTargetEditText.setText(dailyTargetTextView.getText());
							User.setDietGoal(3);
							User.setLifestyle(5);
							dailyTargetEditText.requestFocus();
						}
						else if(checkedId == R.id.recommended_radio_button)
						{
							dailyTargetEditText.setEnabled(false);
							lifestyleLabel.setVisibility(View.VISIBLE);
							dietGoalLabel.setVisibility(View.VISIBLE);
							lifestyleSpinner.setVisibility(View.VISIBLE);
							dietGoalSpinner.setVisibility(View.VISIBLE);
							dailyTargetTextView.setVisibility(View.VISIBLE);
							dailyTargetEditText.setVisibility(View.GONE);
							calculateDailyTarget();
							//TODO persian
							dailyTargetTextView.setText("1500");
						}
					}
				});

		manualRadioButtonText.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v)
					{
						((RadioButton) findViewById(R.id.manual_radio_button)).setChecked(true);
						((RadioButton) findViewById(R.id.recommended_radio_button)).setChecked(false);
					}
				});
		recommendedRadioButtonText.setOnClickListener(
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v)
					{
						((RadioButton) findViewById(R.id.manual_radio_button)).setChecked(false);
						((RadioButton) findViewById(R.id.recommended_radio_button)).setChecked(true);
					}
				});
		lifestyleSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						User.setLifestyle(lifestyleSpinner.getSelectedItemPosition());
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		
		dietGoalSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						User.setLifestyle(dietGoalSpinner.getSelectedItemPosition());
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});	
	}

	private void calculateDailyTarget()
	{
		// TODO Auto-generated method stub
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
		getMenuInflater().inflate(R.menu.target_calculator, menu);
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
		case R.id.action_save:
			if(radioGroup.getCheckedRadioButtonId() == R.id.manual_radio_button)
			{
				if(TextUtils.isEmpty(dailyTargetEditText.getText()))
				{
					Toast.makeText(getApplicationContext(), getString(R.string.error_enter_daily_target), Toast.LENGTH_SHORT).show();
					dailyTargetEditText.requestFocus();
					break;
				}
				User.setDailyTarget(Integer.parseInt(Utils.toEnglishNumbers(dailyTargetEditText.getText().toString())));
			}
			else if(radioGroup.getCheckedRadioButtonId() == R.id.recommended_radio_button)
			{
				User.setDailyTarget(Integer.parseInt(Utils.toEnglishNumbers(dailyTargetTextView.getText().toString())));
			}

			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	}
}
