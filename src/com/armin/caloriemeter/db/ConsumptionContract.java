package com.armin.caloriemeter.db;

import com.armin.caloriemeter.Constants;

import android.provider.BaseColumns;

public final class ConsumptionContract
{
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public ConsumptionContract()
	{
	}

	/* Inner class that defines the table contents */
	
	public static abstract class FoodEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "foods";
		public static final String COLUMN_NAME_FOOD_NAME = "food_name";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String COLUMN_NAME_UNIT = "unit";
		public static final String COLUMN_NAME_ENERGY = "energy";
		
	}
	
	public static abstract class ConsumptionEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "consumption";
		public static final String COLUMN_NAME_DATE = "consumption_date";
		public static final String COLUMN_NAME_TIME = "consumption_time";
		public static final String COLUMN_NAME_AMOUNT = "consumption_amount";
		public static final String COLUMN_NAME_ENERGY = "consumption_energy";
		public static final String COLUMN_NAME_MEAL = "consumption_meal";
		public static final String COLUMN_NAME_UNIT = "consumption_unit";
		public static final String COLUMN_NAME_FOOD_NAME = "consumption_food_name";
	}
	
	public static abstract class DailyConsumptionEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "daily_consumption";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_TARGET = "target";
		public static final String COLUMN_NAME_CONSUMED = "consumed";
	}

	private static final String INTEGER_TYPE = " INTEGER";
	private static final String MEAL_TYPE = " INTEGER";
	private static final String DATE_TYPE = " TEXT";
	private static final String TIME_TYPE = " TEXT";
	private static final String TEXT_TYPE = " TEXT";
	private static final String FLOAT_TYPE = " REAL"; 
	public static final String SQL_CREATE_CONSUMPTION =
			"CREATE TABLE IF NOT EXISTS " + ConsumptionEntry.TABLE_NAME + " (\n" +
					ConsumptionEntry._ID + " INTEGER PRIMARY KEY,\n" +
					ConsumptionEntry.COLUMN_NAME_FOOD_NAME + TEXT_TYPE+ ",\n" +
					ConsumptionEntry.COLUMN_NAME_DATE + DATE_TYPE + ",\n" +
					ConsumptionEntry.COLUMN_NAME_AMOUNT + INTEGER_TYPE + ",\n" +
					ConsumptionEntry.COLUMN_NAME_ENERGY + INTEGER_TYPE+ ",\n" +
					ConsumptionEntry.COLUMN_NAME_MEAL + MEAL_TYPE + ",\n" +
					ConsumptionEntry.COLUMN_NAME_UNIT + TEXT_TYPE + ",\n" +
					ConsumptionEntry.COLUMN_NAME_TIME + TIME_TYPE +
					" )";

	public static final String SQL_DELETE_CONSUMPTION =
			"DROP TABLE IF EXISTS " + ConsumptionEntry.TABLE_NAME;
	
	public static final String SQL_CREATE_DAILY_CONSUMPTION =
			"CREATE TABLE IF NOT EXISTS " + DailyConsumptionEntry.TABLE_NAME + " (\n" +
					DailyConsumptionEntry._ID + " INTEGER PRIMARY KEY,\n" +
					DailyConsumptionEntry.COLUMN_NAME_DATE + DATE_TYPE + ",\n" +
					DailyConsumptionEntry.COLUMN_NAME_CONSUMED + INTEGER_TYPE + ",\n" +
					DailyConsumptionEntry.COLUMN_NAME_TARGET+ TEXT_TYPE +
					" )";

	public static final String SQL_DELETE_DAILY_CONSUMPTION =
			"DROP TABLE IF EXISTS " + DailyConsumptionEntry.TABLE_NAME;

	public static final String SQL_CREATE_FOODS =
			"CREATE TABLE IF NOT EXISTS " + FoodEntry.TABLE_NAME + " (\n" +
					FoodEntry.COLUMN_NAME_FOOD_NAME + TEXT_TYPE + ",\n" +
					FoodEntry.COLUMN_NAME_AMOUNT + FLOAT_TYPE + ",\n" +
					FoodEntry.COLUMN_NAME_UNIT + TEXT_TYPE + ",\n" +
					FoodEntry.COLUMN_NAME_ENERGY + FLOAT_TYPE +",\n"+
					"PRIMARY KEY ("+FoodEntry.COLUMN_NAME_FOOD_NAME+","+FoodEntry.COLUMN_NAME_UNIT+")"+
					" )";
	public static final String SQL_INITIALIZE_FOODS = Constants.INSERT_INTO_FOODS;

	public static final String SQL_DELETE_FOODS =
			"DROP TABLE IF EXISTS " + FoodEntry.TABLE_NAME;

}
