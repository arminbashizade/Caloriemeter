package com.armin.caloriemeter;

import android.content.Context;

public class User {

	private static String email;
	private static int height;
	private static int weight;
	private static int dayOfBirth;
	private static int monthOfbirth;
	private static int yearOfBirth;
	private static Gender gender;
	private static Lifestyle lifestyle;
	private static DietGoal dietGoal;
	private static int dailyTarget;
	
	public static String getEmail() {
		return email;
	}
	public static void setEmail(String email) {
		User.email = email;
	}
	public static int getHeight() {
		return height;
	}
	public static void setHeight(int height) {
		User.height = height;
	}
	public static int getWeight() {
		return weight;
	}
	public static void setWeight(int weight) {
		User.weight = weight;
	}
	public static Gender getGender() {
		return gender;
	}
	public static void setGender(Gender gender) {
		User.gender = gender;
	}
	public static void setGender(String gender, Context context) {
		if(context.getString(R.string.male).equals(gender))
			User.gender = Gender.MALE;
		else if(context.getString(R.string.female).equals(gender))
			User.gender = Gender.FEMALE;
	}
	public static Lifestyle getLifestyle() {
		return lifestyle;
	}
	public static void setLifestyle(Lifestyle lifestyle) {
		User.lifestyle = lifestyle;
	}
	public static void setLifestyle(String lifestyle, Context context) {
		if(context.getString(R.string.sedantry).equals(lifestyle))
			User.lifestyle = Lifestyle.SEDANTRY;
		else if(context.getString(R.string.lightly_active).equals(lifestyle))
			User.lifestyle = Lifestyle.LIGHTLY_ACTIVE;
		else if(context.getString(R.string.moderately_active).equals(lifestyle))
			User.lifestyle = Lifestyle.MODERATELY_ACTIVE;
		else if(context.getString(R.string.very_active).equals(lifestyle))
			User.lifestyle = Lifestyle.VERY_ACTIVE;
		else if(context.getString(R.string.extra_active).equals(lifestyle))
			User.lifestyle = Lifestyle.EXTRA_ACTIVE;
	}
	
	public static int getDailyTarget() {
		return dailyTarget;
	}
	public static void setDailyTarget(int dailyTarget) {
		User.dailyTarget = dailyTarget;
	}
	
	public static DietGoal getDietGoal() {
		return dietGoal;
	}
	public static void setDietGoal(DietGoal dietGoal) {
		User.dietGoal = dietGoal;
	}
	
	public static void setDietGoal(String dietGoal, Context context) {
		if(context.getString(R.string.lose_weight).equals(dietGoal))
			User.dietGoal = DietGoal.LOSE_WEIGHT;
		else if(context.getString(R.string.maintain_weight).equals(dietGoal))
			User.dietGoal = DietGoal.MAINTAIN_WEIGHT;
		else if(context.getString(R.string.gain_weight).equals(dietGoal))
			User.dietGoal = DietGoal.GAIN_WEIGHT;
	}
	public static void setGender(int gender) {
		switch(gender)
		{
		case 0:
			User.gender = Gender.MALE;
			break;
		case 1:
			User.gender = Gender.FEMALE;
			break;
		default:
			User.gender = Gender.MALE;
		}
	}
	
	public static void setLifestyle(int lifestyle) {
		switch(lifestyle)
		{
		case 0:
			User.lifestyle = Lifestyle.SEDANTRY;
			break;
		case 1:
			User.lifestyle = Lifestyle.LIGHTLY_ACTIVE;
			break;
		case 2:
			User.lifestyle = Lifestyle.MODERATELY_ACTIVE;
			break;
		case 3:
			User.lifestyle = Lifestyle.VERY_ACTIVE;
			break;
		case 4:
			User.lifestyle = Lifestyle.EXTRA_ACTIVE;
			break;
		default:
			User.lifestyle = Lifestyle.NOT_SET;
		}
	}
	
	public static void setDietGoal(int dietGoal)
	{
		switch(dietGoal)
		{
		case 0:
			User.dietGoal = DietGoal.LOSE_WEIGHT;
			break;
		case 1:
			User.dietGoal = DietGoal.MAINTAIN_WEIGHT;
			break;
		case 2:
			User.dietGoal = DietGoal.GAIN_WEIGHT;
			break;
		default:
			User.dietGoal = DietGoal.NOT_SET;
		}
	}
	public static int getDayOfBirth() {
		return dayOfBirth;
	}
	public static void setDayOfBirth(int dayOfBirth) {
		User.dayOfBirth = dayOfBirth;
	}
	public static int getMonthOfbirth() {
		return monthOfbirth;
	}
	public static void setMonthOfbirth(int monthOfbirth) {
		User.monthOfbirth = monthOfbirth;
	}
	public static int getYearOfBirth() {
		return yearOfBirth;
	}
	public static void setYearOfBirth(int yearOfBirth) {
		User.yearOfBirth = yearOfBirth;
	}
	
	public static String myToString() {
		return email+"\n"+gender.toString()+"\t"+ dayOfBirth+"/"+monthOfbirth+"/"+yearOfBirth
				+"\n"+height+"\t"+weight+"\n"+lifestyle.toString()+"\n"+dietGoal.toString()+"\t"+dailyTarget;
	}
}
