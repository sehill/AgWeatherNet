package edu.wsu.weather.agweathernet.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.StationModel;
import edu.wsu.weather.agweathernet.helpers.StationsAdapter;

public class StationsFragment extends BaseFragment {
	ListView stationsListView;
	StationsAdapter adapter;
	ArrayList<StationModel> stationsModelList;
	String searchQuery = "";
	SearchView searchView;
	SearchView.OnQueryTextListener queryTextListener;
	boolean showProgress = true;

	public StationsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(CommonUtility.STATIONS_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		activity = getActivity();
		context = activity.getApplicationContext();

		queryTextListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i("SearchView.OnQueryTextListener", query);
				searchQuery = query;
				showProgress = true;

				loadServerData();

				InputMethodManager imm = (InputMethodManager) activity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.i("SearchView.OnQueryTextListener - onQueryTextChange",
						newText);
				searchQuery = newText;
				showProgress = false;

				loadServerData();

				return false;
			}
		};

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.stations_listview, container,
				false);

		stationsListView = (ListView) rootView.findViewById(R.id.stations_list);

		Bundle bundle = getArguments();
		if (bundle != null) {
			searchQuery = getArguments().getString("searchQuery");
			Log.i(CommonUtility.STATIONS_TAG, searchQuery);
		}
		showProgress = true;
		loadServerData();
		((MainActivity) activity).onSectionAttached("Stations");

		setEventListeners();

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.stations, menu);

		SearchManager searchManager = (SearchManager) activity
				.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(activity
				.getComponentName()));
		this.searchView = searchView;
		searchView.setOnQueryTextListener(queryTextListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		showProgress = true;
		loadServerData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
			if (showProgress) {
				progressDialog.show();
			}
		};

		@Override
		protected ArrayList<StationModel> doInBackground(Void... arg0) {
			stationsModelList = new ArrayList<StationModel>();
			// change to using HttpRequestWrapper

			String API_URL = "";
			try {
				API_URL = CommonUtility.HOST_URL + "test/stations.php?uname="
						+ getUserName() + "&n=30&auth_token="
						+ getPreferenceValue("auth_token", "") + "&name="
						+ URLEncoder.encode(searchQuery, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.i(CommonUtility.STATIONS_TAG, API_URL);

			String resultString = "Doing in background";

			try {
				Log.i(CommonUtility.STATIONS_TAG, "getting stations background");

				resultString = HttpRequestWrapper.getString(
						((AgWeatherNetApp) activity.getApplication())
								.getHttpClient(), ((AgWeatherNetApp) activity
								.getApplication()).getHttpContext(), API_URL);

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

			} catch (ClientProtocolException clientProtEx) {
				clientProtEx.printStackTrace();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			progressDialog.dismiss();
			return stationsModelList;
		}

		@Override
		protected void onPostExecute(ArrayList<StationModel> result) {
			adapter = new StationsAdapter(activity, context, stationsModelList);
			stationsListView.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
}
