package edu.wsu.weather.agweathernet.fragments;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.ImageLoader;

public class SingleStationFragment extends Fragment {
	private String stationId;
	private Context context;
	private Activity activity;

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
		super.onCreate(savedInstanceState);
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

	private void getStationById() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				String url = CommonUtility.HOST_URL + "test/stations.php?id="
						+ stationId;
				HttpClient webClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				try {
					HttpResponse response = webClient.execute(get);
					String respString = EntityUtils.toString(response
							.getEntity());
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
			}
		}.execute();

	}

	private void setFieldsContent(JSONObject model) throws JSONException {
		stationName.setText(model.getString("station_name"));
		county.setText(model.getString("county"));
		installDate.setText(model.getString("installation_date"));
		airTemp.setText(model.getString("air_temp"));
		inTime.setText(model.getString("intime"));
		relHumidity.setText(model.getString("rel_humidity"));
		dewPoint.setText(model.getString("dewpoint"));
		leafWetness.setText(model.getString("leaf_wetness"));
		solarRad.setText(model.getString("solar_rad"));
		wind.setText(model.getString("wind_speed"));
		soilTemp.setText(model.getString("soil_temp_8_in"));
		precip.setText(model.getString("precip"));
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
