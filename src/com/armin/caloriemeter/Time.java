package com.armin.caloriemeter;

import java.io.Serializable;

import android.annotation.SuppressLint;

public class Time implements Serializable{
	private static final long serialVersionUID = -9059997315669800577L;
	public static final int AM = 0;
	public static final int PM = 1;
	public int hour;
	public int minute;
	int amPm;
	
//	public Time(int hour, int minute, int amPm) {
//		this.hour = hour;
//		this.minute = minute;
//		this.amPm = amPm;
//	}
	
	public Time(int hour, int minute)
	{
		this.minute = minute;
		this.hour = hour;
//		if(hour < 12)
//		{
//			this.amPm = AM;
//			if(hour == 0)
//				this.hour = 12;
//			else
//				this.hour = hour;
//		}
//		else
//		{
//			this.amPm = PM;
//			if(hour != 12)
//				this.hour -= 12;
//		}
	}

	public String getSTDString()
	{
//		int hour = this.hour;
//		if(hour == 12)
//			hour = 0;
//		
//		if(amPm == PM)
//		{
//			hour += 12;
//		}
		if(hour < 10 && minute < 10)
			return "0"+hour+"0"+minute;
		if(hour < 10)
			return "0"+hour+""+minute;
		if(minute < 10)
			return ""+hour+"0"+minute;
		return hour+""+minute;
	}

	@SuppressLint("DefaultLocale") @Override
	public String toString() {
		int hour = this.hour;
		if(hour < 12)
		{
			this.amPm = AM;
			if(hour == 0)
				hour = 12;
		}
		else
		{
			this.amPm = PM;
			if(hour != 12)
				hour -= 12;
		}

		return String.format("%d:%02d %s", hour, minute, amPm==AM?"am":"pm");
	}
}
