package edu.wsu.weather.agweathernet.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.models.CurrentWeather;

public class CurrentWeatherDataAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<CurrentWeather> currentWeatherList;
	FragmentManager fm;

	public CurrentWeatherDataAdapter(Context ctx,
			ArrayList<CurrentWeather> currentWeatherList, FragmentManager fm) {
		this.ctx = ctx;
		this.currentWeatherList = currentWeatherList;
		this.fm = fm;
	}

	public void update(ArrayList<CurrentWeather> currentWeatherList) {
		this.currentWeatherList = currentWeatherList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (currentWeatherList == null)
			return 0;
		return currentWeatherList.size();
	}

	@Override
	public Object getItem(int index) {
		if (currentWeatherList == null)
			return null;
		return currentWeatherList.get(index);
	}

	@Override
	public long getItemId(int i) {
		return (long) i;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;

		CurrentWeather cweatherModel = currentWeatherList.get(position);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (view == null) {
			view = inflater.inflate(R.layout.current_conditions_row, parent,
					false);
		}

		TextView stationName = (TextView) view.findViewById(R.id.stationName);
		TextView airTemp = (TextView) view.findViewById(R.id.airTemp);
		TextView soilTemp = (TextView) view.findViewById(R.id.soilTemp);
		TextView relHumidity = (TextView) view.findViewById(R.id.relHumidity);
		TextView dewPoint = (TextView) view.findViewById(R.id.dewPoint);
		TextView leafWetness = (TextView) view.findViewById(R.id.leafWetness);
		TextView windSpeed = (TextView) view.findViewById(R.id.windSpeed);
		TextView totalPrec = (TextView) view.findViewById(R.id.totalPrec);
		TextView solarRad = (TextView) view.findViewById(R.id.solarRad);

		stationName.setText(cweatherModel.getStationName());
		airTemp.setText(cweatherModel.getAirTemp());
		soilTemp.setText(cweatherModel.getSoilTemp());
		relHumidity.setText(cweatherModel.getRelHumidity());
		dewPoint.setText(cweatherModel.getDewPoint());
		leafWetness.setText(cweatherModel.getLeafWetness());
		windSpeed.setText(cweatherModel.getWindSpeed());
		totalPrec.setText(cweatherModel.getPrecip());
		solarRad.setText(cweatherModel.getSolarRad());

		return view;
	}

	@Override
	public boolean isEmpty() {
		return currentWeatherList.isEmpty();
	}
}
