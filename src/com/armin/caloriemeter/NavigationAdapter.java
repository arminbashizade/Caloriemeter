package com.armin.caloriemeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationAdapter extends ArrayAdapter<String> {
	private String[] items;
    public NavigationAdapter(Context context, String[] navigationMenuItems) {
       super(context, 0, navigationMenuItems);
       this.items = navigationMenuItems; 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
       }
       // Lookup view for data population
       TextView menuItem = (TextView) convertView.findViewById(R.id.navigarion_drawer_item_text_view);
       // Populate the data into the template view using the data object
       menuItem.setText(items[position]);
       // Return the completed view to render on screen
       return convertView;
   }
}