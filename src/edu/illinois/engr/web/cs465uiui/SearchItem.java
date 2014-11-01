package edu.illinois.engr.web.cs465uiui;

public class SearchItem {
	
	private String restaurantName;
	private String restaurantAddress;
	
	public SearchItem() {
		restaurantName = null;
		restaurantAddress = null;
	}
	
	public SearchItem(String restaurantName,String restaurantAddress) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public String getRestaurantAddress() {
		return restaurantAddress;
	}
}
