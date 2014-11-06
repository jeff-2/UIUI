package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.util.List;


import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.R.id;
import edu.illinois.engr.web.cs465uiui.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ComparisonListArrayAdapter extends ArrayAdapter<ComparisonItem> {
	
	static class ViewHolder {
		public TextView name;
		public TextView address;
		public TextView distance;
		public TextView crowdedness;
	}

	public ComparisonListArrayAdapter(Context context, List<ComparisonItem> items) {
		super(context, R.layout.comparison_row, items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		ViewHolder viewHolder;
		
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
