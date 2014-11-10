package edu.illinois.engr.web.cs465uiui.utils;

import java.io.InputStream;
import java.util.Scanner;

/**Helper code related to input/output.*/
public class IOUtil
{
	/**Reads an entire input stream.*/
	public static String readStream(InputStream in)
	{
		//stackoverflow.com/a/5445161    \A apparently means read to end of file
		@SuppressWarnings("resource")//it complains I never close the first scanner; who cares?
		Scanner scan = new Scanner(in).useDelimiter("\\A");
	    String out = scan.hasNext() ? scan.next() : "";
	    
	    scan.close();
	    return out;
	}
}
