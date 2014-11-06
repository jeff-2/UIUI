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
	private JsonParser<T> parser;
	
	public NetworkRequest(URL url, JsonParser<T> parser) {
		this.url = url;
		this.parser = parser;
	}
	
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
