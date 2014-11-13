package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;

import edu.illinois.engr.web.cs465uiui.GraphActivity;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.comparison.map.ComparisonMapActivity;
import edu.illinois.engr.web.cs465uiui.comparison.map.GeoCoordinates;
import edu.illinois.engr.web.cs465uiui.search.SearchItem;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
public class ComparisonListActivity extends ListActivity {
	
	private static String URL = "http://cs465uiui.web.engr.illinois.edu/query.php";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparison);

		buildQueryString();
		
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

		// sort by crowdedness initially
		comparisonSortCrowdedness.callOnClick();
	}
	
	private void buildQueryString() {
		final Query data = QueryData.load(this);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("?tags=");
		
		List<Tag> tags = data.tags;
		if (tags == null || tags.size() == 0) {
			sb.append("null");
		} else if (tags.size() == 1) {
			sb.append(tags.get(0).name);
		} else {
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

		if (data.position == null) {

			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			LocationListener locationListener = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					completeQueryString(sb,  data, location.getLatitude(), location.getLongitude());
					locationManager.removeUpdates(this);
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
		} else {
			LatLng positionCoords = GeoCoordinates.getCoordinates(data.position);
			if (positionCoords == null)
				return;

		    completeQueryString(sb, data, positionCoords.latitude, positionCoords.longitude);
		}
	}
	
	private void completeQueryString(StringBuilder sb, Query data, double latitude, double longitude) {
		
		sb.append("&latitude=");
		sb.append(latitude);
		
		sb.append("&longitude=");
		sb.append(longitude);
		
		sb.append("&radius=");
		sb.append(data.radiusMiles);
		
		setupAdapter(sb.toString());
	}

	/**
	 * Populates and sets the ListAdapter for this activity.
	 */
	private void setupAdapter(String queryString) {
		
		Log.d("ComparisonListActivity", "queryString=" + queryString);
		if (queryString == null)
			return;
		ComparisonQueryTask task = new ComparisonQueryTask();
		List<ComparisonItem> items = new ArrayList<>();
		try {
			items = task.execute(URL + queryString).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		if (items.isEmpty())
			items.add(new ComparisonItem(-1l, "No restaurants match your query", "", "", "", -1.0f, -1.0f));

		// update display
		ListAdapter adapter = new ComparisonListArrayAdapter(this, items);
		setListAdapter(adapter);
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
}
