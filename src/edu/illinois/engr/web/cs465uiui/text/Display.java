package edu.illinois.engr.web.cs465uiui.text;

import java.util.List;

import edu.illinois.engr.web.cs465uiui.Tag;

/**Creates user-readable text output.*/
public class Display
{
	/**Returns a list of tags as a string.*/
	public static String tagList(List<Tag> list)
	{
		String out = "";
		boolean firstTime = true;
		for(Tag tag : list)
		{
			if(!firstTime)
				out += ", ";
			firstTime = false;
			out += tag.name;
		}
		return out;
	}
}
