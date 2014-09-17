package edu.wsu.weather.agweathernet.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.ImageLoader;
import edu.wsu.weather.agweathernet.helpers.StationModel;
import edu.wsu.weather.agweathernet.helpers.StationsAdapter;

public class HomeFragment extends BaseFragment implements LocationListener {

	Location location;
	LocationManager locationManager;
	String alertDialogResult = "";

	TextView txtviewName;
	TextView txtviewTemp;
	TextView txtviewHumidity;
	TextView txtviewWind;
	TextView txtviewDewpoint;
	TextView txtviewRain;
	ImageView stationImgView;

	ListView stationsListView;
	StationsAdapter adapter;
	ArrayList<StationModel> stationsModelList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();

		context = activity.getApplicationContext();

		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

		setLocation();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("HOME", "resumed");

		setLocation();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.home_layout, container, false);
		initializeFields(rootView);

		loadServerData();

		setEventListeners();

		return rootView;
	}

	private void setEventListeners() {
		stationsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				StationModel selectedModel = (StationModel) stationsModelList
						.get(position);
				Log.i(CommonUtility.STATIONS_TAG, "model = " + selectedModel);

				SingleStationFragment newFrag = new SingleStationFragment();

				Bundle args = new Bundle();

				args.putString("id", selectedModel.getUnitId());

				newFrag.setArguments(args);

				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();

				transaction.replace(R.id.container, newFrag);
				transaction.addToBackStack(null);

				transaction.commit();
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached("Home");
	}

	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		// TODO get data from server with gps.
		getNearestStation();
		Log.i(CommonUtility.HOME_FRAG_TAG, "location-" + loc.getLatitude()
				+ " " + loc.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (activity == null)
				activity = getActivity();
			Log.i("GENERAL", "orientation set");
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	private void getNearestStation() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				// TODO take shared preferences object out for common use.
				SharedPreferences prefs = activity.getSharedPreferences(
						"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
				String username = prefs.getString("username", "");
				String url = CommonUtility.HOST_URL + "test/stations.php?lat="
						+ location.getLatitude() + "&long="
						+ location.getLongitude() + "&uname=" + username;

				try {
					return HttpRequestWrapper.getString(url);
				} catch (Exception ex) {
					Log.e(CommonUtility.HOME_FRAG_TAG, ex.getMessage());
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					Log.i(CommonUtility.HOME_FRAG_TAG, result);
					JSONArray jsonArr = new JSONArray(result);
					JSONObject jsonObj = jsonArr.getJSONObject(0);
					Log.i(CommonUtility.HOME_FRAG_TAG, jsonObj.toString());
					setFieldsContent(jsonObj);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.e(CommonUtility.HOME_FRAG_TAG, e.getMessage());
				}
			}
		}.execute();
	}

	private void initializeFields(View rootView) {
		txtviewName = (TextView) rootView.findViewById(R.id.txtviewName);
		txtviewTemp = (TextView) rootView.findViewById(R.id.txtviewTemp);
		stationImgView = (ImageView) rootView.findViewById(R.id.imgviewStation);
		txtviewHumidity = (TextView) rootView
				.findViewById(R.id.txtviewHumidity);
		txtviewWind = (TextView) rootView.findViewById(R.id.txtviewWind);
		txtviewDewpoint = (TextView) rootView
				.findViewById(R.id.txtviewDewpoint);
		txtviewRain = (TextView) rootView.findViewById(R.id.txtviewRain);

		stationsListView = (ListView) rootView.findViewById(R.id.stations_list);
	}

	private void setFieldsContent(JSONObject jsonObj) throws JSONException {
		txtviewName.setText(jsonObj.getString("station_name"));

		String tempVal = null;

		if ((tempVal = jsonObj.getString("air_temp")) != null
				&& tempVal != "null") {
			txtviewTemp.setText(CommonUtility.DEF_DECIMAL_FORMAT.format(Double
					.parseDouble(tempVal)));
		}
		if ((tempVal = jsonObj.getString("rel_humidity")) != null
				&& tempVal != "null") {
			txtviewHumidity.setText(CommonUtility.DEF_DECIMAL_FORMAT
					.format(Double.parseDouble(tempVal)));
		}
		if ((tempVal = jsonObj.getString("dewpoint")) != null
				&& tempVal != "null") {
			txtviewDewpoint.setText(CommonUtility.DEF_DECIMAL_FORMAT
					.format(Double.parseDouble(tempVal)));
		}
		if ((tempVal = jsonObj.getString("precip")) != null
				&& tempVal != "null") {
			txtviewRain.setText(CommonUtility.DEF_DECIMAL_FORMAT.format(Double
					.parseDouble(tempVal)));
		}
		if ((tempVal = jsonObj.getString("wind_speed")) != null
				&& tempVal != "null") {
			txtviewWind.setText(CommonUtility.DEF_DECIMAL_FORMAT.format(Double
					.parseDouble(tempVal)));
		}

		ImageLoader imageLoader = new ImageLoader(context);

		imageLoader.DisplayImage(
				CommonUtility.HOST_URL + "images/stationpics/thumbs300/tn_"
						+ jsonObj.getString("unit_id") + "N.JPG",
				stationImgView);
	}

	private void setLocation() {
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, this);
		Log.i(CommonUtility.HOME_FRAG_TAG, "retrieving location");
		Location loc = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc != null) {
			onLocationChanged(loc);
		}
	}

	public void showSettingsAlert(String provider) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

		alertDialog.setTitle(provider + " SETTINGS");

		alertDialog.setMessage(provider
				+ " is not enabled! Want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						activity.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	private void loadServerData() {
		Log.i(CommonUtility.STATIONS_TAG, "loadServerData()");
		new StationsLoader().execute();
	}

	private class StationsLoader extends
			AsyncTask<Void, Integer, ArrayList<StationModel>> {
		protected ProgressDialog progressDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Stations");
			progressDialog.setMessage(CommonUtility.LOADING_PEASE_WAIT);
			progressDialog.setCancelable(true);
			progressDialog.show();
		};

		@Override
		protected ArrayList<StationModel> doInBackground(Void... arg0) {
			stationsModelList = new ArrayList<StationModel>();

			String resultString = "Doing in background";
			try {
				Log.i(CommonUtility.STATIONS_TAG, "getting stations background");
				resultString = HttpRequestWrapper
						.getString(CommonUtility.HOST_URL
								+ "test/stations.php?n=30&favsts=1&uname="
								+ getUserName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				StationModel model;
				JSONArray generalJobj = new JSONArray(resultString);

				for (int i = 0; i < generalJobj.length(); i++) {
					model = new StationModel();

					JSONObject jobj = generalJobj.getJSONObject(i);
					model.setUnitId(jobj.getString("unit_id"));
					model.setCity(jobj.getString("city"));
					model.setState(jobj.getString("state"));
					model.setName(jobj.getString("station_name"));
					model.setCounty(jobj.getString("county"));
					model.setInstallationDate(jobj
							.getString("installation_date"));
					model.setFavourite(jobj.getString("isFavourite") != null
							&& jobj.getString("isFavourite").equals("true"));
					stationsModelList.add(model);
				}

				Log.i(CommonUtility.STATIONS_TAG,
						"stations list retrieved, size() = "
								+ stationsModelList.size());
				progressDialog.dismiss();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return stationsModelList;
		}

		@Override
		protected void onPostExecute(ArrayList<StationModel> result) {
			adapter = new StationsAdapter(context, stationsModelList);
			stationsListView.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
}
