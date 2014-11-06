package edu.illinois.engr.web.cs465uiui.utils;

import java.io.InputStream;
import java.util.List;

/**
 * JsonParser provides an interface for parsing an array of JSON objects from an input stream and
 * generating a list of java objects from this.
 *
 * @param <T> the generic type
 */
public interface JsonParser<T> {
	
	/**
	 * Parses the given InputStream of an array of JSON objects and returns a list of type T (Java object representation of the JSON objects).
	 *
	 * @param in the in
	 * @return the list
	 */
	public List<T> parse(InputStream in);
}
