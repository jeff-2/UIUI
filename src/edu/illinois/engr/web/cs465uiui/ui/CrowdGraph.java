package edu.illinois.engr.web.cs465uiui.ui;

import java.util.*;

import edu.illinois.engr.web.cs465uiui.CrowdDay;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.utils.Time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**A view that shows a graph of crowdedness over a time period.
 * TODO will Sets its own on-click listener; don't try to catch those events outside.*/
public class CrowdGraph extends View
{
	private final Paint paintBars = new Paint(), paintClosed = new Paint();
	
	
	
	private CrowdDay data = null;
	
	/**Null when we have no data.*/
	private List<Rect> displayBars = null;
	/**How many minutes "wide" each bar is. Invalid until we get real data.*/
	private int barMinutes = -1;
	/**In minutes; invalid until we get data.*/
	private int open, close;
	
	
	
	/**Constructor used by the system; don't use this directly.*/
	public CrowdGraph(Context context, AttributeSet attr)
	{
		super(context, attr);
		paintBars.setColor(context.getResources().getColor(R.color.emphasis));
		paintClosed.setColor(context.getResources().getColor(R.color.light));
	}
	
	
	/**Give this graph a set of values to display.
	 * The graph will display evenly-spaced bars representing the data.
	 * Null entries are interpreted as "closed".*/
	public void setData(CrowdDay day)
	{
		data = day;
		refresh();
	}
	
	@Override public void onSizeChanged(int w, int h, int ow, int oh){refresh();}
	
	
	/**Recomputes the rectangles that make up the displayed bars.*/
	private void refresh()
	{
		if(data == null)
			return;
		
		if(displayBars == null)
			displayBars = new ArrayList<>();
		else
			displayBars.clear();
		
		open = Time.minutes(data.values.get(0).first);
		close = Time.minutes(data.values.get(data.values.size() - 1).first);
		barMinutes = (close - open) / data.values.size();
		
		int openBars = open / barMinutes, closeBars = (24 * 60 - close) / barMinutes;
		int totalBars = openBars + closeBars + data.values.size();
		int barWidth = getWidth() / totalBars;

		for(int c = 0; c < data.values.size(); c++)
		{
			int barHeight = (int)(data.values.get(c).second * getHeight());
			displayBars.add(new Rect((c+openBars) * barWidth, getHeight() - barHeight, (c+openBars+1) * barWidth, getHeight()));
		}
		
		invalidate();
	}
	
	
	@Override public void onDraw(Canvas canvas)
	{
		if(displayBars == null)
			canvas.drawRect(0, 0, getWidth(), getHeight(), paintBars);
		else if(displayBars.isEmpty())
			canvas.drawRect(0, 0, getWidth(), getHeight(), paintClosed);
		else
		{
			//fill open and closed areas
			canvas.drawRect(0, 0, displayBars.get(0).left, getHeight(), paintClosed);
			canvas.drawRect(displayBars.get(displayBars.size() - 1).right, 0, getWidth(), getHeight(), paintClosed);
			
			//fill the actual bars
			for(Rect rect : displayBars)
				canvas.drawRect(rect, paintBars);
		}
	}
	
	
	
	
	/**Interface the parent activity can implement to be notified when things happen to this graph.*/
	public static interface Listener
	{
		/**Called when the user clicks on part of the graph because he wants to see more info about.*/
		public void onRequestInfo(int index);
	}
}
