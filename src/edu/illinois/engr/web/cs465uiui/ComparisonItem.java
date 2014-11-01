package edu.illinois.engr.web.cs465uiui;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

public class ComparisonItem {
	
	public static final Map<String, Integer> crowdednessMap; 
	public static final Map<String, Integer> colorMap;
	
	static {
		crowdednessMap = new HashMap<String, Integer>();
		crowdednessMap.put("EMPTY", 0);
		crowdednessMap.put("LIGHT", 1);
		crowdednessMap.put("CROWDED", 2);
		crowdednessMap.put("PACKED", 3);
		
		colorMap = new HashMap<String, Integer>();
		colorMap.put("EMPTY", Color.rgb(0, 255, 0));		// green
		colorMap.put("LIGHT", Color.rgb(255, 255, 0));		// yellow
		colorMap.put("CROWDED", Color.rgb(255, 150, 0));	// orange
		colorMap.put("PACKED", Color.rgb(255, 0, 0));		// red
	}
	
	
	private String restaurantName;
	private String restaurantAddress;
	private String distance;
	private String crowdedness;
	
	public ComparisonItem(String restaurantName, String restaurantAddress, String distance, String crowdedness) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.distance = distance;
		this.crowdedness = crowdedness;
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public String getRestaurantAddress() {
		return restaurantAddress;
	}
	
	public String getDistance() {
		return distance;
	}
	
	public String getCrowdedness() {
		return crowdedness;
	}
	
	public boolean equals(ComparisonItem other) {
		return restaurantName.equals(other.getRestaurantName()) && restaurantAddress.equals(other.getRestaurantAddress()) && distance.equals(other.getDistance()) && crowdedness.equals(other.getCrowdedness());
	}
	
	public static int compareDistance(ComparisonItem lhs, ComparisonItem rhs) {
		double lhsDistance = Double.parseDouble(lhs.getDistance().substring(0, lhs.getDistance().indexOf("mi")));
		double rhsDistance = Double.parseDouble(rhs.getDistance().substring(0, rhs.getDistance().indexOf("mi")));
		
		if (lhsDistance - rhsDistance < 0.0) 
			return -1;
		else if (lhsDistance - rhsDistance > 0.0)
			return 1;
		return 0;
	}
	
	public static int compareCrowdedness(ComparisonItem lhs, ComparisonItem rhs) {
		return ComparisonItem.crowdednessMap.get(lhs.getCrowdedness()) - ComparisonItem.crowdednessMap.get(rhs.getCrowdedness());
	}
}
