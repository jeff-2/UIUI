package edu.illinois.engr.web.cs465uiui.ui;

import java.util.Calendar;
import static java.util.Calendar.*;

import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.text.DateDisplay;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

/**Prompts the user for a time, then returns the result to its parent,
 * which should implement the Listener interface.*/
public class TimeDialog extends DialogFragment
{
	/**Sets up a bundle that will later be used as this dialog's args.*/
	public static void setupArgs(Calendar calendar, Bundle bundle)
	{
		if(calendar == null)
			bundle.putString("null", null);//value doesn't matter
		else
			bundle.putLong("time", calendar.getTimeInMillis());
	}
	
	/**Loads values out of this dialog's arguments bundle.*/
	private void loadArgs()
	{
		Bundle args = getArguments();
		if(args == null)
			Log.w("uiui.ui.TimeDialog", "Args weren't expected to be null");
		else if(args.containsKey("null"))
		{
			Calendar now = Calendar.getInstance();
			date.init(now.get(YEAR), now.get(MONTH), now.get(DATE), handler);
		}
		else if(args.containsKey("time"))
		{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(args.getLong("time"));
			date.init(cal.get(YEAR), cal.get(MONTH), cal.get(DATE), handler);
			time.setCurrentHour(cal.get(HOUR_OF_DAY));
			time.setCurrentMinute(cal.get(MINUTE));
		}
		else
			Log.w("uiui.ui.TimeDialog", "Arguments weren't set up correctly");
	}
	
	
	
	/**False until the user makes some sort of change in the date/time pickers.
	 * When false, the user's selection is a null Calendar.*/
	private boolean hasSelectedActual = false;
	/**Whether or not we have sent a time to the parent activity.*/
	private boolean sent = false;
	
	/**Displays the user's current selection.
	 * Update via refresh() whenever the user's selection changes (even by 1 minute).*/
	private TextView currentDisplay;
	private DatePicker date;
	private TimePicker time;
	
	private final Handler handler = new Handler();
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
	{
		View v = inflater.inflate(R.layout.dialog_query_time, null);
		currentDisplay = (TextView)v.findViewById(R.id.diag_qtime_current);
		date = (DatePicker)v.findViewById(R.id.diag_qtime_date);
		time = (TimePicker)v.findViewById(R.id.diag_qtime_time);
		
		v.findViewById(R.id.diag_qtime_now).setOnClickListener(handler);
		time.setOnTimeChangedListener(handler);
		
		if(saved == null)
			loadArgs();
		else
			;//TODO saving and loading state
		refresh();
		return v;
	}
	
	
	@Override public void onDismiss(DialogInterface diag)
	{
		if(!sent)
		{
			if(getActivity() instanceof Listener)
				((Listener)getActivity()).onTimeNoPick();
			else
				Log.w("uiui.ui.TimeDialog", "Could not send chosen time because parent isn't a Listener");
		}
		super.onDismiss(diag);
	}
	
	
	/**Updates the display of the current selection based on the user's choices.*/
	private void refresh()
	{
		if(hasSelectedActual)
			currentDisplay.setText(DateDisplay.full(fromWidgets()));
		else
			currentDisplay.setText("(none)");
	}
	
	/**Close this dialog and provide a Calendar result to the parent activity
	 * based on hasSelectedActual and the widgets in this activity.*/
	private void done()
	{
		Calendar calendar = hasSelectedActual ? fromWidgets() : null;
		if(getActivity() instanceof Listener)
			((Listener)getActivity()).onTimePicked(calendar);
		else
			Log.w("uiui.ui.TimeDialog", "Cannot send chosen time because parent isn't a Listener");
		sent = true;
		dismiss();
	}
	
	/**Turns the current state of the date and time widgets into a Calendar.*/
	private Calendar fromWidgets()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(YEAR,  date.getYear());
		calendar.set(MONTH, date.getMonth());
		calendar.set(DATE, date.getDayOfMonth());
		calendar.set(HOUR_OF_DAY, time.getCurrentHour());
		calendar.set(MINUTE, time.getCurrentMinute());
		return calendar;
	}
	
	
	
	/**Interface to receive notifications when this dialog closes.*/
	public static interface Listener
	{
		/**Called when the dialog closes with no result chosen.*/
		public void onTimeNoPick();
		/**Called when the user chooses a time.
		 * @param time null if the user chose to clear their selection*/
		public void onTimePicked(Calendar time);
	}
	
	
	/**Handles changes in the date and time picking widgets.
	 * Also handles clicks on the "now" button.*/
	private class Handler implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener,
			View.OnClickListener
	{
		@Override public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
		{
			hasSelectedActual = true;
			refresh();
		}

		@Override public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			hasSelectedActual = true;
			refresh();
			Log.d("----", "date changed");
		}
		
		/*called for the "now button"*/
		@Override public void onClick(View v)
		{
			hasSelectedActual = false;
			done();
		}
	}
}
