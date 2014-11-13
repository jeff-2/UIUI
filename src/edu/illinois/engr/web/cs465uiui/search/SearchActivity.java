package edu.illinois.engr.web.cs465uiui.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.GraphActivity;
import edu.illinois.engr.web.cs465uiui.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
public class SearchActivity extends ListActivity implements AsyncListener<List<SearchItem>> {
	
	/** The dialog used to display progress of the SearchTask which gathers restaurant information from a server. */
	private ProgressDialog dialog;
	
	/**
	 * Executes an asynchronous task to request the restaurants from the server. Stores the list of restaurants in a fragment
	 * that is retained if successful. If no network connection is provided, displays a helpful dialog to the user.
	 *
	 * @return the restaurants
	 */
	public void loadRestaurants() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			FragmentManager fm = getFragmentManager();
			SearchFragment fragment = (SearchFragment)fm.findFragmentByTag("search");
			if (fragment == null) {
				fragment = new SearchFragment();
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.add(fragment, "search").commit();
			}
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
				if (!restaurantsBeingLoaded())
					loadRestaurants();
				filterRestaurants(query);
				searchView.clearFocus();

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
		if (!restaurantsBeingLoaded())
			loadRestaurants();
	}

	/**
	 * Filters the list of restaurants being displayed to those that match the specified search text.
	 *
	 * @param query the search text to filter by
	 */
	private void filterRestaurants(String query) {
		// if restaurants not yet loaded, can't filter
		if (getFragmentManager().findFragmentByTag("searchData") == null)
			return;

		Log.d("SearchActivity", "Filtering Restaurants with query:" + query);
		ArrayList<SearchItem> filteredRestaurants = new ArrayList<SearchItem>();
		for (SearchItem restaurant : ((SearchDataFragment)getFragmentManager().findFragmentByTag("searchData")).getRestaurants()) {
			String restaurantName = restaurant.getRestaurantName();
			if (query.length() <= restaurantName.length() && restaurantName.substring(0, query.length()).equalsIgnoreCase(query))
				filteredRestaurants.add(restaurant);
		}
		Log.d("SearchableActivity", "Num filtered:" + filteredRestaurants.size());
		if (filteredRestaurants.isEmpty())
			filteredRestaurants.add(new SearchItem(-1, "No restaurants match your search", ""));
		ListAdapter adapter = new SearchListArrayAdapter(this, filteredRestaurants);
		setListAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("SearchableActivity", "ListView item selected");
		SearchItem item = (SearchItem)getListView().getItemAtPosition(position);
		Intent intent = new Intent(this, GraphActivity.class);
		GraphActivity.setup(item.getRestaurantId(), Calendar.getInstance(), intent);
		startActivity(intent);
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
	 * Prepares and displays progress dialog for gathering restaurant data from a server.
	 */
	private void prepareProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Getting restaurant information. Please wait...");
		dialog.show();
	}
	
	/* (non-Javadoc)
	 * @see edu.illinois.engr.web.cs465uiui.search.AsyncListener#onPreExecute()
	 */
	@Override
	public void onPreExecute() {
		if (dialog == null)
			prepareProgressDialog();
	}

	/* (non-Javadoc)
	 * @see edu.illinois.engr.web.cs465uiui.search.AsyncListener#onPostExecute(java.lang.Object)
	 */
	@Override
	public void onPostExecute(List<SearchItem> result) {
		// save data in a fragment
		FragmentManager fm = getFragmentManager();
		SearchDataFragment dataFragment = new SearchDataFragment();
		dataFragment.setRestaurants(result);
		fm.beginTransaction().add(dataFragment, "searchData").commit();
		cleanup();
	}

	/* (non-Javadoc)
	 * @see edu.illinois.engr.web.cs465uiui.search.AsyncListener#onCancelled(java.lang.Object)
	 */
	@Override
	public void onCancelled(List<SearchItem> result) {
		cleanup();
	}
	
	/**
	 * Cleans up the data associated with the request for restaurant data from the server (SearchFragment and ProgressDialog). 
	 */
	private void cleanup() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentByTag("search");
		if (fragment != null)
			fm.beginTransaction().remove(fragment).commit();
	}

	/**
	 * Checks if restaurants are already loaded or being loaded from the network.
	 * @return true if the restaurants are being loaded or have already been loaded, otherwise false
	 */
	private boolean restaurantsBeingLoaded() {
		return getFragmentManager().findFragmentByTag("search") != null 
				|| getFragmentManager().findFragmentByTag("searchData") != null;
	}
}
