package com.armin.caloriemeter;

import java.io.Serializable;

public class Date implements Serializable{
	private static final long serialVersionUID = 7726494832857697641L;
	public int year;
	public int month;
	public int day;
	
	public Date(int year, int month, int day)
	{
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	@Override
	public String toString()
	{
		return ""+year+""+month+""+day;
	}

	public String getString()
	{
		return year+"/"+month+"/"+day;
	}
	
	public String getSTDString()
	{
		if(month < 10 && day < 10)
			return year+"0"+month+"0"+day;
		if(month < 10)
			return year+"0"+month+""+day;
		if(day < 10)
			return year+""+month+"0"+day;
		return year+""+month+""+day;
	}
}
