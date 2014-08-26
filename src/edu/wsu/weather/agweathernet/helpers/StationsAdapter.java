package edu.wsu.weather.agweathernet.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.R;

public class StationsAdapter extends BaseAdapter {
	Context ctx;
	ArrayList<StationModel> stationList;
	public ImageLoader imageLoader;

	public StationsAdapter(Context ctx, ArrayList<StationModel> stationList) {
		this.ctx = ctx;
		this.stationList = stationList;
		imageLoader = new ImageLoader(ctx);
	}

	public void update(ArrayList<StationModel> stationList) {
		this.stationList = stationList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (stationList == null)
			return 0;
		return stationList.size();
	}

	@Override
	public Object getItem(int index) {
		if (stationList == null)
			return null;
		return stationList.get(index);
	}

	@Override
	public long getItemId(int i) {
		return (long) i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		StationModel stationModel = stationList.get(position);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			view = inflater.inflate(R.layout.station_list_row, parent, false);
		}

		ImageView stationImg = (ImageView) view.findViewById(R.id.thumbnail);

		String uri = CommonUtility.HOST_URL + "images/stationpics/thumbs/tn_"
				+ stationModel.getUnitId() + "_N.JPG";
		imageLoader.DisplayImage(uri, stationImg);

		TextView name = (TextView) view.findViewById(R.id.name);
		TextView county = (TextView) view.findViewById(R.id.countye);
		TextView installationDate = (TextView) view
				.findViewById(R.id.installationDate);

		name.setText(stationModel.getName());
		county.setText(stationModel.getCounty());
		installationDate.setText(stationModel.getInstallationDate());

		return view;
	}

	@Override
	public boolean isEmpty() {
		return stationList.isEmpty();
	}
}
