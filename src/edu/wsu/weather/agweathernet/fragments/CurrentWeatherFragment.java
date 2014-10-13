package edu.wsu.weather.agweathernet.fragments;

import static edu.wsu.weather.agweathernet.CommonUtility.CurrentWeatherFragment;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.CurrentWeatherDataAdapter;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.models.CurrentWeather;

public class CurrentWeatherFragment extends BaseFragment {
	ListView currentConditionsListVIew;
	CurrentWeatherDataAdapter adapter;
	ArrayList<CurrentWeather> cweatherModelList;

	public CurrentWeatherFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		context = getActivity().getApplicationContext();
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.current_conditions_listview,
				container, false);

		currentConditionsListVIew = (ListView) rootView
				.findViewById(R.id.current_conditions_list);

		loadServerData();

		((MainActivity) activity).onSectionAttached("Current Conditions");

		setEventListeners();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadServerData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void setEventListeners() {
		currentConditionsListVIew
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						CurrentWeather selectedModel = (CurrentWeather) cweatherModelList
								.get(position);
						Log.i(CommonUtility.CURR_WEAHTER_ADAPTER, "model = "
								+ selectedModel);

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

	private void loadServerData() {
		Log.i(CommonUtility.CurrentWeatherFragment, "loadServerData()");
		new CurrentConditionsLoader().execute();
	}

	private class CurrentConditionsLoader extends
			AsyncTask<Void, Integer, ArrayList<CurrentWeather>> {
		protected ProgressDialog progressDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Current Conditions");
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
		protected ArrayList<CurrentWeather> doInBackground(Void... arg0) {
			cweatherModelList = new ArrayList<CurrentWeather>();

			// TODO take shared preferences object out for common use.
			SharedPreferences prefs = CurrentWeatherFragment.this.getActivity()
					.getSharedPreferences("edu.wsu.weather.agweathernet",
							Context.MODE_PRIVATE);
			String username = prefs.getString("username", "");
			String authToken = prefs.getString("auth_token", "");

			String API_URL = CommonUtility.HOST_URL + "test/cweather.php";
			API_URL += "?uname=" + username;
			API_URL += "&auth_token=" + authToken;

			Log.i(CommonUtility.CurrentWeatherFragment, "API_URL = " + API_URL);

			String resultString = "Doing in background";

			try {
				Log.i(CommonUtility.CurrentWeatherFragment,
						"getting current conditions background");

				resultString = HttpRequestWrapper.getString(
						((AgWeatherNetApp) activity.getApplication())
								.getHttpClient(), ((AgWeatherNetApp) activity
								.getApplication()).getHttpContext(), API_URL);

				CurrentWeather model;

				JSONArray generalJobj = new JSONArray(resultString);

				for (int i = 0; i < generalJobj.length(); i++) {
					model = new CurrentWeather();

					JSONObject jobj = generalJobj.getJSONObject(i);

					model.setUnitId(jobj.getString("unit_id"));
					model.setAirTemp(jobj.getString("air_temp"));
					model.setSoilTemp(jobj.getString("soil_temp_8_in"));
					model.setRelHumidity(jobj.getString("rel_humidity"));
					model.setDewPoint(jobj.getString("dewpoint"));
					model.setLeafWetness(jobj.getString("leaf_wetness"));
					model.setSoilMoist(jobj.getString("soil_mois_8_in"));
					model.setSolarRad(jobj.getString("solar_rad"));
					model.setPrecip(jobj.getString("precip"));
					model.setWindSpeed(jobj.getString("wind_speed"));
					model.setStationName(jobj.getString("station_name"));
					model.setStationLat(jobj.getString("station_latdeg"));
					model.setStationLong(jobj.getString("station_lngdeg"));

					cweatherModelList.add(model);
				}
				Log.i(CommonUtility.ALERTS_ACT_STR,
						"alerts list retrieved, size() = "
								+ cweatherModelList.size());

			} catch (ClientProtocolException clientProtEx) {
				clientProtEx.printStackTrace();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return cweatherModelList;
		}

		@Override
		protected void onPostExecute(ArrayList<CurrentWeather> result) {
			Log.i(CurrentWeatherFragment,
					"current conditions retrieved, size() = " + result.size());
			adapter = new CurrentWeatherDataAdapter(context, cweatherModelList,
					getFragmentManager());
			currentConditionsListVIew.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
}
