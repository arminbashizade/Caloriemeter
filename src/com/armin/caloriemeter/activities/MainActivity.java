package com.armin.caloriemeter.activities;

import java.util.Calendar;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.notification.NotificationReceiver;
import com.armin.caloriemeter.util.Constants;
import com.fourmob.datetimepicker.date.PersianCalendar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class MainActivity extends Activity {

	PendingIntent pendingIntent; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(PersianCalendar.HOUR_OF_DAY, 22);
		calendar.set(PersianCalendar.MINUTE, 0);
		calendar.set(PersianCalendar.SECOND, 0);
		
		Intent myIntent = new Intent(MainActivity.this, NotificationReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pendingIntent);

		if(isLoggedIn())
		{
			startActivity(new Intent(MainActivity.this, HistoryActivity.class));
		}
		else
		{
			startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//			startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
//		return !("invalid".equals(userData.getString(Constants.EMAIL_KEY, "invalid"))
//				|| TextUtils.isEmpty(userData.getString(Constants.EMAIL_KEY, "invalid")));
		return userData.getInt(Constants.DAILY_TARGET, -1) != -1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
