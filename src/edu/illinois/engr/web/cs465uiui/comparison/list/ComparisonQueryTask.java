package edu.illinois.engr.web.cs465uiui.comparison.list;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.utils.JsonParserImpl;
import edu.illinois.engr.web.cs465uiui.utils.NetworkRequest;

import android.os.AsyncTask;

public class ComparisonQueryTask extends AsyncTask<String, Void, List<ComparisonItem>> {

	@Override
	protected List<ComparisonItem> doInBackground(String... params) {
		URL url;
		try {
			url = new URL(params[0]);
		} catch (MalformedURLException e) {
			return new ArrayList<ComparisonItem>();
		}
		NetworkRequest<ComparisonItem> req = new NetworkRequest<>(url, new JsonParserImpl<ComparisonItem>(ComparisonItem.class));
		List<ComparisonItem> items = req.sendAndReceive();
		return items;
	}
}
