package com.armin.caloriemeter.adapters;

import java.util.ArrayList;

import com.armin.caloriemeter.R;
import com.armin.caloriemeter.util.MealConsumption;
import com.armin.caloriemeter.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IngredientsAdapter extends ArrayAdapter<MealConsumption> {

	ArrayList<MealConsumption> meals;
	TextView energyEditText;
	public IngredientsAdapter(Context context, ArrayList<MealConsumption> meals, TextView energyEditText) {
		super(context, 0);
		this.meals = meals;
		this.energyEditText = energyEditText;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingridient_item, parent, false);
		}
		
		if(position >= getCount())
			return convertView;
		if(meals.get(position) == null)
			return convertView;

		((ImageView)convertView.findViewById(R.id.delete_image))
		.setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v)
					{
						float energy = Float.parseFloat(Utils.toEnglishNumbers(energyEditText.getText().toString()));
						int removedEnergy = meals.get(position).getEnergy();
						energyEditText.setText(Utils.toPersianNumbers((energy-removedEnergy)+"")+"");
						meals.remove(position);
						notifyDataSetChanged();
					}
				});

		((TextView)convertView.findViewById(R.id.ingridient_name_text_view))
		.setText(meals.get(position).getName());
		((TextView)convertView.findViewById(R.id.ingridient_amount))
		.setText(Utils.toPersianNumbers(""+meals.get(position).getAmount())+"");
		((TextView)convertView.findViewById(R.id.ingridient_unit))
		.setText(meals.get(position).getUnit());

		return convertView;
	}

	@Override
	public int getCount() {
		return meals.size();
	}
}
