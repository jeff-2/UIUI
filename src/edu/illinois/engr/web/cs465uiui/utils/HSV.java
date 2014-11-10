package edu.illinois.engr.web.cs465uiui.utils;

import android.graphics.Color;

/**Lets you conveniently create HSV colors.*/
public class HSV
{
	/**Returns the color that corresponds to the HSV input.
	 * @param hue in [0,360]
	 * @param saturation in [0,1]
	 * @param value in [0,1]*/
	public static int hsv(float hue, float saturation, float value)
	{
		return Color.HSVToColor(new float[]{hue, saturation, value});
	}
}
