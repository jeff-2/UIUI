package edu.illinois.engr.web.cs465uiui.ui;

import java.util.*;

import edu.illinois.engr.web.cs465uiui.CrowdDay;
import edu.illinois.engr.web.cs465uiui.utils.HSV;

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
	private static final Paint paint = new Paint();
	static {paint.setColor(HSV.hsv(90, .5f, .75f));}
	
	
	
	private CrowdDay data = null;
	
	/**Empty when no data is present.*/
	private List<Rect> displayBars = new ArrayList<>();
	
	
	
	/**Constructor used by the system; don't use this directly.*/
	public CrowdGraph(Context context, AttributeSet attr){super(context, attr);}
	
	
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
		int widthPerBar = getWidth() / data.values.size();
		
		displayBars.clear();
		for(int c = 0; c < data.values.size(); c++)
		{
			Float value = data.values.get(c).second;
			if(value == null)
				continue;
			int barHeight = (int)(getHeight() * value);
			displayBars.add(new Rect(c * widthPerBar, getHeight() - barHeight, (c+1) * widthPerBar, getHeight()));
		}
		invalidate();
	}
	
	
	@Override public void onDraw(Canvas canvas)
	{
		if(displayBars == null)
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		else
			for(Rect rect : displayBars)
				canvas.drawRect(rect, paint);
	}
	
	
	
	
	/**Interface the parent activity can implement to be notified when things happen to this graph.*/
	public static interface Listener
	{
		/**Called when the user clicks on part of the graph because he wants to see more info about.*/
		public void onRequestInfo(int index);
	}
}
