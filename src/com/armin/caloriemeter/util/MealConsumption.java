package com.armin.caloriemeter.util;

import java.io.Serializable;

public class MealConsumption implements Serializable {
	private static final long serialVersionUID = 6887079905308222505L;
	private String name;
	private int energy;
	private int amount;
	private String unit;
	private Meal meal;
	private Time time;
	private Date date;
	
	
	public MealConsumption(String name, int energy, int amount, String unit, int meal, Time time, Date date)
	{
		this.name = name;
		this.energy = energy;
		this.amount = amount;
		this.unit = unit;
		this.setMeal(meal);
		this.time = time;
		this.date = date;
	}

	public MealConsumption()
	{
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEnergy() {
		return energy;
	}
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Meal getMeal() {
		return meal;
	}
	
	public int getMealNumeral()
	{
		switch (meal) {
		case BREAKFAST: return 0;
		case LUNCH: return 1;
		case DINNER: return 2;
		case SNACK: return 3;
		}
		return 0;
	}
	public void setMeal(Meal meal) {
		this.meal = meal;
	}
	public void setMeal(int meal) {
		switch(meal)
		{
		case 0:
			this.meal = Meal.BREAKFAST;
			break;
		case 1:
			this.meal = Meal.LUNCH;
			break;
		case 2:
			this.meal = Meal.DINNER;
			break;
		case 3:
			this.meal = Meal.SNACK;
			break;
		}
	}
	public String getTimeString() {
		return time.toString();
	}
	
	public Time getTime()
	{
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
