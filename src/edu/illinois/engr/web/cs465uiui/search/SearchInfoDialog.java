package edu.illinois.engr.web.cs465uiui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Restaurant;
import edu.illinois.engr.web.cs465uiui.Tag;

/**
 * Activity to display screen when user click on the map button in
 * the search results screen (SearchableActivity)
 */
public class SearchInfoDialog extends DialogFragment {
	private Restaurant restaurant;
	private Context context;
	
	public SearchInfoDialog(Restaurant restaurant, Context context) {
		this.context = context;
		this.restaurant = restaurant;
	}


	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View infoView = inflater.inflate(R.layout.search_info, null);
		ImageButton mapButton = (ImageButton)infoView.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearchMapActivity();
			}

			private void startSearchMapActivity() {
				Intent intent = new Intent(context, SearchMapActivity.class);
				intent.putExtra("restaurantName", restaurant.name);
				intent.putExtra("restaurantAddress", restaurant.location);
				context.startActivity(intent);
			}
        });
		
		String message = restaurant.location + "\n\nTags: ";
		for(Tag tag : restaurant.tags) {
			message += tag.name + ", ";
		}
		message = message.substring(0, message.length() - 2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
        .setTitle(restaurant.name)
        .setView(infoView)
        .setMessage(message);
		
        return builder.create();
    }

}
