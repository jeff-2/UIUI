package edu.illinois.engr.web.cs465uiui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * JsonParserImpl provides a concrete implementation of the JsonParser interface.
 *
 * @param <T> the generic type
 */
public class JsonParserImpl<T> implements JsonParser<T> {
	
	/** The element class. */
	private Class<T> elementClass;
	
	/**
	 * Instantiates a new json parser impl.
	 *
	 * @param elementClass the element class (type of objects being mapped from JSON to Java objects)
	 */
	public JsonParserImpl(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	
	/* (non-Javadoc)
	 * @see edu.illinois.engr.web.cs465uiui.utils.JsonParser#parse(java.io.InputStream)
	 */
	@Override
	public List<T> parse(InputStream in) {
		
		ObjectMapper objectMapper = new ObjectMapper(); 
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		
		List<T> queryResults = new ArrayList<>();
		try {
			// map array of JSON objects input stream to ArrayList of elementClass objects
			queryResults = objectMapper.readValue(in, typeFactory.constructCollectionType(ArrayList.class, elementClass));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return queryResults;
	}
}
