package edu.illinois.engr.web.cs465uiui.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.utils.JsonParserImpl;
import edu.illinois.engr.web.cs465uiui.utils.NetworkRequest;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * SearchFragment provides management for the execution of an asynchronous task that can persist
 * across orientation changes of the device. Provides callbacks to the created activity regarding the
 * status of the asynchronous task (onPreExecute, onProgressUpdate, onPostExecute and onCancelled).
 */
public class SearchFragment extends Fragment {
	
	/** The listener to notify with status updates of the asynchronous task being executed. */
	private AsyncListener<List<SearchItem>> listener;
	
	/** The task to execute. */
	private SearchTask task;

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		task = new SearchTask();
		task.execute();
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (AsyncListener<List<SearchItem>>) activity;
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
	private class SearchTask extends AsyncTask<Void, Void, List<SearchItem>> {
		
		/** The Constant requestURL. */
		private static final String requestURL = "http://cs465uiui.web.engr.illinois.edu/search.php";

		
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
		protected List<SearchItem> doInBackground(Void... params) {

			List<SearchItem> queryResults = new ArrayList<>();

			URL url;
			try {
				url = new URL(requestURL);
				NetworkRequest<SearchItem> request = new NetworkRequest<SearchItem>(url, new JsonParserImpl<SearchItem>(SearchItem.class));
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
		protected void onPostExecute(List<SearchItem> results) {
			if (listener != null)
				listener.onPostExecute(results);
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
		 */
		protected void onCancelled(List<SearchItem> results) {
			if (listener != null)
				listener.onCancelled(results);
		}
	}
}
