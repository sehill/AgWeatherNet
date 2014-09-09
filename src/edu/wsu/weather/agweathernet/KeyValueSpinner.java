package edu.wsu.weather.agweathernet;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.helpers.SpinnerModel;

public class KeyValueSpinner implements SpinnerAdapter {
	Context context;
	List<SpinnerModel<String>> list;

	public KeyValueSpinner(Context context, List<SpinnerModel<String>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	public String getIdFromIndex(int index) {
		return list.get(index).getId();
	}

	public int getIndexById(String id) {
		for (int i = 0; i < list.size(); i++) {
			SpinnerModel<String> temp = list.get(i);
			if (temp.getId().equals(id))
				return i;
		}
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TextView textview = (TextView) inflater.inflate(
				R.layout.spinner_default_item, null);

		textview.setText(list.get(position).getValue());

		return textview;
	}

	@Override
	public int getViewTypeCount() {
		return android.R.layout.simple_spinner_item;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TextView textview = (TextView) inflater.inflate(
				R.layout.spinner_default_item, null);

		textview.setText(list.get(position).getValue());

		return textview;
	}
}
