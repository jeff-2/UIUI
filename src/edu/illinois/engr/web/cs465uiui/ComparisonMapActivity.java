package edu.illinois.engr.web.cs465uiui;
import java.util.List;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ComparisonMapActivity extends MainActivity {
	private GoogleMap map; 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comparison_map);
		map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

		// TODO: Get address, time, day, radius, and tags from query screen to filter results
		String address = "Green & Wright Champaign";
		LatLng mapCenter = getCoordinates(address);
		
		List<RestaurantMapItem> restaurants = getRestaurants();
		for(RestaurantMapItem restaurant : restaurants) {
			setRestaurantMarker(restaurant);
		}
        Marker start = map.addMarker(new MarkerOptions()
			        .icon(BitmapDescriptorFactory.defaultMarker())
			        .position(mapCenter)
			        .flat(false)
			        .title("Start Location"));
	    start.showInfoWindow();
	    
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 15));
	    map.setMyLocationEnabled(true);
	}
	
	private LatLng getCoordinates(String address) {
    	try {
			return new GeoCoordinates().execute(address).get();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Helper function to set a Marker on the GoogleMap with the 
	 * location and information from the restaurant
	 * @param restaurant The RestaurantMapItem to put on the map
	 */
	private void setRestaurantMarker(RestaurantMapItem restaurant) {
		int resourceID = getResourceID(restaurant.getCrowdedness());
		LatLng markerPosition = new LatLng(restaurant.getLatitude(),restaurant.getLongitude());
		String title = restaurant.getName();
		String snippet = restaurant.getCrowdedness();
		
        map.addMarker(new MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(resourceID))
        .position(markerPosition)
        .flat(false)
        .title(title)
        .snippet(snippet));
	}
	
	/**
	 * Gets the proper R.drawable resource representing a picture used for a 
	 * GoogleMap marker. Picture depends on the crowdedness of the restaurant. 
	 * @param crowdedness A string representing the crowdedness of the restaurant
	 * @return An int representing a resource from the R.drawable class 
	 */
	private int getResourceID(String crowdedness) {
		switch(crowdedness) {
			case "EMPTY":
				return R.drawable.restaurant_green;
			case "LIGHT":
				return R.drawable.restaurant_yellow;
			case "CROWDED":
				return R.drawable.restaurant_orange;
			case "PACKED":
				return R.drawable.restaurant_red;
			default:
				return 0;
		}
	}
	
	/**
	 * Returns a List of RestaurantMapItems with information pulled from
	 * the remote database
	 */
	private List<RestaurantMapItem> getRestaurants() {
		try {
			return new ComparisonMapQuery().execute().get();
		} catch (Exception e) {
			return null;
		}
	}

}
