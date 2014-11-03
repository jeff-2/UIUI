package edu.illinois.engr.web.cs465uiui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button button = (Button) findViewById(R.id.mainButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	goToSearchActivity();
            }
        });
        
        final Button otherButton = (Button) findViewById(R.id.mainButton2);
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
	
	public void goToComparisonActivity() {
    	Intent intent = new Intent(this, ComparisonListActivity.class);
    	startActivity(intent);
	}
	
	public void goToSearchActivity() {
    	Intent intent = new Intent(this, SearchableActivity.class);
    	startActivity(intent);
	}
	
	public void goToComparisonMapActivity() {
    	Intent intent = new Intent(this, ComparisonMapActivity.class);
    	startActivity(intent);
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
}
