package edu.wsu.weather.agweathernet.fragments;

import static edu.wsu.weather.agweathernet.CommonUtility.HOME_FRAG_TAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
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
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.ImageLoader;

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
		return rootView;
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
		Criteria criteria = new Criteria();

		String provider = locationManager.getBestProvider(criteria, false);
		Log.i(CommonUtility.HOME_FRAG_TAG, provider);

		if (!locationManager.isProviderEnabled(provider)) {
			showSettingsAlert(provider);
		}
		provider = locationManager.getBestProvider(criteria, true);
		Log.i(HOME_FRAG_TAG, provider);
		locationManager.requestLocationUpdates(provider, 1000L, 500.0f, this);
		Location loc = locationManager.getLastKnownLocation(provider);
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
}
