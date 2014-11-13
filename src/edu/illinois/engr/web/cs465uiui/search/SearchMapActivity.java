package edu.illinois.engr.web.cs465uiui.search;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.comparison.map.GeoCoordinates;

/**
 * Activity to display screen when user click on the map button in
 * the search results screen (SearchableActivity)
 */
public class SearchMapActivity extends FragmentActivity {
	private GoogleMap map; 
	private LocationManager locationManager;
	private LocListener locationListener;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_map);
		Bundle extras = getIntent().getExtras();
		String restaurantName = extras.getString("restaurantName");
		String restaurantAddress = extras.getString("restaurantAddress");
		
		map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

		LatLng mapCenter = GeoCoordinates.getCoordinates(restaurantAddress);
        Marker start = map.addMarker(new MarkerOptions()
			        .icon(BitmapDescriptorFactory.defaultMarker())
			        .position(mapCenter)
			        .flat(false)
			        .title(restaurantName)
			        .snippet(restaurantAddress));
	    start.showInfoWindow();
	    
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 15));
	    map.setMyLocationEnabled(true);
	    
	    /** Get Current Location Test **/
	    locationListener = new LocListener();
	    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    if(currentLocation != null) {
	    	System.out.println(currentLocation.getLatitude());
		    System.out.println(currentLocation.getLongitude());
	    }
	}

	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
		
	}
	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}
}
