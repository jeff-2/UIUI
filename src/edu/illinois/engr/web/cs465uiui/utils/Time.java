package edu.illinois.engr.web.cs465uiui.utils;

import java.util.Calendar;

public class Time
{
	/**Puts a string in the format h:m (24-hour time, as usual) into a Calendar without changing the date.*/
	public static void putTime(String time, Calendar calendar)
	{
		String[] halves = time.split(":");
		if(halves.length < 2)
			throw new IllegalArgumentException(halves.length + "");
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(halves[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(halves[1]));
	}
}
