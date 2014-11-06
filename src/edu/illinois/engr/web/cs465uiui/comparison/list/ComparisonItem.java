package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

/**
 * ComparisonItem provides storage for the data displayed in the ComparisonListActivity. Each row of the list consists of
 * a restaurant name, address, distance from a given location and a qualitative indication of the crowdedness of the restaurant. Also
 * provides the ability to compare ComparisonItems for use in sorting by distance or crowdedness.
 */
public class ComparisonItem {
	
	/** The Constants representing the Colors used for different crowdedness levels. */
	public static final int GREEN = Color.rgb(0, 255, 0);
	public static final int YELLOW = Color.rgb(255, 255, 0);
	public static final int ORANGE = Color.rgb(255, 150, 0);
	public static final int RED = Color.rgb(255, 0, 0);

	/** The crowdednessMap - a mapping between text and a numerical representation of crowdedness. */
	public static final Map<String, Integer> crowdednessMap;
	
	/** The colorMap - a mapping between text and a color representation of crowdedness. */
	public static final Map<String, Integer> colorMap;

	static {
		crowdednessMap = new HashMap<>();
		crowdednessMap.put("EMPTY", 0);
		crowdednessMap.put("LIGHT", 1);
		crowdednessMap.put("CROWDED", 2);
		crowdednessMap.put("PACKED", 3);

		colorMap = new HashMap<>();
		colorMap.put("EMPTY", GREEN);
		colorMap.put("LIGHT", YELLOW);
		colorMap.put("CROWDED", ORANGE);
		colorMap.put("PACKED", RED);
	}

	/** The restaurant name. */
	private String restaurantName;
	
	/** The restaurant address. */
	private String restaurantAddress;
	
	/** The distance from a specified location. */
	private String distance;
	
	/** The crowdedness. */
	private String crowdedness;

	/**
	 * Instantiates a new comparison item.
	 *
	 * @param restaurantName the restaurant name
	 * @param restaurantAddress the restaurant address
	 * @param distance the distance from a specified location
	 * @param crowdedness the crowdedness
	 */
	public ComparisonItem(String restaurantName, String restaurantAddress, String distance, String crowdedness) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.distance = distance;
		this.crowdedness = crowdedness;
	}

	/**
	 * Gets the restaurant name.
	 *
	 * @return the restaurant name
	 */
	public String getRestaurantName() {
		return restaurantName;
	}

	/**
	 * Gets the restaurant address.
	 *
	 * @return the restaurant address
	 */
	public String getRestaurantAddress() {
		return restaurantAddress;
	}

	/**
	 * Gets the distance from a specified location.
	 *
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * Gets the crowdedness.
	 *
	 * @return the crowdedness
	 */
	public String getCrowdedness() {
		return crowdedness;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof ComparisonItem) {
			ComparisonItem otherItem = (ComparisonItem) other;

			return ((restaurantName == null && otherItem.restaurantName == null) || (restaurantName.equals(otherItem.restaurantName)))
					&& ((restaurantAddress == null && otherItem.restaurantName == null) || (restaurantAddress.equals(otherItem.restaurantAddress)))
					&& ((distance == null && otherItem.distance == null) || (distance.equals(otherItem.distance)))
					&& ((crowdedness == null && otherItem.crowdedness == null) || (crowdedness.equals(otherItem.crowdedness)));
		}
		return false;
	}

	/**
	 * Compare the distance between this ComparisonItem and another one. Returns 1, if this distance is greater, -1 if this
	 * distance is lesser, and 0 if the distances are the same.
	 *
	 * @param other the other ComparisonItem to compare distance with
	 * @return the int representing the relative distance between this ComparisonItem and the other
	 */
	public int compareDistance(ComparisonItem other) {
		if (other == null || distance.indexOf("mi") <= 0 || other.distance.indexOf("mi") <= 0)
			return 1;
		
		double myDistance, otherDistance;
		try {
			myDistance = Double.parseDouble(distance.substring(0, distance.indexOf("mi")));
			otherDistance = Double.parseDouble(other.distance.substring(0,	other.distance.indexOf("mi")));
		} catch (NumberFormatException e) {
			return 1;
		}

		if (myDistance - otherDistance < 0.0)
			return -1;
		else if (myDistance - otherDistance > 0.0)
			return 1;
		return 0;
	}

	/**
	 * Compare the crowdedness between this ComparisonItem and another one. Returns a positive integer, if this crowdedness is greater, 
	 * a negative integer if this crowdedness is lesser, and 0 if the crowdedness of each is the same.
	 *
	 * @param other the other ComparisonItem to compare crowdedness with
	 * @return the int representing the relative crowdedness between this ComparisonItem and the other
	 */
	public int compareCrowdedness(ComparisonItem other) {
		if (other == null || crowdednessMap.get(crowdedness) == null || crowdednessMap.get(other.crowdedness) == null)
			return 1;
		
		return crowdednessMap.get(crowdedness).intValue() - crowdednessMap.get(other.crowdedness).intValue();
	}
}
