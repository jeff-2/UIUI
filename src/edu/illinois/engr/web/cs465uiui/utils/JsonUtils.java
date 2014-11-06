package edu.illinois.engr.web.cs465uiui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import edu.illinois.engr.web.cs465uiui.search.SearchItem;


public class JsonUtils {
	
	public static ArrayList<SearchItem> parseJSONResponse(InputStream in) {
		
		ObjectMapper objectMapper = new ObjectMapper(); 
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		
		ArrayList<SearchItem> queryResults = new ArrayList<SearchItem>();
		try {
			queryResults = objectMapper.readValue(in, typeFactory.constructCollectionType(ArrayList.class, SearchItem.class));
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
