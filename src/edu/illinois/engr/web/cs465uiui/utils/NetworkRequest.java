package edu.illinois.engr.web.cs465uiui.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * NetworkRequest provides a reusable method of sending a http GET request to our server, getting the
 * resulting response and parsing that array of JSON objects into a List of Java Objects.
 *
 * @param <T> the generic type
 */
public class NetworkRequest<T> {
	
	/** The url to request from. */
	private URL url;
	
	/** The parser to parse with. */
	private JsonParser<T> parser;
	
	/**
	 * Instantiates a new network request.
	 *
	 * @param url the url to request from
	 * @param parser the parser to parse with
	 */
	public NetworkRequest(URL url, JsonParser<T> parser) {
		this.url = url;
		this.parser = parser;
	}
	
	/**
	 * Sends a http GET request, receives a response, and parses the resulting stream containing the
	 * content body in JSON format, into a List of Java Objects.
	 *
	 * @return the list containing Java Objects with the results of the request
	 */
	public List<T> sendAndReceive() {
		List<T> results = new ArrayList<>();
		try {
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			try {
				InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				results = parser.parse(inputStream);
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return results;
	}
}
