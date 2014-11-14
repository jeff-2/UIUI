package edu.illinois.engr.web.cs465uiui.utils;

import java.util.Calendar;
import static java.util.Calendar.*;

public class Time
{
	/**Puts a string in the format h:m (24-hour time, as usual) into a Calendar without changing the date.*/
	public static void putTime(String time, Calendar calendar)
	{
		String[] halves = time.split(":");
		if(halves.length < 2)
			throw new IllegalArgumentException(halves.length + "");
		calendar.set(HOUR_OF_DAY, Integer.parseInt(halves[0]));
		calendar.set(MINUTE, Integer.parseInt(halves[1]));
	}
	
	
	/**Get the number of minutes since midnight, ignoring things like daylight savings time.*/
	public static int minutes(Calendar cal)
	{
		return cal.get(HOUR_OF_DAY) * 60 + cal.get(MINUTE);
	}
	
	/**Turns a number of minutes since midnight into a calendar.*/
	public static Calendar fromMinutes(int minutes)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(HOUR_OF_DAY, minutes / 60);
		cal.set(MINUTE, minutes % 60);
		return cal;
	}
}
