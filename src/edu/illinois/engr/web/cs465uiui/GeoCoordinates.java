package edu.illinois.engr.web.cs465uiui;


import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class used to call the Google Maps API online asynchronously to receive 
 * JSON of the query. Used to get the Longitude and Latitude of a given address.
 */
public class GeoCoordinates extends AsyncTask<String, Void, LatLng> {
	@Override
	protected LatLng doInBackground(String... address) {
		Double longitude = 0.0;
		Double latitude = 0.0;
		try {
			String jsonStr = "";
			HttpResponse response;
			String addr = URLEncoder.encode(address[0], "UTF-8");
			String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" +
					addr + "&sensor=false";
			
			// Get JSON from the server
			HttpClient myClient = new DefaultHttpClient();
			HttpPost myConnection = new HttpPost(url);
			response = myClient.execute(myConnection);
			jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			// Extract the longitude and latitude from the first result
			JSONObject json = new JSONObject(jsonStr);
			JSONArray jArray = json.getJSONArray("results");
			JSONObject firstResult = jArray.getJSONObject(0);
			longitude = (Double) firstResult.getJSONObject("geometry").getJSONObject("location").get("lng");
			latitude = (Double) firstResult.getJSONObject("geometry").getJSONObject("location").get("lat");
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return new LatLng(latitude, longitude);
	}
	
	/**
	 * Gets a LatLng from the Google Maps API from the address
	 * @param address String representing the address of the restaurant
	 * @return LatLng created from the address
	 */
	public static LatLng getCoordinates(String address) {
    	try {
			return new GeoCoordinates().execute(address).get();
		} catch (Exception e) {
			return null;
		}
	}

}
