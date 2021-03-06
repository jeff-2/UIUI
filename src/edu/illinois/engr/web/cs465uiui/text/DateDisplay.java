package edu.illinois.engr.web.cs465uiui.text;

import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.*;

/**Date display is standardized throughout the app via this class.*/
public class DateDisplay
{
	/**Display a date and time with full detail.*/
	public static String full(Calendar cal)
	{
		return cal.get(DATE) + " " + cal.getDisplayName(MONTH, SHORT, Locale.getDefault()) + " " +
				cal.get(YEAR) + " @ " + cal.get(HOUR_OF_DAY) + ":" + String.format("%02d", cal.get(MINUTE));
	}
	
	
	/**Displays the date only, and leaves out the year.*/
	public static String dateShort(Calendar cal)
	{
		return cal.getDisplayName(MONTH, SHORT, Locale.getDefault()) + " " + cal.get(DATE);
	}
	
	
	/**Displays the time only.*/
	public static String time(Calendar cal)
	{
		return cal.get(HOUR_OF_DAY) + ":" + String.format("%02d", cal.get(MINUTE));
	}
}
