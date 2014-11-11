package edu.illinois.engr.web.cs465uiui.net;

import java.io.IOException;
import java.net.*;
import java.util.*;

import org.json.*;

import android.util.LruCache;

import edu.illinois.engr.web.cs465uiui.ComparisonEntry;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.Restaurant;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.utils.IOUtil;

/**Fetches simple sets of data from the server.*/
public class Fetch
{
	private static final String URL_TAGS = "http://cs465uiui.web.engr.illinois.edu/tags.php";
	
	
	/**Null when not loaded.*/
	private static List<Tag> tags = null;
	
	
	
	/**Fetches all tags from the server.*/
	public static List<Tag> allTags() throws IOException, JSONException
	{
		if(tags == null)
		{
			HttpURLConnection conn = (HttpURLConnection)new URL(URL_TAGS).openConnection();
			conn.setReadTimeout(2000);
			conn.connect();
			String str = IOUtil.readStream(conn.getInputStream());
			
			JSONArray root = new JSONArray(str);
			tags = new ArrayList<>();
			for(int c = 0; c < root.length(); c++)
				tags.add(new Tag(root.getJSONObject(c).getString("restaurantTag")));
		}
		return tags;
	}
	
	
	/**Gets the crowdedness of a restaurant on a date.
	 * Returns a list of crowd levels at evenly-spaced times throughout the day;
	 * if there are 5 results, they occur at midnight, 6:00, noon, 6:00 PM, and midnight.*/
	public static List<Float> crowdednessOn(Calendar date, long restaurantID) throws JSONException, IOException
	{
		//TODO actually load from server
		Float[] arr = {null, null, .2f, .6f, .9f, .5f, .6f, .4f, .7f, .8f, .6f, null};
		return Arrays.asList(arr);
	}
	
	
	/**Sends the query to the server and gets a response.*/
	public static List<ComparisonEntry> query(Query query)
	{
		//TODO actually load from server
		List<ComparisonEntry> out = new ArrayList<>();
		out.add(new ComparisonEntry(new Restaurant(1, "McDonalds", "someplace", 0, 0), .4f));
		out.add(new ComparisonEntry(new Restaurant(1, "McDonalds (drive-thru)", "someplace", 0, 0), .5f));
		out.add(new ComparisonEntry(new Restaurant(1, "Panera", "someplace", 0, 0), .7f));
		out.add(new ComparisonEntry(new Restaurant(1, "Pizza Hut", "someplace", 0, 0), .2f));
		return out;
	}
}
