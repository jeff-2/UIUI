package edu.illinois.engr.web.cs465uiui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;


public class JsonParserImpl<T> implements JsonParser<T> {
	
	private Class<T> elementClass;
	
	public JsonParserImpl(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	
	@Override
	public List<T> parse(InputStream in) {
		
		ObjectMapper objectMapper = new ObjectMapper(); 
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		
		List<T> queryResults = new ArrayList<>();
		try {
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
