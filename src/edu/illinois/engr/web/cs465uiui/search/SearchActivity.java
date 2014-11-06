package edu.illinois.engr.web.cs465uiui.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.utils.JsonParserImpl;
import edu.illinois.engr.web.cs465uiui.utils.NetworkRequest;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

/**
 * SearchActivity provides the functionality for searching for a particular restaurant. It displays the restaurants and their
 * corresponding addresses that match the specified search. Allows the user to verify the location of the restaurant on the map
 * and select a search result for a more informative view.
 */
public class SearchActivity extends ListActivity {
	
	private static final String requestURL = "http://cs465uiui.web.engr.illinois.edu/search.php";

	/** The restaurants in the database. */
	private List<SearchItem> restaurants;

	/**
	 * Executes an asynchronous task to request the restaurants from the server. Sets the list of restaurants to the result
	 * if successful. If no network connection is provided, displays a helpful dialog to the user.
	 */
	public void getRestaurants() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			SearchQueryTask task = new SearchQueryTask(this);
			task.execute();
		} else {
			showNetworkConnectivityDialog();
		}
	}
	
	/**
	 * Displays a network connectivity dialog indicating to the user that functionality will be limited
	 * due to a lack of internet access.
	 */
	private void showNetworkConnectivityDialog() {
		Log.d("SearchActivity", "Error:no network connectivity");
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setMessage("You are not currently connected to a network. No search results will be available until internet access is available.")
		.setTitle("No Network Access")
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setPositiveButton("Settings", 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int id) {
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
						}
					});
		builder.create().show();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.d("SearchActivity", "OnCreate");

		final SearchView searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// filters all restaurants down to restaurants that match the search text
				if (restaurants == null || restaurants.isEmpty())
					getRestaurants();
				filterRestaurants(query);

				// TODO: onQueryTextSubmit is called twice
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				
				// if search emptied, clear results
				if (newText.length() == 0) {
					Log.d("SearchActivity", "Query Text Cleared");
					setListAdapter(null);
				} else {
					filterRestaurants(newText);
				}
				return true;
			}
		});
		getRestaurants();
	}

	/**
	 * Filters the list of restaurants being displayed to those that match the specified search text.
	 *
	 * @param query the search text to filter by
	 */
	private void filterRestaurants(String query) {
		if (restaurants == null || restaurants.isEmpty())
			return;

		Log.d("SearchActivity", "Filtering Restaurants with query:" + query);
		ArrayList<SearchItem> filteredRestaurants = new ArrayList<SearchItem>();
		for (SearchItem restaurant : restaurants) {
			String restaurantName = restaurant.getRestaurantName();
			if (query.length() <= restaurantName.length() && restaurantName.substring(0, query.length()).equalsIgnoreCase(query))
				filteredRestaurants.add(restaurant);
		}
		Log.d("SearchableActivity", "Num filtered:" + filteredRestaurants.size());
		if (filteredRestaurants.isEmpty())
			filteredRestaurants.add(new SearchItem("No restaurants match your search", ""));
		ListAdapter adapter = new SearchListArrayAdapter(this, filteredRestaurants);
		setListAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("SearchableActivity", "ListView item selected");
		// TODO: pass on relevant data to graph screen
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * SearchQueryTask provides an asynchronous task which queries the server for restaurants and parses the response,
	 * putting the result into the restaurants list.
	 */
	private class SearchQueryTask extends AsyncTask<Void, Void, List<SearchItem>> {
		
		/** The dialog used for indicating the progress of this task. */
		private ProgressDialog dialog;

		/**
		 * Instantiates a new search query task.
		 *
		 * @param context the context
		 */
		public SearchQueryTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("Loading restaurant information. Please wait.");
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected List<SearchItem> doInBackground(Void... params) {

			List<SearchItem> queryResults = new ArrayList<>();

			URL url;
			try {
				url = new URL(requestURL);
				NetworkRequest<SearchItem> request = new NetworkRequest<SearchItem>(url, new JsonParserImpl<SearchItem>(SearchItem.class));
				queryResults = request.sendAndReceive();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			return queryResults;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(List<SearchItem> results) {
			
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			
			Log.d("ServerQueryTask", "onPostExecute");
			for (SearchItem result : results) {
				Log.d("ServerQueryTask", "name:" + result.getRestaurantName());
				Log.d("ServerQueryTask", "address:" + result.getRestaurantAddress());
			}

			SearchActivity.this.restaurants = results;
		}
	}

}
