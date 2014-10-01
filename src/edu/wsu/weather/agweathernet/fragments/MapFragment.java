package edu.wsu.weather.agweathernet.fragments;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;

public class MapFragment extends BaseFragment implements OnMarkerClickListener,
		OnInfoWindowClickListener {

	View rootView;
	Context context;
	Activity activity;
	GoogleMap map;
	String stationLocLat;
	String stationLocLng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		context = activity.getApplicationContext();
		setHasOptionsMenu(true);
		((MainActivity) activity).onSectionAttached("Stations");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(CommonUtility.MAP_FRAG_TAG, "onCreateView");

		if (rootView != null) {

			Log.i(CommonUtility.MAP_FRAG_TAG, "rootView != null");

			ViewGroup parent = (ViewGroup) rootView.getParent();

			if (parent != null) {

				Log.i(CommonUtility.MAP_FRAG_TAG, "parent != null");

				parent.removeView(rootView);
			}
		}
		try {
			Log.i(CommonUtility.MAP_FRAG_TAG, "Inflated");

			rootView = inflater.inflate(R.layout.location_fragment, container,
					false);

		} catch (InflateException e) {
			e.printStackTrace();
			Log.i(CommonUtility.MAP_FRAG_TAG, e.getMessage());
		}

		map = ((com.google.android.gms.maps.SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.location_map)).getMap();
		map.setMyLocationEnabled(true);
		map.setOnInfoWindowClickListener(this);

		Bundle bundleArgs = getArguments();

		if (bundleArgs != null) {
			Log.i(CommonUtility.MAP_FRAG_TAG, bundleArgs.toString());
			stationLocLat = bundleArgs.getString("lat");
			stationLocLng = bundleArgs.getString("lng");
		}

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager()
				.findFragmentById(R.id.location_map));
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	@Override
	public void onResume() {
		Log.i(CommonUtility.MAP_FRAG_TAG, "call loadMarkers(...)");
		loadMarkers();
		Log.i(CommonUtility.MAP_FRAG_TAG, "end call loadMarkers(...)");
		super.onResume();
	}

	/*
	 * @Override public void onStop() { super.onStop(); }
	 */

	@Override
	public void onInfoWindowClick(Marker marker) {
		Log.i(CommonUtility.MAP_FRAG_TAG, "onInfoWindowClick");

		SingleStationFragment newFrag = new SingleStationFragment();

		Bundle args = new Bundle();

		args.putString("id", marker.getSnippet());

		newFrag.setArguments(args);

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		transaction.replace(R.id.container, newFrag);
		transaction.addToBackStack(null);

		transaction.commit();
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		return false;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (activity == null)
				activity = getActivity();
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			break;
		}
		return false;
	}

	private void loadMarkers() {

		new AsyncTask<Void, Void, String>() {
			protected ProgressDialog progressDialog;

			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(activity);
				progressDialog.setTitle("Stations");
				progressDialog.setMessage(CommonUtility.LOADING_PEASE_WAIT);
				progressDialog.setCancelable(true);
				progressDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						HttpRequestWrapper.abortRequest();
						cancel(true);
					}
				});
				progressDialog.show();
			};

			@Override
			protected String doInBackground(Void... params) {
				try {
					String res = HttpRequestWrapper.getString(
							((AgWeatherNetApp) activity.getApplication())
									.getHttpClient(),
							((AgWeatherNetApp) activity.getApplication())
									.getHttpContext(),
							CommonUtility.HOST_URL
									+ "test/stations.php?n=3000&uname="
									+ getPreferenceValue("username", "")
									+ "&auth_token="
									+ getPreferenceValue("auth_token", ""));

					Log.i(CommonUtility.MAP_FRAG_TAG, "got response = " + res);
					return res;
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
				try {
					JSONArray generalJobj = new JSONArray(result);
					MarkerOptions options;
					for (int i = 0; i < generalJobj.length(); i++) {
						JSONObject jobj;

						jobj = generalJobj.getJSONObject(i);

						options = new MarkerOptions();

						options.position(new LatLng(Double.parseDouble(jobj
								.getString("station_latdeg")), Double
								.parseDouble(jobj.getString("station_lngdeg"))
								* CommonUtility.MULTIPLY_LAT_LNG_BY));

						options.title(jobj.getString("station_name") + " - "
								+ jobj.getString("county"));

						options.snippet(jobj.getString("unit_id"));

						map.addMarker(options);
						Log.i(CommonUtility.MAP_FRAG_TAG, "marker added");
						boundsBuilder.include(options.getPosition());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				CameraUpdate cu;
				if (stationLocLat != null && stationLocLng != null
						&& !stationLocLat.isEmpty() && !stationLocLng.isEmpty()) {
					Log.i(CommonUtility.MAP_FRAG_TAG, "Zooming to the station");
					LatLng loc = new LatLng(Double.parseDouble(stationLocLat),
							Double.parseDouble(stationLocLng)
									* CommonUtility.MULTIPLY_LAT_LNG_BY);
					cu = CameraUpdateFactory.newLatLngZoom(loc, 12.0f);

				} else {
					LatLngBounds bounds = boundsBuilder.build();
					int padding = 0;
					cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				}
				super.onPreExecute();
				progressDialog.dismiss();

				map.animateCamera(cu);
			}
		}.execute();
	}
}