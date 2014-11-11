package edu.illinois.engr.web.cs465uiui.ui;

import java.util.*;

import edu.illinois.engr.web.cs465uiui.utils.HSV;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**A view that shows a graph of crowdedness over a day.*/
public class CrowdGraph extends View
{
	private static final Paint paint = new Paint();
	static {paint.setColor(HSV.hsv(0, .5f, .5f));}
	
	
	
	private List<Float> data = new ArrayList<>();
	
	/**Empty when no data is present.*/
	private List<Rect> displayBars = new ArrayList<>();
	
	
	
	/**Constructor used by the system; don't use this directly.*/
	public CrowdGraph(Context context, AttributeSet attr){super(context, attr);}
	
	
	/**Give this graph a set of values to display.
	 * The graph will display evenly-spaced bars representing the data.
	 * Null entries are interpreted as "closed".*/
	public void setData(List<Float> newData)
	{
		data = new ArrayList<>(newData);
		refresh();
	}
	
	@Override public void onSizeChanged(int w, int h, int ow, int oh){refresh();}
	
	/**Recomputes the rectangles that make up the displayed bars.*/
	private void refresh()
	{
		int widthPerBar = getWidth() / data.size();
		
		displayBars.clear();
		for(int c = 0; c < data.size(); c++)
		{
			Float value = data.get(c);
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
