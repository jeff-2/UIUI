package edu.illinois.engr.web.cs465uiui.net;

import java.util.*;

import edu.illinois.engr.web.cs465uiui.Tag;

/**Fetches simple sets of data from the server.*/
public class Fetch
{
	/**Fetches all tags from the server.
	 * Caches results in memory.*/
	public static List<Tag> allTags()
	{
		//TODO actually load from server
		List<Tag> out = new ArrayList<>();
		String[] strings = {"Seafood", "Burgers", "Fast", "Pizza"};
		for(String s : strings)
			out.add(new Tag(s));
		return out;
	}
}
