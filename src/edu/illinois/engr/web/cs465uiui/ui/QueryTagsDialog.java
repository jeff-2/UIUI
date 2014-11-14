package edu.illinois.engr.web.cs465uiui.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.net.ServerResult;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**Lets the user select tags for the current query.
 * Saves and loads queries from QueryData.
 * XXX won't handle being destroyed and restarted well*/
public class QueryTagsDialog extends DialogFragment
{
	/**Null when not loaded.*/
	private Query query = null;

	/**Null when not loaded.*/
	private List<Tag> tags = null;
	
	private RadioButton any, all;
	private View loading;
	private ListView list;
	
	private final Adapter adapter = new Adapter();
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
	{
		View v = inflater.inflate(R.layout.dialog_query_tags, null);
		any = (RadioButton)v.findViewById(R.id.diag_qtags_any);
		all = (RadioButton)v.findViewById(R.id.diag_qtags_all);
		loading = v.findViewById(R.id.diag_qtags_loading);
		list = (ListView)v.findViewById(R.id.diag_qtags_list);
		
		query = QueryData.load(getActivity());
		
		list.setAdapter(adapter);
		(query.allTags ? all : any).setChecked(true);
		
		return v;
	}
	
	
	@Override public void onStart()
	{
		super.onStart();
		new LoadTask().execute();
	}
	
	@Override public void onDismiss(DialogInterface d)
	{
		query.allTags = all.isChecked();
		QueryData.save(query, getActivity());
		
		if(getActivity() instanceof Listener)
			((Listener)getActivity()).onQueryTagsClosed();
		else
			Log.w("uiui.ui.QueryTagsDialog", "Could not notify parent that this closed because it's not a Listener");
		
		super.onDismiss(d);
	}
	
	
	
	
	
	/**Adapts the tags list to the listview.*/
	private class Adapter extends BaseAdapter
	{
		@Override public int getCount(){return tags == null ? 0 : tags.size();}
		@Override public Tag getItem(int position){return tags.get(position);}
		@Override public long getItemId(int position){return getItem(position).hashCode();}
		@Override public View getView(int position, View convert, ViewGroup parent)
		{
			View v = convert == null ? getActivity().getLayoutInflater().inflate(R.layout.sub_query_tag, null) : convert;
			CheckBox check = (CheckBox)v.findViewById(R.id.sub_qtag_check);;
			TextView name = (TextView)v.findViewById(R.id.sub_qtag_name);
			Tag tag = getItem(position);
			
			check.setOnCheckedChangeListener(null);
			check.setChecked(query.tags.contains(tag));
			check.setOnCheckedChangeListener(new CheckHandler(tag));
			name.setText(tag.name);
			
			return v;
		}
		
		
		/**Handles clicks on check boxes for a tag.*/
		private class CheckHandler implements CompoundButton.OnCheckedChangeListener
		{
			private final Tag tag;
			public CheckHandler(Tag tag){this.tag = tag;}
			
			@Override public void onCheckedChanged(CompoundButton button, boolean isChecked)
			{
				if(isChecked)
					query.tags.add(tag);
				else
					query.tags.remove(tag);
			}
		}
	}
	
	/**Loads tags in a background thread, then displays them.
	 * Also loads the query.*/
	private class LoadTask extends AsyncTask<Void, Void, ServerResult<List<Tag>>>
	{
		@Override protected void onPreExecute()
		{
			loading.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
		
		@Override protected ServerResult<List<Tag>> doInBackground(Void... params)
		{
			ServerResult<List<Tag>> result = UIFetch.allTags();
			if(result.success)
				Collections.sort(result.payload, Tag.compare);
			return result;
		}
		
		@Override protected void onPostExecute(ServerResult<List<Tag>> result)
		{
			if(result.success)
			{
				//unselect tags that no longer exist
				tags = result.payload;
				for(Iterator<Tag> it = query.tags.iterator(); it.hasNext(); /*nothing*/)
					if(!tags.contains(it.next()))
						it.remove();
				
				adapter.notifyDataSetChanged();
				loading.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}
			else
				UIFetch.explainError(result.error, getActivity());
		}
	}
	
	
	
	/**The parent activity can implement this to be notified when this dialog closes.*/
	public static interface Listener
	{
		/**Called when this dialog closes for any reason.*/
		public void onQueryTagsClosed();
	}
}
