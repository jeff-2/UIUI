package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import edu.illinois.engr.web.cs465uiui.search.AsyncListener;
import edu.illinois.engr.web.cs465uiui.utils.JsonParserImpl;
import edu.illinois.engr.web.cs465uiui.utils.NetworkRequest;

/**
 * ComparisonFragment provides management for the execution of an asynchronous task that can persist
 * across orientation changes of the device. Provides callbacks to the created activity regarding the
 * status of the asynchronous task (onPreExecute, onProgressUpdate, onPostExecute and onCancelled).
 */
public class ComparisonFragment extends Fragment {
	/** The listener to notify with status updates of the asynchronous task being executed. */
	private AsyncListener<List<ComparisonItem>> listener;
	
	/** The task to execute. */
	private ComparisonTask task;

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		task = new ComparisonTask();
		Bundle bundle = (Bundle)getArguments();
		String queryString = bundle.getString("queryString");
		task.execute(queryString);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (AsyncListener<List<ComparisonItem>>) activity;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		listener = null;
		if (task != null)
			task.cancel(true);
	}
	
	/**
	 * SearchTask provides an asynchronous task which queries the server for restaurants and parses the response,
	 * putting the result into the restaurants list.
	 */
	private class ComparisonTask extends AsyncTask<String, Void, List<ComparisonItem>> {
		
		/** The Constant requestURL. */
		private static final String requestURL = "http://cs465uiui.web.engr.illinois.edu/query.php";

		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			if (listener != null)
				listener.onPreExecute();
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected List<ComparisonItem> doInBackground(String... params) {

			List<ComparisonItem> queryResults = new ArrayList<>();
			String queryString = params[0];

			URL url;
			try {
				url = new URL(requestURL + queryString);
				Log.d("url=", requestURL + queryString);
				NetworkRequest<ComparisonItem> request = new NetworkRequest<ComparisonItem>(url, new JsonParserImpl<ComparisonItem>(ComparisonItem.class));
				queryResults = request.sendAndReceive();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			return queryResults;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(List<ComparisonItem> results) {
			if (listener != null)
				listener.onPostExecute(results);
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
		 */
		protected void onCancelled(List<ComparisonItem> results) {
			if (listener != null)
				listener.onCancelled(results);
		}
	}
}
