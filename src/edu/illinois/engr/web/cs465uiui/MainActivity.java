package edu.illinois.engr.web.cs465uiui;

import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.comparison.list.ComparisonListActivity;
import edu.illinois.engr.web.cs465uiui.comparison.map.ComparisonMapActivity;
import edu.illinois.engr.web.cs465uiui.search.SearchActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * MainActivity provides the initial startup screen for the application and navigation to the search and query screens.
 */
public class MainActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button button = (Button) findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToSearchActivity();
            }
        });
        
        final Button otherButton = (Button) findViewById(R.id.comparisonListButton);
        otherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToComparisonActivity();
            }
        });
        
        final Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToComparisonMapActivity();
            }
        });
	}
	
	
	// TODO: this is temporary
	/**
	 * Go to comparison activity.
	 */
	public void goToComparisonActivity() {
    	Intent intent = new Intent(this, ComparisonListActivity.class);
    	startActivity(intent);
	}
	
	/**
	 * Go to search activity.
	 */
	public void goToSearchActivity() {
    	Intent intent = new Intent(this, SearchActivity.class);
    	startActivity(intent);
	}
	
	// TODO: this is temporary
	/**
	 * Go to comparison map activity.
	 */
	public void goToComparisonMapActivity() {
    	Intent intent = new Intent(this, ComparisonMapActivity.class);
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
}
