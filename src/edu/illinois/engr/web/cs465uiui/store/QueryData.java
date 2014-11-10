package edu.illinois.engr.web.cs465uiui.store;

import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.Tag;

/**Deals with saving and loading the user's current query.*/
public class QueryData
{
	/**The name of our shared preferences file.*/
	private static final String PREFS_NAME = "prefs-query";
	/**SharedPreferences keys.*/
	private static final String KEY_TIME = "query_time", KEY_TAGS = "query_tags", KEY_ALL_TAGS = "query_alltags",
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
		
		Set<String> stringTags = new HashSet<>();
		for(Tag tag : query.tags)
			stringTags.add(tag.name);
		edit.putStringSet(KEY_TAGS, stringTags);
		
		edit.putBoolean(KEY_ALL_TAGS, query.allTags);
		edit.putString(KEY_POSITION, query.position);
		edit.putInt(KEY_RADIUS, query.radiusMiles);
		
		edit.apply();
		QueryData.query = query.clone();
	}
	
	
	/**Loads the last saved query from storage, or returns the cached version in memory if it's available.*/
	public static Query load(Context context)
	{
		if(query != null)
			return query;
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Query query = new Query();
		
		if(prefs.contains(KEY_TIME))
		{
			query.time = Calendar.getInstance();
			query.time.setTimeInMillis(prefs.getLong(KEY_TIME, -1));
		}
		
		Set<String> tags = prefs.getStringSet(KEY_TAGS, new HashSet<String>());
		for(String tagName : tags)
			query.tags.add(new Tag(tagName));
		
		query.allTags = prefs.getBoolean(KEY_ALL_TAGS, query.allTags);
		query.position = prefs.getString(KEY_POSITION, query.position);
		query.radiusMiles = prefs.getInt(KEY_RADIUS, query.radiusMiles);
		
		return query;
	}
}
