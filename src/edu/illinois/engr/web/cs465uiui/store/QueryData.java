package edu.illinois.engr.web.cs465uiui.store;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import edu.illinois.engr.web.cs465uiui.Query;

/**Deals with saving and loading the user's current query.*/
public class QueryData
{
	/**The name of our shared preferences file.*/
	private static final String PREFS_NAME = "prefs-query";
	/**SharedPreferences keys.*/
	private static final String KEY_TIME = "query_time", KEY_TAGS = "query_tags",
			KEY_POSITION = "query_position", KEY_RADIUS = "query_radius";
	
	
	/**Null when not loaded.*/
	private static Query query = null;
	
	
	/**Save a query to storage.*/
	public static void save(Query query, Context context)
	{
		SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
		if(query.time == null)
			edit.remove(KEY_TIME);
		else
			edit.putLong(KEY_TIME, query.time.getTimeInMillis());
		//TODO tags
		
		//TODO
		edit.apply();
	}
	
	
	/**Load the last saved query from storage.*/
	public static Query load(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Query query = new Query();
		if(prefs.contains(KEY_TIME))
		{
			query.time = Calendar.getInstance();
			query.time.setTimeInMillis(prefs.getLong(KEY_TIME, -1));
		}
		
		//TODO
		return query;
	}
}
