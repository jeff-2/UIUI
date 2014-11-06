package edu.illinois.engr.web.cs465uiui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**A set of criteria for restaurants the user wants to compare.*/
public class Query
{
	/**What date and time the user is interested in; null means right now.*/
	public Calendar time;
	
	/*What tags the user is interested in.*/
	public List<Tag> tags;
	
	/**Whether the user is looking for restaurants with all of these tags, as opposed to any of them.*/
	public boolean allTags;
	
	/**What location the user is interested in, or null for the current GPS location.*/
	public String location;
	
	/**The user wants results within this distance of the central position. Must be positive.*/
	public int radiusMiles;
	
	
	/**Creates a query with the default values.*/
	public Query(){this(null, new ArrayList<Tag>(), false, null, 4);}
	
	public Query(Calendar time, List<Tag> tags, boolean allTags, String location, int radiusMiles)
	{
		this.time = time;
		this.tags = tags;
		this.allTags = allTags;
		this.location = location;
		this.radiusMiles = radiusMiles;
	}
}
