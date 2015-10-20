package com.armin.caloriemeter.adapters;

import java.util.ArrayList;

import com.armin.caloriemeter.Meal;
import com.armin.caloriemeter.MealConsumption;
import com.armin.caloriemeter.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MealHistoryAdapter extends ArrayAdapter<MealConsumption> {

	ArrayList<MealConsumption> meals;
	public MealHistoryAdapter(Context context, ArrayList<MealConsumption> meals) {
		super(context, 0);
		this.meals = meals;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.meals_list_item, parent, false);
       }

       if(meals.get(position) == null)
    	   return convertView;
       
       Meal meal = meals.get(position).getMeal();
       String mealString = "";
       if(meal == Meal.BREAKFAST)
    	   mealString = convertView.getResources().getString(R.string.breakfast);
       else if(meal == Meal.DINNER)
    	   mealString = convertView.getResources().getString(R.string.dinner);
       else if(meal == Meal.LUNCH)
    	   mealString = convertView.getResources().getString(R.string.lunch);
       else
    	   mealString = convertView.getResources().getString(R.string.snack);
       
       ((TextView)convertView.findViewById(R.id.consumption_food_name))
       		.setText(meals.get(position).getName());
       ((TextView)convertView.findViewById(R.id.consumption_energy))
       		.setText(meals.get(position).getEnergy()+"");
       ((TextView)convertView.findViewById(R.id.consumption_amount))
       		.setText(meals.get(position).getAmount()+"");
       ((TextView)convertView.findViewById(R.id.consumption_unit))
       		.setText(meals.get(position).getUnit());
       ((TextView)convertView.findViewById(R.id.consumption_meal))
       		.setText(mealString);
       ((TextView)convertView.findViewById(R.id.consumption_time))
       		.setText(meals.get(position).getTimeString());
       
       return convertView;
   }
	
	@Override
	public int getCount() {
		return meals.size();
	}
}
