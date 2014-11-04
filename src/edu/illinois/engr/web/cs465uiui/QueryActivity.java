package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;

import edu.illinois.engr.web.cs465uiui.store.QueryData;
import edu.illinois.engr.web.cs465uiui.ui.TimeDialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**Lets the user set up a query that will be used on the comparison screens.
 * Lets the user enter the time when they want to visit a restaurant,
 * the tags they're looking for,
 * and the location they're looking for restaurants near.*/
public class QueryActivity extends Activity
{
	/**The query the user has constructed.*/
	private Query query;
	
	private TextView timeDisplay, positionDisplay;
	
	
	@Override protected void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		setContentView(R.layout.activity_query);
		
		timeDisplay = (TextView)findViewById(R.id.act_query_time);
		positionDisplay = (TextView)findViewById(R.id.act_query_position);
		
		query = QueryData.load(getApplicationContext());
		refresh();
	}
	
	
	/**Updates the displays of time, location, and tags based on the chosen time, position, and tags.*/
	private void refresh()
	{
		timeDisplay.setText(query.time == null ? "(now)" : query.time.toString());
		//TODO tags
		positionDisplay.setText(query.location == null ? "(my GPS position)" : query.location);
	}
	
	
	
	public void handleEditTime(View v)
	{
		Bundle args = new Bundle();
		TimeDialog.setupArgs(query.time, args);
		TimeDialog dialog = new TimeDialog();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), null);
	}
	
	
	
	/**Handles dialogs closing.*/
	private class Handler implements TimeDialog.Listener
	{
		@Override public void onTimeNoPick(){}

		@Override public void onTimePicked(Calendar time)
		{
			query.time = time;
			refresh();
		}
	}
}
