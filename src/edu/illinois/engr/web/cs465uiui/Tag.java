package edu.illinois.engr.web.cs465uiui;

import java.util.Comparator;

/**A category of restaurants."*/
public class Tag
{
	/**something like "seafood"*/
	public final String name;
	
	public Tag(String name){this.name = name;}
	
	
	
	/**Can be used to sort tags alphabetically.*/
	public static final Comparator<Tag> compare = new Comparator<Tag>()
	{
		@Override public int compare(Tag a, Tag b)
		{
			return a.name.compareTo(b.name);
		}
	};
}
