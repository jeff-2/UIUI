package edu.illinois.engr.web.cs465uiui.search;

/**
 * SearchItem provides storage for the data displayed in the SearchActivity. Each row of the results from the search displays a
 * restaurantName and address, as well as an icon for verifying the location on a map.
 */
public class SearchItem {
	
	private long restaurantId;
	
	/** The restaurant name. */
	private String restaurantName;
	
	/** The restaurant address. */
	private String restaurantAddress;
	
	/**
	 * Instantiates a new search item.
	 */
	public SearchItem() {
		restaurantId = -1;
		restaurantName = null;
		restaurantAddress = null;
	}
	
	/**
	 * Instantiates a new search item.
	 *
	 * @param restaurantName the restaurant name
	 * @param restaurantAddress the restaurant address
	 */
	public SearchItem(long restaurantId, String restaurantName,String restaurantAddress) {
		this.restaurantId = restaurantId;
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public String getRestaurantAddress() {
		return restaurantAddress;
	}
	
	public long getRestaurantId() {
		return restaurantId;
	}
}
