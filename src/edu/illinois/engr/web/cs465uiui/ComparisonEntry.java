package edu.illinois.engr.web.cs465uiui;

import edu.illinois.engr.web.cs465uiui.ui.CrowdLevel;

/**An entry returned by the server in response to a Query.*/
public class ComparisonEntry
{
	public final Restaurant restaurant;
	/**@see {@link CrowdLevel}*/
	public final float crowdedness;
	
	public ComparisonEntry(Restaurant restaurant, float crowdedness)
	{
		this.restaurant = restaurant;
		this.crowdedness = crowdedness;
	}
}
