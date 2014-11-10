package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;

import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.comparison.map.GeoCoordinates;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

import android.app.ListActivity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
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

	// TODO: add icon to switch to map comparison
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comparison);

		// TODO: get data from previous activity
		setupAdapter();

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
	
	private String buildQueryString() {
		Query data = QueryData.load(this);
		
		StringBuilder sb = new StringBuilder();
		sb.append("?tags=");
		
		List<Tag> tags = data.tags;
		if (tags == null || tags.size() == 0) {
			sb.append("null");
		} else if (tags.size() == 1) {
			sb.append(tags.get(0));
		} else {
			sb.append(tags.get(0));
			sb.append(",");
			for (int i = 1; i < tags.size() - 1; i++) {
				sb.append(tags.get(i));
				sb.append(",");
			}
			sb.append(tags.get(tags.size() - 1));
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
		
		double latitude = -1;
		double longitude = -1;
		if (data.position == null) {

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(),true));
			
			// TODO: some location handling stuff
			if (lastKnownLocation == null) {
				return null;
			}
			
			latitude = lastKnownLocation.getLatitude();
			longitude = lastKnownLocation.getLongitude();
		} else {
			LatLng positionCoords = GeoCoordinates.getCoordinates(data.position);
			if (positionCoords == null)
				return null;

		    latitude = positionCoords.latitude;
		    longitude = positionCoords.longitude;
		}
		
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
		// TODO: use data from previous activity
		
		String queryString = buildQueryString();
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
			items.add(new ComparisonItem("No restaurants match your query", "", "", ""));

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
		// TODO: pass on data to graph view screen
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
