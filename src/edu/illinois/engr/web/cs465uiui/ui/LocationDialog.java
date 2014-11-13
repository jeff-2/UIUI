package edu.illinois.engr.web.cs465uiui.ui;


import edu.illinois.engr.web.cs465uiui.R;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

/**Prompts the user for a string location, then returns the result to its parent,
 * which should implement the Listener interface.
 * Throughout this class, null location means the current GPS location.*/
public class LocationDialog extends DialogFragment
{
	/**Sets up a bundle that will later be used as this dialog's args.
	 * @param initialLocation the location that is initially selected when the dialog opens, possibly null.*/
	public static void setupArgs(String initialLocation, Bundle args)
	{
		if(initialLocation != null)
			args.putString("location", initialLocation);
	}
	
	/**Get the initial location that was provided via this dialog's args, possibly null.*/
	private String initialLocation()
	{
		return getArguments().getString("location");
	}
	
	
	
	/**Whether or not we have sent yet sent a location to the parent activity.*/
	private boolean sent = false;
	
	/**Displays the location that was chosen when this dialog opened.*/
	private TextView originalDisplay;
	private EditText input;
	
	private final Handler handler = new Handler();
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
	{
		View v = inflater.inflate(R.layout.dialog_location, null);
		originalDisplay = (TextView)v.findViewById(R.id.diag_location_current);
		input = (EditText)v.findViewById(R.id.diag_location_input);
		
		originalDisplay.setText(initialLocation() == null ? "(my GPS location)" : initialLocation());
		if(initialLocation() != null)
			input.setText(initialLocation());
		
		v.findViewById(R.id.diag_location_gps).setOnClickListener(handler);
		
		return v;
	}
	
	
	@Override public void onDismiss(DialogInterface diag)
	{
		if(!sent && !input.getText().toString().equals(""))
			fire(input.getText().toString());
		super.onDismiss(diag);
	}
	
	
	/**Send a result to the parent activity (if possible) and close the dialog.
	 * @param chosen can be null*/
	private void fire(String chosen)
	{
		if(getActivity() instanceof Listener)
			((Listener)getActivity()).onLocationPicked(chosen);
		else
			Log.w("uiui.ui.LocationDialog", "Couldn't send location to parent activity because it isn't a Listener.");
		sent = true;
		dismiss();
	}
	
	
	
	/**Interface to receive notifications when this dialog closes.*/
	public static interface Listener
	{
		/**Called when the dialog closes.
		 * @param location the chosen location, possibly null*/
		public void onLocationPicked(String location);
	}
	
	
	
	/**Handles clicks on the gps-location button.*/
	private class Handler implements View.OnClickListener
	{
		@Override public void onClick(View v){fire(null);}
	}
}
