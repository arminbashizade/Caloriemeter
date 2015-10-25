package com.armin.caloriemeter.adapters;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.activities.MainActivity;
import com.armin.caloriemeter.activities.ProfileActivity;
import com.armin.caloriemeter.db.ConsumptionDatabaseHelper;
import com.armin.caloriemeter.util.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class NavigationAdapter extends ArrayAdapter<String> implements OnClickListener{

	private static final int PROFILE = 0;
	private static final int LOG_OFF = 1;

	private ConsumptionDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	private String[] items;
	private Context context;
	private Activity activity;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	public NavigationAdapter(Activity activity, Context context, DrawerLayout drawerLayout,
			ListView drawerList, String[] navigationMenuItems)
	{
		super(context, 0, navigationMenuItems);
		this.items = navigationMenuItems;
		this.context = context;
		this.activity = activity;
		this.drawerLayout = drawerLayout;
		this.drawerList = drawerList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
		}
		//TODO change icons
		// Lookup view for data population
		ImageView icon = (ImageView) convertView.findViewById(R.id.navigarion_drawer_item_button_image);
		Button menuItem = (Button) convertView.findViewById(R.id.navigarion_drawer_item_button);
		// Populate the data into the template view using the data object
		menuItem.setText(items[position]);
		icon.setTag(position);
		menuItem.setTag(position);
		if(position == LOG_OFF)
			icon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_action_delete));
		menuItem.setOnClickListener(this);
		icon.setOnClickListener(this);
		// Return the completed view to render on screen
		return convertView;
	}
	@Override
	public void onClick(View v) {
		int position = (Integer)v.getTag();
		switch(position)
		{
		case PROFILE:
			context.startActivity(new Intent(context, ProfileActivity.class));
			break;
		case LOG_OFF:
			dbHelper = new ConsumptionDatabaseHelper(getContext());
			db = dbHelper.getReadableDatabase();
			dbHelper.deleteDatabase(db);

			context.getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0).edit().clear().commit();
			Intent intent = new Intent(context, MainActivity.class);
			activity.finish();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			activity.startActivity(intent);
			break;
		}
		drawerLayout.closeDrawer(drawerList);
	}
}