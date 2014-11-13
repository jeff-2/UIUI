package edu.illinois.engr.web.cs465uiui.comparison.map;

import java.io.IOException;

import org.json.JSONException;

import android.os.AsyncTask;
import edu.illinois.engr.web.cs465uiui.Restaurant;
import edu.illinois.engr.web.cs465uiui.net.Fetch;

public class SearchInfoFetch extends AsyncTask<Long, Void, Restaurant> {
	@Override
	protected Restaurant doInBackground(Long... params) {
		System.out.println("b");
		Restaurant restaurant = null;
		System.out.println("c");
		try {
			System.out.println("d");
			restaurant = Fetch.restaurant(params[0]);
		} catch (JSONException e) {
			System.out.println("e");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("f");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("g");
		return restaurant;
	}
	


}
