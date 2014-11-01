package edu.illinois.engr.web.cs465uiui;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SearchListArrayAdapter extends ArrayAdapter<SearchItem> {
	
	static class ViewHolder {
		public TextView name;
		public TextView address;
		public ImageButton map;
	}

	public SearchListArrayAdapter(Context context, List<SearchItem> items) {
		super(context, R.layout.search_row, items);
	}
	
	public boolean isEnabled(int position) {
		return !("No restaurants match your search".equals(getItem(position).getRestaurantName()));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		ViewHolder viewHolder;
		
		if (rowView == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        rowView = inflater.inflate(R.layout.search_row, parent, false);
	        
	        viewHolder = new ViewHolder();
	        viewHolder.name = (TextView) rowView.findViewById(R.id.searchRestaurantName);
	        viewHolder.address = (TextView) rowView.findViewById(R.id.searchRestaurantAddress);
	        viewHolder.map = (ImageButton) rowView.findViewById(R.id.searchButton);
	        
	        rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
        viewHolder.name.setText(getItem(position).getRestaurantName());
        viewHolder.address.setText(getItem(position).getRestaurantAddress());
        
        viewHolder.map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: redirect to map (need api call?)
				Log.d("SearchListArrayAdapter", "image button clicked. should redirect to map");
			}
        });
        
        if ("No restaurants match your search".equals(getItem(position).getRestaurantName())) {
        	((LinearLayout)rowView).removeView(viewHolder.map);
        } else if (rowView.findViewById(R.id.searchButton) == null) {
        	((LinearLayout)rowView).addView(viewHolder.map);
        }

        return rowView;
	}

}
