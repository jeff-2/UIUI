package edu.illinois.engr.web.cs465uiui.utils;

import java.io.InputStream;
import java.util.List;

public interface JsonParser<T> {
	public List<T> parse(InputStream in);
}
