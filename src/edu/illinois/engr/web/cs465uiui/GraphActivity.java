package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

/**Shows a graph of the crowdedness of a restaurant over a day, and lets the user switch days.
 * The intent used to start this activity must be provided to setup(). TODO not implemented yet*/
public class GraphActivity extends Activity
{
	/**Put a restaurant and initial date into the intent that will be used to start this activity.*/
	public static void setup(Restaurant restaurant, Calendar date, Intent intent)
	{
		intent.putExtra("restaurant", restaurant.id);
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
	
	
	
	/**Null when not loaded.*/
	private Restaurant restaurant;
	/**Null when not loaded.*/
	private Calendar date;
	
	
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		new LoadTask().execute();
	}
	
	
	
	/**Loads crowdedness data from the server on a background thread and then displays it on the UI thread.*/
	private class LoadTask extends AsyncTask<Void, Void, Void>
	{
		
		@Override protected Void doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
