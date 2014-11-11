package edu.illinois.engr.web.cs465uiui.ui;

import static edu.illinois.engr.web.cs465uiui.utils.HSV.hsv;

/**Internally, crowdedness is a floating-point number in [0,1].
 * This enum breaks that number line up into discrete levels to display to users.
 * The closed value corresponds to a null crowdedness elsewhere.*/
public enum CrowdLevel
{
	packed ("Packed", hsv(0, .65f, .8f), "Extremely crowded; expect a very slow meal."),
	crowded ("Crowded", hsv(25, .65f, .8f), "Quite crowded; good luck finding a seat."),
	medium ("Moderate", hsv(50, .65f, .8f), "Moderately crowded; it'll be OK, but not great."),
	light ("Light", hsv(85, .65f, .8f), "Not crowded at all."),
	empty ("Empty", hsv(215, .65f, .8f), "You'll have the place to yourself."),
	closed ("Closed", hsv(0, 0, .8f), "The restaurant isn't open at this time.");
	
	/**A short name for this crowd level.*/
	public final String name;
	/**An app-wide color to use for this level.*/
	public final int color;
	/**A 1-sentence description of what that crowd level means.*/
	public final String description;
	
	private CrowdLevel(String name, int color, String description)
	{
		this.name = name;
		this.color = color;
		this.description = description;
	}
	
	
	
	private static final CrowdLevel[] nonClosed = {empty, light, medium, crowded, packed};
	/**Gets a crowdedness value for a floating point number.*/
	public static CrowdLevel from(float crowdedness)
	{
		//TODO does this algorithm work? (consider unit testing)
		int index = (int)Math.floor(crowdedness * nonClosed.length);
		return nonClosed[index];
	}
}
