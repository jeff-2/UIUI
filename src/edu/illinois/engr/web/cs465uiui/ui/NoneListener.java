package edu.illinois.engr.web.cs465uiui.ui;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**A listener that does nothing.
 * Useful when recycling views (in ListViews) if you want to edit a component without firing its listeners.*/
public class NoneListener implements OnCheckedChangeListener
{
	/**A static instance of this class--no need to create multiple.*/
	public static final NoneListener INSTANCE = new NoneListener();
	
	@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){}
}
