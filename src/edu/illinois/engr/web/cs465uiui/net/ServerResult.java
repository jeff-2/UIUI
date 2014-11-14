package edu.illinois.engr.web.cs465uiui.net;

/**The result of a request to the server.*/
public class ServerResult<T>
{
	/**Whether the request succeeded or not.
	 * If true, error is null; otherwise, result is null.*/
	public final boolean success;
	
	/**The thing we retrieved from the server. Null if the request failed.*/
	public final T payload;
	/**The error that caused the request to fail.*/
	public final Exception error;
	
	
	private ServerResult(boolean success, T result, Exception error)
	{
		this.success = success;
		this.payload = result;
		this.error = error;
	}
	
	/**Create a result for a successful server request.*/
	public ServerResult(T result){this(true, result, null);}
	
	/**Create a result for an unsuccessful server request.*/
	public ServerResult(Exception error){this(false, null, error);}
}
