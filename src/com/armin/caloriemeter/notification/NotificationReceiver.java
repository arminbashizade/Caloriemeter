package com.armin.caloriemeter.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent service = new Intent(context, NotificationService.class);
		context.startService(service);
	}

}
