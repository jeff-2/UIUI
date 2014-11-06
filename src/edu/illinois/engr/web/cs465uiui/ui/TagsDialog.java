package edu.illinois.engr.web.cs465uiui.ui;

import java.util.List;

import edu.illinois.engr.web.cs465uiui.Query;
import edu.illinois.engr.web.cs465uiui.R;
import edu.illinois.engr.web.cs465uiui.Tag;
import edu.illinois.engr.web.cs465uiui.net.Fetch;
import edu.illinois.engr.web.cs465uiui.store.QueryData;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class TagsDialog extends DialogFragment
{
	/**Null when not loaded.*/
	private Query query = null;

	/**Null when not loaded.*/
	private List<Tag> tags = null;
	
	private RadioGroup group;
	private RadioButton any, all;
	private View loading;
	private ListView list;
	
	private final Adapter adapter = new Adapter();
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved)
	{
		View v = inflater.inflate(R.layout.dialog_query_tags, null);
		group = (RadioGroup)v.findViewById(R.id.diag_qtags_group);
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
	
	@Override public void onStop()
	{
		query.allTags = all.isChecked();
		QueryData.save(query, getActivity());
		super.onStop();
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
			
			check.setOnCheckedChangeListener(NoneListener.INSTANCE);
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
	private class LoadTask extends AsyncTask<Void, Void, List<Tag>>
	{
		@Override protected void onPreExecute()
		{
			loading.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
		
		@Override protected List<Tag> doInBackground(Void... params)
		{
			return Fetch.allTags();
		}
		
		@Override protected void onPostExecute(List<Tag> result)
		{
			tags = result;
			adapter.notifyDataSetChanged();
			loading.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
		}
	}
}
