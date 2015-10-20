package com.armin.caloriemeter.activities;

import com.armin.caloriemeter.Constants;
import com.armin.caloriemeter.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(isLoggedIn())
		{
			startActivity(new Intent(MainActivity.this, HistoryActivity.class));
		}
		else
		{
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
		}
	}

	@Override
	protected void onResume() {
		finish();
		super.onResume();
	}
	@SuppressLint("NewApi") @Override
	public boolean onNavigateUpFromChild(Activity child) {
		finish();
		return super.onNavigateUpFromChild(child);
	}
	private boolean isLoggedIn()
	{
		SharedPreferences userData = getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
		return !("invalid".equals(userData.getString(Constants.EMAIL_KEY, "invalid"))
				|| TextUtils.isEmpty(userData.getString(Constants.EMAIL_KEY, "invalid")));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
