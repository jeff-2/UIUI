package edu.illinois.engr.web.cs465uiui.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;

import edu.illinois.engr.web.cs465uiui.Restaurant;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.net.Fetch;
import edu.illinois.engr.web.cs465uiui.net.ServerResult;

/**Calls through to the Fetch class for server communication,
 * but catches exceptions and puts them in ServerResults.
 * Also has a helper function to communicate errors to the user.*/
public class UIFetch
{
	public static ServerResult<List<Tag>> allTags()
	{
		try{return new ServerResult<>(Fetch.allTags());}
		catch(JSONException | IOException ex)
		{return new ServerResult<>(ex);}
	}
	
	
	public static ServerResult<List<Float>> crowdednessOn(Calendar date, long restaurantID)
	{
		try{return new ServerResult<>(Fetch.crowdednessOn(date, restaurantID));}
		catch(JSONException | IOException ex)
		{return new ServerResult<>(ex);}
	}
	
	
	public static ServerResult<Restaurant> restaurant(long id)
	{
		try{return new ServerResult<>(Fetch.restaurant(id));}
		catch(JSONException | IOException ex)
		{return new ServerResult<>(ex);}
	}
	
	
	public static ServerResult<List<Restaurant>> restaurants(List<Long> ids)
	{
		try{return new ServerResult<>(Fetch.restaurants(ids));}
		catch(JSONException | IOException ex)
		{return new ServerResult<>(ex);}
	}
	
	
	
	/**Launches a dialog notifying the user about a request failure.
	 * @param error the error the fetch function returned.*/
	public static void explainError(Exception error, Activity activity)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(activity).setTitle("Connection Failure");
		if(error instanceof JSONException)
			build.setMessage("There was an internal error while reading the server's response.");
		else if(error instanceof IOException)
			build.setMessage("Could not contact the server successfully--are you connected to the internet?");
		else
			build.setMessage("Something unexpected went wrong.");
		build.create().show();
	}
}
