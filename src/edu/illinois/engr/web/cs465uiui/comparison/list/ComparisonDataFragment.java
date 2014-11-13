package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;

/**
 * ComparisonDataFragment provides a way to store data that persists across orientation changes in ComparisonListActivity. Retains
 * a list of restaurants that are returned from the user's query.
 */
public class ComparisonDataFragment extends Fragment {
	/** The restaurants. */
    private List<ComparisonItem> restaurants;

    /* (non-Javadoc)
     * @see android.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Sets the restaurants.
     *
     * @param restaurants the new restaurants
     */
    public void setRestaurants(List<ComparisonItem> restaurants) {
        this.restaurants = restaurants;
    }

    /**
     * Gets the restaurants.
     *
     * @return the restaurants
     */
    public List<ComparisonItem> getRestaurants() {
        return restaurants;
    }
}
