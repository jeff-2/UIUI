package edu.illinois.engr.web.cs465uiui.search;

import java.util.List;

import android.app.Activity;
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
import edu.illinois.engr.web.cs465uiui.R;

/**
 * SearchListArrayAdapter provides an ArrayAdapter backing for the SearchActivity containing SearchItems.
 */
public class SearchListArrayAdapter extends ArrayAdapter<SearchItem> {
	
	/** The context. */
	private Context context;
	
	/**
	 * ViewHolder holds the data associated with this view.
	 */
	private static class ViewHolder {
		public TextView name;
		public TextView address;
		public ImageButton info;
	}

	/**
	 * Instantiates a new search list array adapter.
	 *
	 * @param context the context
	 * @param items the items
	 */
	public SearchListArrayAdapter(Context context, List<SearchItem> items) {
		super(context, R.layout.search_row, items);
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	public boolean isEnabled(int position) {
		return !("No restaurants match your search".equals(getItem(position).getRestaurantName()));
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		ViewHolder viewHolder;
		
		// Utilize ViewHolder pattern so that the layout is only inflated when required
		if (rowView == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        rowView = inflater.inflate(R.layout.search_row, parent, false);
	        
	        viewHolder = new ViewHolder();
	        viewHolder.name = (TextView) rowView.findViewById(R.id.searchRestaurantName);
	        viewHolder.address = (TextView) rowView.findViewById(R.id.searchRestaurantAddress);
	        viewHolder.info = (ImageButton) rowView.findViewById(R.id.infoButton);
	        
	        rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
        viewHolder.name.setText(getItem(position).getRestaurantName());
        viewHolder.address.setText(getItem(position).getRestaurantAddress());
        
        
        viewHolder.info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String restaurantName = getItem(position).getRestaurantName();
				String restaurantAddress = getItem(position).getRestaurantAddress();
				SearchInfoDialog dialog = new SearchInfoDialog(restaurantName, restaurantAddress, context);
				dialog.show(((Activity) context).getFragmentManager(), null);
				Log.d("SearchListArrayAdapter", "image button clicked. should redirect to map");
			}
        });
        
        // hide map button if no results
        if ("No restaurants match your search".equals(getItem(position).getRestaurantName())) {
        	((LinearLayout)rowView).removeView(viewHolder.info);
        } else if (rowView.findViewById(R.id.infoButton) == null) {
        	((LinearLayout)rowView).addView(viewHolder.info);
        }

        return rowView;
	}


}
