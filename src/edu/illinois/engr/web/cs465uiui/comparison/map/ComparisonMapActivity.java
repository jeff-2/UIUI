package edu.illinois.engr.web.cs465uiui.comparison.map;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.illinois.engr.web.cs465uiui.MainActivity;
import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.comparison.list.ComparisonItem;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

/**
 * Activity to display screen to compare restaurants on a map
 XXX when the map fails to load, it tries to reconnect endlessly instead of displaying a message*/
public class ComparisonMapActivity extends MainActivity {
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
			} else {
				Toast.makeText(this, "This device is not supported.",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return;
		}

		setContentView(R.layout.comparison_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		List<ComparisonItem> comparisonList = getIntent().getExtras().getParcelableArrayList("comparisonList");
		LatLng mapCenter = new LatLng(0,0);
		// TODO: Get location
		final Query data = QueryData.load(this);
		if (data.position == null) {

			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(),true));
			
			if (lastKnownLocation == null) {
				LocationListener locationListener = new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						locationManager.removeUpdates(this);
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {}

					@Override
					public void onProviderEnabled(String provider) {}

					@Override
					public void onProviderDisabled(String provider) {} 
					
				};
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); 
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			} else {
				mapCenter = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
			}
		} else {
			mapCenter = GeoCoordinates.getCoordinates(data.position);
			
		}
		String address = "Green & Wright Champaign";

		for (ComparisonItem restaurant : comparisonList) {
			setRestaurantMarker(restaurant);
		}
		Marker start = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.defaultMarker())
				.position(mapCenter).flat(false).title("Start Location"));
		start.showInfoWindow();

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
		map.setMyLocationEnabled(true);
		
		Button listButton = (Button)this.findViewById(R.id.ListButton);
		listButton.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View v) {
		    finish();
		  }
		});
	}

	/**
	 * Helper function to set a Marker on the GoogleMap with the location and
	 * information from the restaurant
	 * 
	 * @param restaurant
	 *            The RestaurantMapItem to put on the map
	 */
	private void setRestaurantMarker(ComparisonItem restaurant) {
		int resourceID = getResourceID(restaurant.getRestaurantCrowdedness());
		LatLng markerPosition = new LatLng(restaurant.getRestaurantLatitude(),
				restaurant.getRestaurantLongitude());
		String title = restaurant.getRestaurantName();
		String snippet = restaurant.getRestaurantCrowdedness();

		map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(resourceID))
				.position(markerPosition).flat(false).title(title)
				.snippet(snippet));
	}

	/**
	 * Gets the proper R.drawable resource representing a picture used for a
	 * GoogleMap marker. Picture depends on the crowdedness of the restaurant.
	 * 
	 * @param crowdedness
	 *            A string representing the crowdedness of the restaurant
	 * @return An int representing a resource from the R.drawable class
	 */
	private int getResourceID(String crowdedness) {
		switch (crowdedness) {
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

}
