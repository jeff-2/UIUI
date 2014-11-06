package edu.illinois.engr.web.cs465uiui.comparison.map;

/**
 * Class used to represent a restaurant for the ComparisonMapActivity
 */
public class RestaurantMapItem {
	private String name;
	private String address;
	private String crowdedness;
	private String day;
	private String time;
	private double latitude;
	private double longitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCrowdedness() {
		return crowdedness;
	}
	public void setCrowdedness(String crowdedness) {
		this.crowdedness = crowdedness;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	
}
