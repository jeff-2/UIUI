package edu.illinois.engr.web.cs465uiui.store;

import android.content.Context;
import edu.illinois.engr.web.cs465uiui.Query;

/**Deals with saving and loading the user's current query.*/
public class QueryData
{
	/**Null when not loaded.*/
	private static Query query = null;
	
	
	/**Save a query to storage.*/
	public static void save(Query query, Context context)
	{
		//TODO
	}
	
	/**Load the last saved query from storage.*/
	public static Query load(Context context)
	{
		//TODO
		return new Query();
	}
}
