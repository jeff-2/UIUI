package edu.illinois.engr.web.cs465uiui.net;

import java.io.IOException;
import java.net.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.*;

import android.util.Log;
import android.util.LruCache;
import android.util.Pair;

import edu.illinois.engr.web.cs465uiui.ComparisonEntry;
import edu.illinois.engr.web.cs465uiui.CrowdDay;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.Restaurant;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.utils.IOUtil;
import static edu.illinois.engr.web.cs465uiui.utils.Time.putTime;

/**Fetches data from the server.
 * May cache results.*/
public class Fetch
{
	private static final String URL_TAGS = "http://cs465uiui.web.engr.illinois.edu/tags.php",
			URL_RESTAURANT = "http://cs465uiui.web.engr.illinois.edu/restaurants.php",
			URL_CROWDEDNESS = "http://cs465uiui.web.engr.illinois.edu/crowdedness.php";
	
	
	/**Null when not loaded.*/
	private static List<Tag> tags = null;
	
	/**Caches restaurants by id.*/
	private static final LruCache<Long, Restaurant> restaurantCache = new LruCache<>(100);
	
	
	
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
	
	
	/**Fetches a single restaurant. May return a cached result.*/
	public static Restaurant restaurant(long id) throws JSONException, IOException
	{
		Restaurant r = restaurantCache.get(id);
		if(r != null)
			return r;
		List<Restaurant> rs = loadRestaurants(Arrays.asList(new Long[]{id}));
		return rs.get(0);
	}
	
	
	/**Fetches info restaurants with the provided IDs.
	 * May return cached results.
	 * Results are in no particular order.*/
	public static List<Restaurant> restaurants(List<Long> ids) throws JSONException, IOException
	{
		List<Restaurant> out = new ArrayList<>();
		List<Long> needed = new ArrayList<>();
		for(Long id : ids)
		{
			Restaurant r = restaurantCache.get(id);
			if(r == null)
				needed.add(id);
			else
				out.add(r);
		}
		
		out.addAll(loadRestaurants(needed));
		return out;
	}
	
	/**Loads restaurants with the provided IDs from the server.
	 * Also puts them into the cache.*/
	private static List<Restaurant> loadRestaurants(List<Long> ids) throws JSONException, IOException
	{
		JSONArray json = new JSONArray();
		for(Long id : ids)
			json.put(id);
		
		HttpPost post = new HttpPost(URL_RESTAURANT);
		post.setEntity(new StringEntity(json.toString()));
		HttpResponse response = new DefaultHttpClient().execute(post);
		
		JSONArray root = new JSONArray(IOUtil.readStream(response.getEntity().getContent()));
		List<Restaurant> restaurants = new ArrayList<>();
		for(int c = 0; c < root.length(); c++)
		{
			JSONObject current = root.getJSONObject(c);
			Restaurant r = new Restaurant(current.getLong("id"), current.getString("name"), current.getString("location"),
					(float)current.getDouble("lat"), (float)current.getDouble("lon"));
			restaurants.add(r);
			restaurantCache.put(r.id, r);
		}
		return restaurants;
	}
	
	
	/**Gets the crowdedness of a restaurant on a date.
	 * Returns a list of crowd levels at evenly-spaced times throughout the day;
	 * if there are 5 results, they occur at midnight, 6:00, noon, 6:00 PM, and midnight.*/
	@Deprecated public static List<Float> crowdednessOn(Calendar date, long restaurantID) throws JSONException, IOException
	{
		//TODO actually load from server
		Float[] arr = {null, null, .2f, .6f, .9f, .5f, .6f, .4f, .7f, .8f, .6f, null};
		return Arrays.asList(arr);
	}
	
	
	public static CrowdDay crowdedness(Calendar date, long restaurantID) throws JSONException, IOException
	{
		HttpGet get = new HttpGet(URL_CROWDEDNESS);
		get.getParams().setLongParameter("restaurantId", restaurantID);
		get.getParams().setParameter("day", date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toUpperCase());
		HttpResponse response = new DefaultHttpClient().execute(get);
		JSONObject root = new JSONObject(IOUtil.readStream(response.getEntity().getContent()));
		
		Calendar open = (Calendar)date.clone(), close = (Calendar)date.clone();
		putTime(root.getString("open"), open);
		putTime(root.getString("close"), close);
		
		List<Pair<Calendar, Float>> values = new ArrayList<>();
		JSONArray array = root.getJSONArray("crowdedness");
		for(int c = 0; c < array.length(); c++)
		{
			JSONObject current = array.getJSONObject(c);
			Calendar time = (Calendar)date.clone();
			putTime(current.getString("time"), time);
			float crowd = Float.parseFloat(current.getString("crowdedness"));
			values.add(new Pair<>(time, crowd));
		}
		
		return new CrowdDay(open, close, values);
	}
}
