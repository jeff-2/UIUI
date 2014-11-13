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

/**
 * Activity to display screen when user click on the map button in
 * the search results screen (SearchableActivity)
 */
public class SearchInfoDialog extends DialogFragment {
	private String restaurantName;
	private String restaurantAddress;
	private Context context;
	
	public SearchInfoDialog(String restaurantName, String restaurantAddress, Context context) {
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.context = context;
	}
	
	

    public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.search_info, null);
		ImageButton mapButton = (ImageButton)v.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearchMapActivity();
			}

			private void startSearchMapActivity() {
				Intent intent = new Intent(context, SearchMapActivity.class);
				intent.putExtra("restaurantName", restaurantName);
				intent.putExtra("restaurantAddress", restaurantAddress);
				context.startActivity(intent);
			}
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
        .setTitle(restaurantName)
        .setView(v)
        .setMessage(restaurantAddress + "\n\nTags: Pizza");
		
        return builder.create();
    }

}
