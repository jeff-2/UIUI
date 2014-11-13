package edu.illinois.engr.web.cs465uiui;

import java.util.List;

/**Represents a restaurant taken from the server.*/
public class Restaurant
{
	/**A database id.*/
	public final long id;
	public final String name;
	/**ex. "N Neil St., Champaign, IL"*/
	public final String location;
	/**The coordinates of this restaurant (latitude/longitude).*/
	public final float lat, lon;
	/**Tags this restaurant belongs to.*/
	public final List<Tag> tags;
	
	public Restaurant(long id, String name, String location, float lat, float lon, List<Tag> tags)
	{
		this.id = id;
		this.name = name;
		this.location = location;
		this.lat = lat;
		this.lon = lon;
		this.tags = tags;
	}
}
