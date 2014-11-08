package edu.illinois.engr.web.cs465uiui.search;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;

/**
 * SearchDataFragment provides a way to store data that persists across orientation changes in SearchActivity. Retains
 * a list of restaurants that are used in searching.
 */
public class SearchDataFragment extends Fragment {
	
    /** The restaurants. */
    private List<SearchItem> restaurants;

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
    public void setRestaurants(List<SearchItem> restaurants) {
        this.restaurants = restaurants;
    }

    /**
     * Gets the restaurants.
     *
     * @return the restaurants
     */
    public List<SearchItem> getRestaurants() {
        return restaurants;
    }
}
