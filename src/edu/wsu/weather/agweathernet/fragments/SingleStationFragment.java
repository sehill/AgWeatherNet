package edu.wsu.weather.agweathernet.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.ImageLoader;

public class SingleStationFragment extends BaseFragment {
	private String stationId;
	private String lat;
	private String lng;

	private TextView stationName;
	private TextView county;
	private TextView installDate;
	private TextView airTemp;
	private TextView inTime;
	private TextView relHumidity;
	private TextView dewPoint;
	private TextView leafWetness;
	private TextView solarRad;
	private TextView wind;
	private TextView soilTemp;
	private TextView precip;
	private ImageView stationImg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(CommonUtility.SINGLE_STATION_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		context = getActivity().getApplicationContext();
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_single_station,
				container, false);

		initializeProperties(rootView);

		stationId = getArguments().getString("id");

		((MainActivity) activity).onSectionAttached("Station");

		if (stationId == null || stationId.isEmpty()) {
			// TODO stationId was null do something and log.
		} else {
			getStationById();

			ImageLoader imageLoader = new ImageLoader(context);
			imageLoader.DisplayImage(CommonUtility.HOST_URL
					+ "images/stationpics/thumbs300/tn_" + stationId + "N.JPG",
					stationImg);
		}

		return rootView;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.station_single, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.openOnMap:
			MapFragment newFrag = new MapFragment();

			Bundle args = new Bundle();

			args.putString("lat", lat);
			args.putString("lng", lng);

			newFrag.setArguments(args);

			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();

			transaction.replace(R.id.container, newFrag);
			transaction.addToBackStack(null);

			transaction.commit();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void getStationById() {

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

				String url = CommonUtility.HOST_URL + "test/stations.php?id="
						+ stationId + "&uname=" + getUserName()
						+ "&auth_token=" + getPreferenceValue("auth_token", "");
				try {
					String respString = HttpRequestWrapper.getString(
							((AgWeatherNetApp) activity.getApplication())
									.getHttpClient(),
							((AgWeatherNetApp) activity.getApplication())
									.getHttpContext(), url);
					return respString;
				} catch (Exception ex) {
					Log.e(CommonUtility.SINGLE_STATION_TAG, ex.getMessage());
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					Log.i(CommonUtility.SINGLE_STATION_TAG, result);
					JSONArray jsonArr = new JSONArray(result);
					JSONObject jsonObj = jsonArr.getJSONObject(0);
					Log.i(CommonUtility.SINGLE_STATION_TAG, jsonObj.toString());
					setFieldsContent(jsonObj);
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e(CommonUtility.SINGLE_STATION_TAG, e.getMessage());
				}
				progressDialog.dismiss();
			}
		}.execute();
	}

	@SuppressLint("SimpleDateFormat")
	private void setFieldsContent(JSONObject model) throws JSONException {
		try {
			stationName.setText(model.getString("station_name"));

			county.setText(model.getString("county"));

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			Date dateStr = formatter
					.parse(model.getString("installation_date"));

			installDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(
					dateStr).toString());

			airTemp.setText(CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
					.parseDouble(model.getString("air_temp"))));

			inTime.setText(model.getString("intime"));

			relHumidity
					.setText(CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
							.parseDouble(model.getString("rel_humidity"))));

			dewPoint.setText(CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
					.parseDouble(model.getString("dewpoint"))));

			leafWetness.setText(CommonUtility.DEF_DECIMAL_FORMAT.format(Double
					.parseDouble(model.getString("leaf_wetness"))));

			solarRad.setText(CommonUtility.DECIMAL_FORMAT_NONE.format(Double
					.parseDouble(model.getString("solar_rad"))));

			wind.setText(CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
					.parseDouble(model.getString("wind_speed"))));

			soilTemp.setText(CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
					.parseDouble(model.getString("soil_temp_8_in"))));

			precip.setText(CommonUtility.DEF_DECIMAL_FORMAT.format(Double
					.parseDouble(model.getString("precip"))));

			lat = model.getString("station_latdeg");

			lng = model.getString("station_lngdeg");

		} catch (ParseException e) {
			Log.e(CommonUtility.SINGLE_STATION_TAG, e.getMessage());
		} catch (Exception e) {
			Log.e(CommonUtility.SINGLE_STATION_TAG, e.getMessage());
		}
	}

	private void initializeProperties(View rootView) {
		stationName = (TextView) rootView.findViewById(R.id.stationName);
		county = (TextView) rootView.findViewById(R.id.county);
		installDate = (TextView) rootView.findViewById(R.id.dateInstalled);
		airTemp = (TextView) rootView.findViewById(R.id.temperature);
		inTime = (TextView) rootView.findViewById(R.id.inTime);
		relHumidity = (TextView) rootView.findViewById(R.id.relHumidity);
		dewPoint = (TextView) rootView.findViewById(R.id.dewPoint);
		leafWetness = (TextView) rootView.findViewById(R.id.leafWetnes);
		solarRad = (TextView) rootView.findViewById(R.id.solarRad);
		wind = (TextView) rootView.findViewById(R.id.wind);
		soilTemp = (TextView) rootView.findViewById(R.id.soilTemp);
		precip = (TextView) rootView.findViewById(R.id.todayRain);
		stationImg = (ImageView) rootView.findViewById(R.id.fstStationImg);
	}
}
