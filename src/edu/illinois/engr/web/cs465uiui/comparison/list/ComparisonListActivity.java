package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import edu.illinois.engr.web.cs465uiui.GraphActivity;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.comparison.map.ComparisonMapActivity;
import edu.illinois.engr.web.cs465uiui.comparison.map.GeoCoordinates;
import edu.illinois.engr.web.cs465uiui.search.AsyncListener;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ComparisonListActivity takes the information provided from the QueryActivity and displays the results in a list format. 
 * It provides the restaurant names, addresses, distances and crowdedness for all of the restaurants that matched
 * the query criteria. It also provides the ability to sort the list by distance, or crowdedness.
 */
public class ComparisonListActivity extends ListActivity implements AsyncListener<List<ComparisonItem>> {
	
	/** The dialog used to display progress of the SearchTask which gathers restaurant information from a server. */
	private ProgressDialog dialog;
	
	/**
	 * Executes an asynchronous task to request the restaurants matching the user query from the server. Stores the list of restaurants in a fragment
	 * that is retained if successful. If no network connection is provided, displays a helpful dialog to the user.
	 */
	private void loadRestaurants(String queryString) {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			FragmentManager fm = getFragmentManager();
			ComparisonFragment fragment = (ComparisonFragment)fm.findFragmentByTag("comparison");
			if (fragment == null) {
				fragment = new ComparisonFragment();
				Bundle args = new Bundle();
				args.putString("queryString", queryString);
				fragment.setArguments(args);
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.add(fragment, "comparison").commit();
			}
		} else {
			showNetworkConnectivityDialog();
		}
	}
	
	private void loadLocation() {
		final Query data = QueryData.load(this);
		
		if (data.position == null) {

			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				showGPSDisabledDialog();
		    } else {
		    	
				if (dialog == null)
					prepareProgressDialog();
		    	
				LocationListener locationListener = new LocationListener() {
	
					@Override
					public void onLocationChanged(Location location) {
						loadRestaurants(buildQueryString(data, location.getLatitude(), location.getLongitude()));
					}
	
					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {}
	
					@Override
					public void onProviderEnabled(String provider) {}
	
					@Override
					public void onProviderDisabled(String provider) {} 
					
				};
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); 
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		    }
		} else {
			if (dialog == null)
				prepareProgressDialog();
			
			LatLng positionCoords = GeoCoordinates.getCoordinates(data.position);
			
			loadRestaurants(buildQueryString(data, positionCoords.latitude, positionCoords.longitude));
		}
	}

	/**
	 * Displays a network connectivity dialog indicating to the user that functionality will be limited
	 * due to a lack of GPS data.
	 */
    private void showGPSDisabledDialog() {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this)
    	.setMessage("Your GPS is currently disabled. No location information can be gathered until it is available.")
    	.setTitle("No GPS Info Available")
    	.setCancelable(false)
    	.setIcon(android.R.drawable.ic_dialog_alert)
        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
        	}
        })
        .setPositiveButton("Settings", 
        		new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		            }
        		});
    	builder.create().show();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparison);
		
		final ImageButton comparisonMapButton = (ImageButton) findViewById(R.id.comparisonMapButton);
		comparisonMapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ComparisonListActivity.this, ComparisonMapActivity.class);
				intent.putParcelableArrayListExtra("comparisonList", (ArrayList<ComparisonItem>)((ComparisonListArrayAdapter)getListAdapter()).getList());
				startActivity(intent);
			}
			
		});

		final TextView comparisonSortDistance = (TextView) findViewById(R.id.comparisonSortDistance);
		final TextView comparisonSortCrowdedness = (TextView) findViewById(R.id.comparisonSortCrowdedness);
		

		// Sort by distance when user presses on sort over the distance column
		comparisonSortDistance.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {

				// already sorted by distance, do nothing
				if (comparisonSortDistance.getCurrentTextColor() == getResources().getColor(android.R.color.holo_blue_light)) {
					return;
				}

				// sort by distance
				if (getListAdapter() != null && getListAdapter() instanceof ArrayAdapter<?>) {
					((ArrayAdapter<ComparisonItem>) getListAdapter()).sort(new Comparator<ComparisonItem>() {
							@Override
							public int compare(ComparisonItem lhs, ComparisonItem rhs) {
								if (lhs.equals(rhs))
									return 0;
	
								int compare = lhs.compareDistance(rhs);
	
								// tie-break with crowdedness
								if (compare == 0)
									return lhs.compareCrowdedness(rhs);
	
								return compare;
							}
					});
				}
				comparisonSortDistance.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
				comparisonSortCrowdedness.setTextColor(getResources().getColor(android.R.color.black));
			}
		});

		// Sort by crowdedness when the user selects sort over the distance
		// column
		comparisonSortCrowdedness.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {

				// already sorted by crowdedness, do nothing
				if (comparisonSortCrowdedness.getCurrentTextColor() == getResources().getColor(android.R.color.holo_blue_light))
					return;

				// sort by crowdedness
				if (getListAdapter() != null && getListAdapter() instanceof ArrayAdapter<?>) {
					((ArrayAdapter<ComparisonItem>) getListAdapter()).sort(new Comparator<ComparisonItem>() {
							@Override
							public int compare(ComparisonItem lhs, ComparisonItem rhs) {
								if (lhs.equals(rhs))
									return 0;
	
								int compare = lhs.compareCrowdedness(rhs);
	
								// tie-break w/ distance
								if (compare == 0)
									return lhs.compareDistance(rhs);
	
								return compare;
							}
					});
				}
				comparisonSortCrowdedness.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
				comparisonSortDistance.setTextColor(getResources().getColor(android.R.color.black));
			}
		});
		
		if (!restaurantsBeingLoaded()) {
			loadLocation();
		} else {
			setupAdapter();
		}
	}
	
	private String buildQueryString(Query data, double latitude, double longitude) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("?tags=");
		
		List<Tag> tags = data.tags;
		if (tags == null || tags.size() == 0) {
			sb.append("null");
		} else if (tags.size() == 1) {
			sb.append(tags.get(0).name);
		} else {
			Collections.sort(tags, Tag.compare);
			for (Tag t : tags) {
				Log.d("tag:", t.name);
			}
			sb.append(tags.get(0).name);
			sb.append(",");
			for (int i = 1; i < tags.size() - 1; i++) {
				sb.append(tags.get(i).name);
				sb.append(",");
			}
			sb.append(tags.get(tags.size() - 1).name);
		}
		

		SimpleDateFormat timeFormat = new SimpleDateFormat("H:m", Locale.US);
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE",  Locale.US);
		sb.append("&day=");
		if (data.time == null) {
			Calendar calendar = Calendar.getInstance();
			sb.append(dayFormat.format(calendar.getTime()).toUpperCase(Locale.US));
			sb.append("&time=");
			sb.append(timeFormat.format(calendar.getTime()));
		} else {
			sb.append(dayFormat.format(data.time.getTime()).toUpperCase(Locale.US));
			sb.append("&time=");
			sb.append(timeFormat.format(data.time.getTime()));
		}
		
		sb.append("&allTags=");
		sb.append(data.allTags ? "true" : "false");

		sb.append("&latitude=");
		sb.append(latitude);
		
		sb.append("&longitude=");
		sb.append(longitude);
		
		sb.append("&radius=");
		sb.append(data.radiusMiles);
		
		return sb.toString();
	}

	/**
	 * Populates and sets the ListAdapter for this activity.
	 */
	private void setupAdapter() {
		
		ComparisonDataFragment dataFragment = (ComparisonDataFragment)getFragmentManager().findFragmentByTag("comparisonData");
		while (dataFragment == null) {
			dataFragment = (ComparisonDataFragment)getFragmentManager().findFragmentByTag("comparisonData");
		}
		List<ComparisonItem> items = dataFragment.getRestaurants();
		
		if (items.isEmpty())
			items.add(new ComparisonItem(-1l, "No restaurants match your query", "", "", "", -1.0f, -1.0f));

		// update display
		ListAdapter adapter = new ComparisonListArrayAdapter(this, items);
		setListAdapter(adapter);

		final TextView comparisonSortCrowdedness = (TextView) findViewById(R.id.comparisonSortCrowdedness);
		// sort by crowdedness initially
		comparisonSortCrowdedness.callOnClick();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("ComparisonListActivity", "ListView item selected");
		ComparisonItem item = (ComparisonItem)getListView().getItemAtPosition(position);
		Intent intent = new Intent(this, GraphActivity.class);
		GraphActivity.setup(item.getRestaurantId(), Calendar.getInstance(), intent);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		dialog.setTitle("Getting restaurant information and GPS location. Please wait...");
		dialog.setCancelable(false);
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
	public void onPostExecute(List<ComparisonItem> result) {
		// save data in a fragment
		FragmentManager fm = getFragmentManager();
		ComparisonDataFragment dataFragment = new ComparisonDataFragment();
		dataFragment.setRestaurants(result);
		fm.beginTransaction().add(dataFragment, "comparisonData").commit();
		fm.executePendingTransactions();
		setupAdapter();
		cleanup();
	}

	/* (non-Javadoc)
	 * @see edu.illinois.engr.web.cs465uiui.search.AsyncListener#onCancelled(java.lang.Object)
	 */
	@Override
	public void onCancelled(List<ComparisonItem> result) {
		cleanup();
	}
	
	/**
	 * Cleans up the data associated with the request for restaurant data from the server (ComparisonFragment and ProgressDialog). 
	 */
	private void cleanup() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentByTag("comparison");
		if (fragment != null)
			fm.beginTransaction().remove(fragment).commit();
	}

	/**
	 * Checks if restaurants are already loaded or being loaded from the network.
	 * @return true if the restaurants are being loaded or have already been loaded, otherwise false
	 */
	private boolean restaurantsBeingLoaded() {
		return getFragmentManager().findFragmentByTag("comparison") != null 
				|| getFragmentManager().findFragmentByTag("comparisonData") != null;
	}
}
