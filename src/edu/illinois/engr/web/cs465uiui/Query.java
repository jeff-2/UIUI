package edu.illinois.engr.web.cs465uiui;

import java.util.Calendar;

/**A set of criteria for restaurants the user wants to compare.*/
public class Query
{
	/**What date and time the user is interested in; null means right now.*/
	public Calendar time = null;
	
	/*TODO what tags the user is interested in.*/
	
	/**What location the user is interested in, or null for the current GPS location.*/
	public String location = null;
	
	/**The user wants results within this distance of the central position. Must be positive.*/
	public int radiusMiles = 4;
}
