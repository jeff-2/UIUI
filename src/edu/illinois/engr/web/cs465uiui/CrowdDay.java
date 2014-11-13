package edu.illinois.engr.web.cs465uiui;

import java.util.*;
import static java.util.Calendar.*;

import android.util.Pair;

/**Represents a day of crowdedness levels at a restaurant.*/
public class CrowdDay
{
	/**The opening and closing times today.*/
	public Calendar open, close;
	/**The predicted crowdedness values for this restaurant at different times throughout the day.*/
	public List<Pair<Calendar, Float>> values;
	
	
	public CrowdDay(Calendar open, Calendar close, List<Pair<Calendar, Float>> values)
	{
		this.open = open;
		this.close = close;
		this.values = values;
	}
	
	
	/**Returns the value that occurs closest to the provided time.
	 * Values must be sorted for this to work, because I'm going to implement binary search in the future.
	 * XXX should use binary search.*/
	public Pair<Calendar, Float> closestToTime(int hour, int minute)
	{
		int minDiff = Integer.MAX_VALUE;
		Pair<Calendar, Float> minValue = null;
		for(Pair<Calendar, Float> value : values)
		{
			int diff = Math.abs(value.first.get(HOUR_OF_DAY) * 60 + value.first.get(MINUTE));
			if(diff < minDiff)
			{
				minDiff = diff;
				minValue = value;
			}
		}
		
		return minValue;
	}
	
	
	/**Comparator for sorting the values list by times.*/
	public static final Comparator<Pair<Calendar, Float>> compareByTime = new Comparator<Pair<Calendar,Float>>()
	{
		@Override public int compare(Pair<Calendar,Float> a, Pair<Calendar,Float> b)
		{
			return a.first.compareTo(b.first);
		}
	};
}
