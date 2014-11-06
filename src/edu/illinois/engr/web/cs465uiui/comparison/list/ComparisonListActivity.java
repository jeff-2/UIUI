package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.R;

import android.app.ListActivity;
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
				comparisonSortCrowdedness.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
				comparisonSortDistance.setTextColor(getResources().getColor(android.R.color.black));
			}
		});

		// sort by crowdedness initially
		comparisonSortCrowdedness.callOnClick();
	}

	/**
	 * Populates and sets the ListAdapter for this activity.
	 */
	private void setupAdapter() {
		// TODO: use data from previous activity

		// garbage data
		List<ComparisonItem> items = new ArrayList<ComparisonItem>();
		items.add(new ComparisonItem("Restaurant Name", "Restaurant Address", "5.3 miles", "CROWDED"));
		items.add(new ComparisonItem("Some Really Long Restaurant Name Goes Here", "Some Really Long Restaurant Address Is Being Displayed Here", "341352.342 miles", "EMPTY"));
		items.add(new ComparisonItem("Panda Express", "627 S Wright Street E Green St Champaign, IL 61820",	"3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "CROWDED"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "LIGHT"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));
		items.add(new ComparisonItem("A", "B", "3 miles", "PACKED"));
		items.add(new ComparisonItem("A", "B", "3 miles", "EMPTY"));

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
