package com.armin.caloriemeter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConsumptionDatabaseHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "caloriemeter.db";
	
	public ConsumptionDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(ConsumptionContract.SQL_CREATE_FOODS);
		db.execSQL(ConsumptionContract.SQL_INITIALIZE_FOODS);
		db.execSQL(ConsumptionContract.SQL_CREATE_DAILY_CONSUMPTION);
		db.execSQL(ConsumptionContract.SQL_CREATE_CONSUMPTION);
	}

	public void deleteDatabase(SQLiteDatabase db)
	{
		db.execSQL(ConsumptionContract.SQL_DELETE_CONSUMPTION);
		db.execSQL(ConsumptionContract.SQL_DELETE_DAILY_CONSUMPTION);
		db.execSQL(ConsumptionContract.SQL_CREATE_CONSUMPTION);
		db.execSQL(ConsumptionContract.SQL_CREATE_DAILY_CONSUMPTION);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//TODO!!
		db.execSQL(ConsumptionContract.SQL_DELETE_CONSUMPTION);
		db.execSQL(ConsumptionContract.SQL_DELETE_DAILY_CONSUMPTION);
		onCreate(db);
	}
}
