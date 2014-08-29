package edu.wsu.weather.agweathernet.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;

public class HomeFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.home_layout, container, false);

		// LocationManager locationManager = (LocationManager) getActivity()
		// .getSystemService(Context.LOCATION_SERVICE);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached("Home");
	}

}
