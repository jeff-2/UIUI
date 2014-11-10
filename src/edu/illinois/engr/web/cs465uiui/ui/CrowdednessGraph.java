package edu.illinois.engr.web.cs465uiui.ui;

import java.util.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**A view that shows a graph of crowdedness over a day. TODO not implemented yet*/
public class CrowdednessGraph extends View
{
	private List<CrowdLevel> data = new ArrayList<>();
	
	
	/**Constructor used by the Android OS; I have no idea what the args do.*/
	public CrowdednessGraph(Context context, AttributeSet attr, int a, int b){super(context, attr, a, b);}
	
	
	
	
	/**Interface the parent activity can implement to be notified when things happen to this graph.*/
	public static interface Listener
	{
		/**Called when the user clicks on part of the graph because he wants to see more info about.*/
		public void onRequestInfo();
	}
}
