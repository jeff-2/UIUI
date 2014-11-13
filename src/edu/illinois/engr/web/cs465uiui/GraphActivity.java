package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.net.ServerResult;
import edu.illinois.engr.web.cs465uiui.ui.CrowdGraph;
import edu.illinois.engr.web.cs465uiui.ui.UIFetch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
	
	
	
	private TextView name, location;
	private CrowdGraph graph;
	
	
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		graph = (CrowdGraph)findViewById(R.id.act_graph_graph);
		name = (TextView)findViewById(R.id.act_graph_name);
		location = (TextView)findViewById(R.id.act_graph_location);
		
		new LoadTask(intentDate(), intentRestaurantId(), this).execute();
	}
	
	
	
	/**Loads crowdedness data from the server on a background thread and then displays it on the UI thread.*/
	private class LoadTask extends AsyncTask<Void, Void, ServerResult<List<Float>>>
	{
		private final Calendar date;
		private final long id;
		private final Activity activity;
		
		//acquired in background thread
		private Restaurant restaurant;
		
		public LoadTask(Calendar date, long restaurantID, Activity activity)
		{
			this.date = date;
			this.id = restaurantID;
			this.activity = activity;
		}
		
		@Override protected ServerResult<List<Float>> doInBackground(Void... params)
		{
			ServerResult<Restaurant> result = UIFetch.restaurant(id);
			if(result.success)
				restaurant = result.result;
			else
				return new ServerResult<>(result.error);//XXX kludgy
				
			return UIFetch.crowdednessOn(date, id);
		}
		
		@Override protected void onPostExecute(ServerResult<List<Float>> result)
		{
			if(result.success)
			{
				name.setText(restaurant.name);
				location.setText(restaurant.location);
				graph.setData(result.result);
			}
			else
				UIFetch.explainError(result.error, activity);
		}
	}
}
