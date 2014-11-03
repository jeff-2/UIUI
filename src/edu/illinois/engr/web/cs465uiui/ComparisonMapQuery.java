package edu.illinois.engr.web.cs465uiui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;

/**
 * Class used to get database information from a remote server needed
 * for the ComparisonMapActivity
 */
public class ComparisonMapQuery extends AsyncTask<Void, Void, List<RestaurantMapItem>> {
	@Override
	protected List<RestaurantMapItem> doInBackground(Void... params) {
		List<RestaurantMapItem> restaurants = new ArrayList<RestaurantMapItem>();
		try {
			String jsonStr = "";
			HttpResponse response;
			String url = "http://web.engr.illinois.edu/~cs465uiui/comparison_map.php";
			
			// Get JSON from the server
			HttpClient myClient = new DefaultHttpClient();
			HttpPost myConnection = new HttpPost(url);
			response = myClient.execute(myConnection);
			jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			// Construct RestaurantMapItems from the JSON
			JSONArray jArray = new JSONArray(jsonStr);
			for(int i = 0; i < jArray.length(); i++) {
				RestaurantMapItem restaurant = new RestaurantMapItem();
				JSONObject json = jArray.getJSONObject(i);
				
				restaurant.setName((String) json.get("restaurantName"));
				restaurant.setAddress((String) json.get("restaurantAddress"));
				restaurant.setCrowdedness((String) json.get("crowdedness"));
				restaurant.setTime((String) json.get("time"));
				restaurant.setDay((String) json.get("day"));
				restaurant.setLatitude((Double) json.get("latitude"));
				restaurant.setLongitude((Double) json.get("longitude"));
				
				restaurants.add(restaurant);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return restaurants;
	}
}
