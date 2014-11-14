package edu.illinois.engr.web.cs465uiui.comparison.list;

import com.google.android.gms.maps.model.LatLng;

import android.app.Fragment;
import android.os.Bundle;

public class PositionDataFragment extends Fragment {
	/** The current gps location latitude and longitude. */
    private LatLng position;

    /* (non-Javadoc)
     * @see android.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    /**
     * Sets the lat lng.
     *
     * @param position the new lat lng
     */
    public void setLatLng(LatLng position) {
        this.position = position;
    }

    /**
     * Gets the restaurants.
     *
     * @return the restaurants
     */
    public LatLng getLatLng() {
        return position;
    }
}
