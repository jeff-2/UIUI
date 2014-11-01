package edu.illinois.engr.web.cs465uiui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchableActivity extends ListActivity {

	private SearchQueryTask task;
	private List<SearchItem> restaurants;

	public void getRestaurants() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			task = new SearchQueryTask(this);
			task.execute();
		} else {
			// TODO: more user friendly handling of no network connectivity. maybe check before going to search activity?
			Log.d("SearchableActivity", "Error:no network connectivity");
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setMessage("You are not currently connected to a network. Enable access to be able to search.")
			.setTitle("Info")
			.setCancelable(false)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								dialog.dismiss();
								finish();
							}
						});
			builder.create().show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.d("SearchableActivity", "OnCreate");

		final SearchView searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				filterRestaurants(query);

				// TODO: onQueryTextSubmit is called twice
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() == 0) {
					Log.d("SearchableActivity", "Query Text Cleared");
					// clear results
					setListAdapter(null);
				} else {
					filterRestaurants(newText);
				}
				return true;
			}
		});

		getRestaurants();
	}

	private void filterRestaurants(String query) {
		if (restaurants == null)
			return;

		Log.d("SearchableActivity", "Filtering Restaurants with query:" + query);
		ArrayList<SearchItem> filteredRestaurants = new ArrayList<SearchItem>();
		for (SearchItem restaurant : restaurants) {
			String restaurantName = restaurant.getRestaurantName();
			if (query.length() <= restaurantName.length() && restaurantName.substring(0, query.length()).equalsIgnoreCase(query)) {
				filteredRestaurants.add(restaurant);
			}
		}
		Log.d("SearchableActivity", "Num filtered:" + filteredRestaurants.size());
		if (filteredRestaurants.isEmpty()) {
			filteredRestaurants.add(new SearchItem("No restaurants match your search", ""));
		}
		ListAdapter adapter = new SearchListArrayAdapter(this,
				filteredRestaurants);
		setListAdapter(adapter);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("SearchableActivity", "ListView item selected");
		// TODO: pass on relevant data to graph screen
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	private class SearchQueryTask extends AsyncTask<Void, Void, List<SearchItem>> {
		
		// TODO: references to activity are unsafe to hold
		private ProgressDialog dialog;

		public SearchQueryTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("Loading restaurant information. Please wait.");
		}
		
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
		
		@Override
		protected List<SearchItem> doInBackground(Void... params) {
			
			long time = System.currentTimeMillis();
			
			List<SearchItem> queryResults = new ArrayList<SearchItem>();

			try {
				URL url = new URL("http://cs465uiui.web.engr.illinois.edu/search.php");
				try {
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					try {
						InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
						queryResults = JsonUtils.parseJSONResponse(inputStream);
					} finally {
						urlConnection.disconnect();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			Log.d("ServerQueryTask", "Elapsed time:" + (System.currentTimeMillis() - time));

			return queryResults;
		}

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

			SearchableActivity.this.restaurants = results;
		}
	}

}
