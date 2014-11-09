package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;

import com.google.android.gms.common.internal.q;

import edu.illinois.engr.web.cs465uiui.store.QueryData;
import edu.illinois.engr.web.cs465uiui.text.DateDisplay;
import edu.illinois.engr.web.cs465uiui.text.Display;
import edu.illinois.engr.web.cs465uiui.ui.QueryTagsDialog;
import edu.illinois.engr.web.cs465uiui.ui.TimeDialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**Lets the user set up a query that will be used on the comparison screens.
 * Lets the user enter the time when they want to visit a restaurant,
 * the tags they're looking for,
 * and the location they're looking for restaurants near.*/
public class QueryActivity extends Activity implements TimeDialog.Listener, QueryTagsDialog.Listener
{
	/**The query the user has constructed.*/
	private Query query;
	
	private TextView timeDisplay, tagsDisplay, allTagsDisplay, positionDisplay;
	
	
	@Override protected void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		setContentView(R.layout.activity_query);
		
		timeDisplay = (TextView)findViewById(R.id.act_query_time);
		tagsDisplay = (TextView)findViewById(R.id.act_query_tags);
		allTagsDisplay = (TextView)findViewById(R.id.act_query_alltags);
		positionDisplay = (TextView)findViewById(R.id.act_query_position);
		
		query = QueryData.load(getApplicationContext());
		refresh();
	}
	
	
	/**Updates the displays of time, location, and tags based on the chosen time, position, and tags.*/
	private void refresh()
	{
		timeDisplay.setText(query.time == null ? "(now)" : DateDisplay.full(query.time));
		tagsDisplay.setText(Display.tagList(query.tags));
		allTagsDisplay.setText(query.tags.isEmpty() ? "any tag" : query.allTags ? "all of" : "any of");
		positionDisplay.setText(query.position == null ? "(my GPS position)" : query.position);
	}
	
	
	
	public void handleEditTime(View v)
	{
		Bundle args = new Bundle();
		TimeDialog.setupArgs(query.time, args);
		TimeDialog dialog = new TimeDialog();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), null);
	}
	
	public void handleEditTags(View v)
	{
		new QueryTagsDialog().show(getFragmentManager(), "");
	}
	
	
	
	@Override public void onTimeNoPick(){}

	@Override public void onTimePicked(Calendar time)
	{
		Log.d("----", "time picked " + (time == null ? "null" : DateDisplay.full(time)));
		query.time = time;
		refresh();
	}
	
	@Override public void onQueryTagsClosed()
	{
		query = QueryData.load(getApplicationContext());
		refresh();
	}
}
