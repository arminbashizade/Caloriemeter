package com.armin.caloriemeter.adapters;

import com.armin.caloriemeter.Constants;
import com.armin.caloriemeter.R;
import com.armin.caloriemeter.activities.LoginActivity;
import com.armin.caloriemeter.activities.ProfileActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class NavigationAdapter extends ArrayAdapter<String> {

	private static final int PROFILE = 0;
	private static final int LOG_OFF = 1;

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
       // Lookup view for data population
       Button menuItem = (Button) convertView.findViewById(R.id.navigarion_drawer_item_button);
       // Populate the data into the template view using the data object
       menuItem.setText(items[position]);
       menuItem.setOnClickListener(new OnClickListener() {
		
    	   @Override
    	   public void onClick(View v) {
    		   String text = (String) ((Button)v).getText();
    		   int position = items[0].equals(text)?0:1;
    		   switch(position)
				{
				case PROFILE:
					context.startActivity(new Intent(context, ProfileActivity.class));
					break;
				case LOG_OFF:
					context.getSharedPreferences(Constants.USER_SHARED_PREFERENCES, 0).edit().clear().commit();
					activity.startActivity(new Intent(context, LoginActivity.class));
					activity.finish();
					break;
				}
				drawerLayout.closeDrawer(drawerList);
    	   }
       });
       // Return the completed view to render on screen
       return convertView;
   }
}