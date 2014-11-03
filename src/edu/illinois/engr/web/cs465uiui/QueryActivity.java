package edu.illinois.engr.web.cs465uiui;

import android.app.Activity;
import android.os.Bundle;

/**Lets the user set up a query that will be used on the comparison screens.
 * Lets the user enter the time when they want to visit a restaurant,
 * the tags they're looking for,
 * and the location they're looking for restaurants near.*/
public class QueryActivity extends Activity
{
	
	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);
	}
	
}
