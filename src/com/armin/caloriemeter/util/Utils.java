package com.armin.caloriemeter.util;

import java.util.HashMap;

public class Utils
{
	private static HashMap<Character, Character> persianToEnglish;
	private static void initialize()
	{
		persianToEnglish = new HashMap<Character, Character>();
		persianToEnglish.put('۰', '0');
		persianToEnglish.put('۱', '1');
		persianToEnglish.put('۲', '2');
		persianToEnglish.put('۳', '3');
		persianToEnglish.put('۴', '4');
		persianToEnglish.put('۵', '5');
		persianToEnglish.put('۶', '6');
		persianToEnglish.put('۷', '7');
		persianToEnglish.put('۸', '8');
		persianToEnglish.put('۹', '9');
	}
	
	public static String toPersianNumbers(String str)
    {
    	char[] persianNumbers = {'\u06F0', '\u06F1', '\u06F2', '\u06F3', '\u06F4', '\u06F5', '\u06F6', '\u06F7', '\u06F8', '\u06F9'};
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i<str.length(); i++)
    	{
    	    if(Character.isDigit(str.charAt(i)))
    	    {
    	        builder.append(persianNumbers[(int)(str.charAt(i))-48]);
    	    }
    	    else
    	    {
    	        builder.append(str.charAt(i));
    	    }
    	}
    	
    	return builder.toString();
    }
    
	public static String toEnglishNumbers(String str)
    {
		if(persianToEnglish == null)
			initialize();
    	String persianNumbers = "۱۲۳۴۵۶۷۸۹۰";
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < str.length(); i++)
    	{
    	    if(persianNumbers.contains(str.charAt(i)+""))
    	    {
    	        builder.append(persianToEnglish.get(str.charAt(i)));
    	    }
    	    else
    	    {
    	        builder.append(str.charAt(i));
    	    }
    	}
    	
    	return builder.toString();
    }
    
}
