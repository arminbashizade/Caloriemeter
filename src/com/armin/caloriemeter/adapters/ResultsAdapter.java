package com.armin.caloriemeter.adapters;


import java.util.ArrayList;

import com.armin.caloriemeter.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultsAdapter extends ArrayAdapter<String> {

	ArrayList<String> results;
	public ResultsAdapter(Context context, ArrayList<String> results) {
		super(context, 0);
		this.results = results;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_row, parent, false);
       }

       TextView textView = (TextView) convertView.findViewById(R.id.result_text_view);
       if(position < getCount())
       textView.setText(results.get(position));
          
       return convertView;
   }
	
	@Override
	public int getCount() {
		return results.size();
	}
}
