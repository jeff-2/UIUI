package edu.illinois.engr.web.cs465uiui.search;

/**
 * The AsyncListener provides an interface for an AsyncTask to
 * provide updates on the progress of its execution.
 *
 * @param <Result> the generic type
 */
public interface AsyncListener<Result> {
	
	/**
	 * Notified before execution of the asynctask.
	 */
	void onPreExecute();
	
	/**
	 * Notified when the asynctask completes execution.
	 *
	 * @param result the result
	 */
	void onPostExecute(Result result);
	
	/**
	 * Notified when the asynctask is cancelled.
	 *
	 * @param result the result
	 */
	void onCancelled(Result result);
}
