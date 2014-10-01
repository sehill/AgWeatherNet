package edu.wsu.weather.agweathernet;

import java.util.ArrayList;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.fragments.SingleAlertFragment;

public class AlertsAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<AlertsModel> alertsList;
	FragmentManager fm;

	public AlertsAdapter(Context ctx, ArrayList<AlertsModel> alertsList,
			FragmentManager fm) {
		this.ctx = ctx;
		this.alertsList = alertsList;
		this.fm = fm;
	}

	public void update(ArrayList<AlertsModel> alertsList) {
		this.alertsList = alertsList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (alertsList == null)
			return 0;
		return alertsList.size();
	}

	@Override
	public Object getItem(int index) {
		if (alertsList == null)
			return null;
		return alertsList.get(index);
	}

	@Override
	public long getItemId(int i) {
		return (long) i;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;

		AlertsModel alertModel = alertsList.get(position);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (view == null) {
			view = inflater.inflate(R.layout.alertslist_row, parent, false);
		}

		TextView name = (TextView) view.findViewById(R.id.alertName);
		TextView stationName = (TextView) view.findViewById(R.id.stationName);
		TextView method = (TextView) view.findViewById(R.id.method);

		name.setText(alertModel.reportName);
		stationName.setText(alertModel.station);
		method.setText(alertModel.alertEvent);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertsModel selectedModel = (AlertsModel) alertsList
						.get(position);
				Log.i(CommonUtility.ALERTS_ACT_STR, "model = " + selectedModel);

				SingleAlertFragment newFrag = new SingleAlertFragment();

				Bundle args = new Bundle();

				args.putString("id", selectedModel.Id);

				newFrag.setArguments(args);

				FragmentTransaction transaction = fm.beginTransaction();

				transaction.replace(R.id.container, newFrag);
				transaction.addToBackStack(null);

				transaction.commit();
			}
		});
		return view;
	}

	@Override
	public boolean isEmpty() {
		return alertsList.isEmpty();
	}
}
