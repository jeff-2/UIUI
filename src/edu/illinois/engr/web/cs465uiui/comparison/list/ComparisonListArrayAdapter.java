package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.List;


import edu.illinois.engr.web.cs465uiui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * ComparisonListArrayAdapter provides an ArrayAdapter backing for the ComparisonListActivity containing ComparisonItems.
 */
public class ComparisonListArrayAdapter extends ArrayAdapter<ComparisonItem> {
	
	/**
	 * ViewHolder holds the data associated with this view.
	 */
	private static class ViewHolder {
		
		public TextView name;
		public TextView address;
		public TextView distance;
		public TextView crowdedness;
	}

	/**
	 * Instantiates a new comparison list array adapter.
	 *
	 * @param context the context
	 * @param items the items
	 */
	public ComparisonListArrayAdapter(Context context, List<ComparisonItem> items) {
		super(context, R.layout.comparison_row, items);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		ViewHolder viewHolder;
		
		// Utilize ViewHolder pattern so that the layout is only inflated when required
		if (rowView == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        rowView = inflater.inflate(R.layout.comparison_row, parent, false);
	        
	        viewHolder = new ViewHolder();
	        viewHolder.name = (TextView) rowView.findViewById(R.id.comparisonRestaurantName);
	        viewHolder.address = (TextView) rowView.findViewById(R.id.comparisonRestaurantAddress);
	        viewHolder.distance = (TextView) rowView.findViewById(R.id.comparisonDistance);
	        viewHolder.crowdedness = (TextView) rowView.findViewById(R.id.comparisonCrowdedness);
	        
	        rowView.setTag(viewHolder);
	        
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
        viewHolder.name.setText(getItem(position).getRestaurantName());
        viewHolder.address.setText(getItem(position).getRestaurantAddress());
        viewHolder.distance.setText(getItem(position).getDistance());
        viewHolder.crowdedness.setText(getItem(position).getCrowdedness());
        viewHolder.crowdedness.setTextColor(ComparisonItem.colorMap.get(getItem(position).getCrowdedness()));

        return rowView;
	}
}
