package edu.illinois.engr.web.cs465uiui.search;

/**
 * SearchItem provides storage for the data displayed in the SearchActivity. Each row of the results from the search displays a
 * restaurantName and address, as well as an icon for verifying the location on a map. 
 */
public class SearchItem {
	
	/** The restaurant name. */
	private String restaurantName;
	
	/** The restaurant address. */
	private String restaurantAddress;
	
	/**
	 * Instantiates a new search item.
	 */
	public SearchItem() {
		restaurantName = null;
		restaurantAddress = null;
	}
	
	/**
	 * Instantiates a new search item.
	 *
	 * @param restaurantName the restaurant name
	 * @param restaurantAddress the restaurant address
	 */
	public SearchItem(String restaurantName,String restaurantAddress) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
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
}
