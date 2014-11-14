package edu.illinois.engr.web.cs465uiui;

import java.util.*;

import static java.util.Calendar.*;

import edu.illinois.engr.web.cs465uiui.net.ServerResult;
import edu.illinois.engr.web.cs465uiui.text.DateDisplay;
import edu.illinois.engr.web.cs465uiui.text.Display;
import edu.illinois.engr.web.cs465uiui.ui.CrowdGraph;
import edu.illinois.engr.web.cs465uiui.ui.CrowdLevel;
import edu.illinois.engr.web.cs465uiui.ui.UIFetch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**Shows a graph of the crowdedness of a restaurant over a day, and lets the user switch days.
 * The intent used to start this activity must be provided to setup().*/
public class GraphActivity extends Activity
{
	/**Put a restaurant ID and initial date into the intent that will be used to start this activity.*/
	public static void setup(long restaurantID, Calendar date, Intent intent)
	{
		intent.putExtra("restaurant", restaurantID);
		intent.putExtra("date", date.getTimeInMillis());
	}
	
	/**Gets the restaurant ID out of the intent used to start this activity.*/
	private long intentRestaurantId(){return getIntent().getExtras().getLong("restaurant");}
	/**Gets the date out of the intent used to start this activity.*/
	private Calendar intentDate()
	{
		Calendar out = Calendar.getInstance();
		out.setTimeInMillis(getIntent().getExtras().getLong("date"));
		return out;
	}
	
	
	
	/**The date we're currently looking at.*/
	private Calendar date;
	/**If true, nav buttons shouldn't do anything.*/
	private boolean busy = false;
	/**Null when not loaded.*/
	private CrowdDay day = null;
	
	private TextView name, location, focusLevel, focusTime, focusExplanation;
	private CrowdGraph graph;
	private View zoomIn, zoomOut, zoomLeft, zoomRight;
	private ViewGroup navBar;
	
	
	private final SelectionHandler selectionHandler = new SelectionHandler();
	private final ZoomHandler zoomHandler = new ZoomHandler();
	
	
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		date = intentDate();
		
		graph = (CrowdGraph)findViewById(R.id.act_graph_graph);
		name = (TextView)findViewById(R.id.act_graph_name);
		location = (TextView)findViewById(R.id.act_graph_location);
		focusLevel = (TextView)findViewById(R.id.act_graph_focuslevel);
		focusTime = (TextView)findViewById(R.id.act_graph_focustime);
		focusExplanation = (TextView)findViewById(R.id.act_graph_focusexplanation);
		zoomIn = findViewById(R.id.act_graph_zoom_in);
		zoomOut = findViewById(R.id.act_graph_zoom_out);
		zoomLeft = findViewById(R.id.act_graph_zoom_left);
		zoomRight = findViewById(R.id.act_graph_zoom_right);
		navBar = (ViewGroup)findViewById(R.id.act_graph_navbar);
		
		graph.register(selectionHandler);
		zoomIn.setOnClickListener(zoomHandler);
		zoomOut.setOnClickListener(zoomHandler);
		zoomLeft.setOnClickListener(zoomHandler);
		zoomRight.setOnClickListener(zoomHandler);
		
		name.setText("");
		location.setText("");
		
		new LoadTask(intentDate(), intentRestaurantId(), this).execute();
		refreshNav();
		focus(null, null);
	}
	
	
	/**Refreshes the dates displayed in the nav bar.*/
	private void refreshNav()
	{
		navBar.removeAllViews();
		Calendar current = (Calendar)date.clone();
		current.add(DATE, -4);
		for(int c = 0; c < 7; c++)
		{
			current.add(DATE, 1);
			View v = getLayoutInflater().inflate(R.layout.sub_graph_navday, null);
			TextView name = (TextView)v.findViewById(R.id.sub_navday_name);
			
			name.setText(DateDisplay.dateShort(current));
			if(current.get(DATE) == date.get(DATE))//if this is the selected day
				name.setBackground(getResources().getDrawable(R.drawable.graph_nav_selected));
			v.setOnClickListener(new NavHandler((Calendar)current.clone(), this));
			navBar.addView(v);
		}
	}
	
	
	
	/**Fill out the focus display area with a focus.
	 * @param time null to clear the focus display instead.*/
	private void focus(Calendar calendar, CrowdLevel level)
	{
		if(calendar == null)
		{
			focusLevel.setText("");
			focusTime.setText("");
			focusExplanation.setText("");
		}
		else
		{
			focusLevel.setText(level.name);
			focusLevel.setTextColor(level.color);
			focusTime.setText(DateDisplay.time(calendar));
			focusExplanation.setText(level.description);
		}
	}
	
	
	
	/**Loads crowdedness data from the server on a background thread and then displays it on the UI thread.*/
	private class LoadTask extends AsyncTask<Void, Void, ServerResult<CrowdDay>>
	{
		private final Calendar myDate;
		private final long id;
		private final Activity activity;
		
		//acquired in background thread
		private Restaurant restaurant;
		
		public LoadTask(Calendar date, long restaurantID, Activity activity)
		{
			this.myDate = date;
			this.id = restaurantID;
			this.activity = activity;
		}
		
		@Override protected void onPreExecute()
		{
			busy = true;
		}
		
		@Override protected ServerResult<CrowdDay> doInBackground(Void... params)
		{
			ServerResult<Restaurant> result = UIFetch.restaurant(id);
			if(result.success)
				restaurant = result.result;
			else
				return new ServerResult<>(result.error);//XXX kludgy
				
			return UIFetch.crowdednessOn(myDate, id);
		}
		
		@Override protected void onPostExecute(ServerResult<CrowdDay> result)
		{
			if(result.success)
			{
				day = result.result;
				date = myDate;
				name.setText(restaurant.name);
				location.setText(restaurant.location);
				graph.setData(result.result);
				refreshNav();
			}
			else
				UIFetch.explainError(result.error, activity);
			busy = false;
		}
	}
	
	
	/**Handles clicks on the nav element with the provided date.*/
	private class NavHandler implements View.OnClickListener
	{
		private final Calendar myDate;
		private final Activity activity;
		public NavHandler(Calendar date, Activity activity)
		{
			this.myDate = date;
			this.activity = activity;
		}
		
		@Override public void onClick(View v)
		{
			date = myDate;
			new LoadTask(myDate, intentRestaurantId(), activity).execute();
		}
	}
	
	
	/**Handles selection events from the graph.*/
	private class SelectionHandler implements CrowdGraph.Listener
	{
		@Override public void onSelectedClosed(Calendar time){focus(time, CrowdLevel.closed);}

		@Override public void onSelectedBar(int dataIndex)
		{
			focus(day.values.get(dataIndex).first, CrowdLevel.from(day.values.get(dataIndex).second));
		};
	}
	
	
	
	/**Handles zooming the graph.
	 * Listens for clicks on the zoom-in and zoom-out buttons.*/
	private class ZoomHandler implements View.OnClickListener
	{
		@Override public void onClick(View v)
		{
			if(v == zoomIn)
				graph.setScaleX(2 * graph.getScaleX());
			else if(v == zoomOut)
				graph.setScaleX(graph.getScaleX() / 2);
			else if(v == zoomLeft)
				graph.setScrollX(graph.getScrollX() - 150);
			else if(v == zoomRight)
				graph.setScrollX(graph.getScrollX() + 150);
		}
	}
}
