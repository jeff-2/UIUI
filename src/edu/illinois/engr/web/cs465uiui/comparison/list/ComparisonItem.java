package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ComparisonItem provides storage for the data displayed in the ComparisonListActivity. Each row of the list consists of
 * a restaurant name, address, distance from a given location and a qualitative indication of the crowdedness of the restaurant. Also
 * provides the ability to compare ComparisonItems for use in sorting by distance or crowdedness.
 */
public class ComparisonItem implements Parcelable {
	
	/** The Constants representing the Colors used for different crowdedness levels. */
	public static final int GREEN = Color.rgb(0, 255, 0);
	
	/** The Constant YELLOW. */
	public static final int YELLOW = Color.rgb(255, 255, 0);
	
	/** The Constant ORANGE. */
	public static final int ORANGE = Color.rgb(255, 150, 0);
	
	/** The Constant RED. */
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
	private String restaurantDistance;
	
	/** The crowdedness. */
	private String restaurantCrowdedness;
	
	/** The latitude of the restaurant's location */
	private float restaurantLongitude;
	
	/** The longitude of the restaurant's location */
	private float restaurantLatitude;

	/**
	 * Instantiates a new comparison item.
	 *
	 * @param restaurantName the restaurant name
	 * @param restaurantAddress the restaurant address
	 * @param distance the distance from a specified location
	 * @param crowdedness the crowdedness
	 */
	public ComparisonItem(String restaurantName, String restaurantAddress, String distance, String crowdedness, float restaurantLongitude, float restaurantLatitude) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.restaurantDistance = distance;
		this.restaurantCrowdedness = crowdedness;
		this.restaurantLongitude = restaurantLongitude;
		this.restaurantLatitude = restaurantLatitude;
	}
	
	/**
	 * Instantiates a new comparison item.
	 */
	public ComparisonItem() {
		this.restaurantName = null;
		this.restaurantAddress = null;
		this.restaurantDistance = null;
		this.restaurantCrowdedness = null;
		this.restaurantLongitude = -1.0f;
		this.restaurantLongitude = -1.0f;
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
	public String getRestaurantDistance() {
		return restaurantDistance;
	}

	/**
	 * Gets the crowdedness.
	 *
	 * @return the crowdedness
	 */
	public String getRestaurantCrowdedness() {
		return restaurantCrowdedness;
	}
	
	/**
	 * Gets the latitude of the restaurant location.
	 * 
	 * @return the latitude
	 */
	public float getRestaurantLatitude() {
		return restaurantLatitude;
	}
	
	/**
	 * Gets the longitude of the restaurant location.
	 * 
	 * @return the longitude
	 */
	public float getRestaurantLongitude() {
		return restaurantLongitude;
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
					&& ((restaurantDistance == null && otherItem.restaurantDistance == null) || (restaurantDistance.equals(otherItem.restaurantDistance)))
					&& ((restaurantCrowdedness == null && otherItem.restaurantCrowdedness == null) || (restaurantCrowdedness.equals(otherItem.restaurantCrowdedness)))
					&& restaurantLongitude == otherItem.restaurantLongitude
					&& restaurantLatitude == otherItem.restaurantLatitude;
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
		if (other == null || restaurantDistance.indexOf("mi") <= 0 || other.restaurantDistance.indexOf("mi") <= 0)
			return 1;
		
		double myDistance, otherDistance;
		try {
			myDistance = Double.parseDouble(restaurantDistance.substring(0, restaurantDistance.indexOf("mi")));
			otherDistance = Double.parseDouble(other.restaurantDistance.substring(0,	other.restaurantDistance.indexOf("mi")));
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
		if (other == null || crowdednessMap.get(restaurantCrowdedness) == null || crowdednessMap.get(other.restaurantCrowdedness) == null)
			return 1;
		
		return crowdednessMap.get(restaurantCrowdedness).intValue() - crowdednessMap.get(other.restaurantCrowdedness).intValue();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(restaurantName);
		dest.writeString(restaurantAddress);
		dest.writeString(restaurantDistance);
		dest.writeString(restaurantCrowdedness);
		dest.writeFloat(restaurantLongitude);
		dest.writeFloat(restaurantLatitude);
	}
	
	public static final Parcelable.Creator<ComparisonItem> CREATOR = new Parcelable.Creator<ComparisonItem>() {
		public ComparisonItem createFromParcel(Parcel in) {
		    return new ComparisonItem(in);
		}

		public ComparisonItem[] newArray(int size) {
		    return new ComparisonItem[size];
		}
	};
	
	private ComparisonItem(Parcel in) {
		restaurantName = in.readString();
		restaurantAddress = in.readString();
		restaurantDistance = in.readString();
		restaurantCrowdedness = in.readString();
		restaurantLongitude = in.readFloat();
		restaurantLatitude = in.readFloat();
	} 
}
