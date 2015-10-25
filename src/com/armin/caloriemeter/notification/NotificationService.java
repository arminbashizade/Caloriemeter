package com.armin.caloriemeter.notification;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.activities.HistoryActivity;
import com.armin.caloriemeter.util.Constants;
import com.armin.caloriemeter.util.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;

public class NotificationService extends Service
{
	private NotificationManager notificationManager;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.getApplicationContext();
		notificationManager = (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent1 = new Intent(this.getApplicationContext(),HistoryActivity.class);

		PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

		//TODO set title and text
		SharedPreferences userData = getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0);
		int target = userData.getInt(Constants.DAILY_TARGET, 0);
		int consumed = userData.getInt(Constants.CONSUMED_KEY, 0);
		
		String goodJob = getResources().getString(R.string.good_job);
		String tooLow = getResources().getString(R.string.too_low);
		String tooHigh = getResources().getString(R.string.too_high);

		String title = null;
		String targetColon = getResources().getString(R.string.target);
		String consumedColon = getResources().getString(R.string.consumed);
		
		if(target - consumed > Constants.THRESHOLD)// too low
			title = tooLow;
		else if(consumed - target > Constants.THRESHOLD)// too high
			title = tooHigh;
		else // good job
			title = goodJob;
		
		String text = targetColon + " " + target + " " + consumedColon + " " + consumed; 
		
		Builder notification = new Builder(this)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle(title)
				.setContentText(Utils.toPersianNumbers(text))
				.setWhen(System.currentTimeMillis());  // the time stamp

		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notification.setAutoCancel(true);
		notification.setContentIntent(pendingNotificationIntent);

		notificationManager.notify(0, notification.build());
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
