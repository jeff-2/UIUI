package edu.illinois.engr.web.cs465uiui.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NetworkRequest<T> {
	
	private URL url;
	
	public NetworkRequest(URL url) {
		this.url = url;
	}
	
	public List<T> sendAndReceive(JsonParser<T> parser) {
		List<T> results = new ArrayList<T>();
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
