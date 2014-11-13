package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;

import edu.illinois.engr.web.cs465uiui.comparison.list.ComparisonListActivity;
import edu.illinois.engr.web.cs465uiui.store.QueryData;
import edu.illinois.engr.web.cs465uiui.text.DateDisplay;
import edu.illinois.engr.web.cs465uiui.text.Display;
import edu.illinois.engr.web.cs465uiui.ui.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**Lets the user set up a query that will be used on the comparison screens.
 * Lets the user enter the time when they want to visit a restaurant,
 * the tags they're looking for,
 * and the location they're looking for restaurants near.*/
public class QueryActivity extends Activity implements LocationDialog.Listener, TimeDialog.Listener, QueryTagsDialog.Listener
{
	/**The query the user has constructed.*/
	private Query query;
	
	private TextView timeDisplay, tagsDisplay, allTagsDisplay, positionNone, positionCustom;
	private EditText radiusInput;
	
	private final Handler handler = new Handler();
	
	
	@Override protected void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		setContentView(R.layout.activity_query);
		
		timeDisplay = (TextView)findViewById(R.id.act_query_time);
		tagsDisplay = (TextView)findViewById(R.id.act_query_tags);
		allTagsDisplay = (TextView)findViewById(R.id.act_query_alltags);
		positionNone = (TextView)findViewById(R.id.act_query_position_none);
		positionCustom = (TextView)findViewById(R.id.act_query_position_custom);
		radiusInput = (EditText)findViewById(R.id.act_query_distance);
		
		positionNone.setOnClickListener(handler);
		positionCustom.setOnClickListener(handler);
		
		query = QueryData.load(getApplicationContext());
		refresh();
	}
	
	@Override protected void onStop()
	{
		save();
		super.onStop();
	}
	
	
	/**Gets user input and saves it.
	 * Call this when a dialog or other activity is about to be started.*/
	private void save()
	{
		try
		{
			query.radiusMiles = Integer.parseInt(radiusInput.getText().toString());
			if(query.radiusMiles <= 0)
				query.radiusMiles = Query.DEFAULT_RADIUS;
		}
		catch(NumberFormatException e){query.radiusMiles = Query.DEFAULT_RADIUS;}
		QueryData.save(query, getApplicationContext());
	}
	
	
	/**Updates the displays of time, location, and tags based on the chosen time, position, and tags.*/
	private void refresh()
	{
		timeDisplay.setText(query.time == null ? "(now)" : DateDisplay.full(query.time));
		tagsDisplay.setText(Display.tagList(query.tags));
		allTagsDisplay.setText(query.tags.isEmpty() ? "any tag" : query.allTags ? "all of" : "any of");
		radiusInput.setText(String.valueOf(query.radiusMiles));
		if(query.position == null)
		{
			positionNone.setVisibility(View.VISIBLE);
			positionCustom.setVisibility(View.GONE);
		}
		else
		{

			positionCustom.setVisibility(View.VISIBLE);
			positionNone.setVisibility(View.GONE);
			positionCustom.setText(query.position);
		}
	}
	
	
	
	public void handleEditTime(@SuppressWarnings("unused") View v)
	{
		save();
		Bundle args = new Bundle();
		TimeDialog.setupArgs(query.time, args);
		TimeDialog dialog = new TimeDialog();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), null);
	}
	
	public void handleEditTags(@SuppressWarnings("unused") View v)
	{
		save();
		new QueryTagsDialog().show(getFragmentManager(), "");
	}
	
	public void handleDone(@SuppressWarnings("unused") View v){startActivity(new Intent(this, ComparisonListActivity.class));}
	
	
	
	@Override public void onTimeNoPick(){}

	@Override public void onTimePicked(Calendar time)
	{
		query.time = time;
		refresh();
	}
	
	@Override public void onQueryTagsClosed()
	{
		query = QueryData.load(getApplicationContext());
		refresh();
	}
	
	@Override public void onLocationPicked(String location)
	{
		query.position = location;
		refresh();
	}
	
	
	
	/**Handles clicks on the 2 position labels.*/
	private class Handler implements View.OnClickListener
	{
		@Override public void onClick(View v)
		{
			Bundle args = new Bundle();
			LocationDialog.setupArgs(query.position, args);
			LocationDialog dialog = new LocationDialog();
			dialog.setArguments(args);
			dialog.show(getFragmentManager(), "");
		}
	}
}
