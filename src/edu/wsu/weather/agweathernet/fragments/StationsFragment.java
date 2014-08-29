package edu.wsu.weather.agweathernet.fragments;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
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
import edu.wsu.weather.agweathernet.helpers.StationModel;
import edu.wsu.weather.agweathernet.helpers.StationsAdapter;

public class StationsFragment extends BaseFragment {
	ListView stationsListView;
	StationsAdapter adapter;
	ArrayList<StationModel> stationsModelList;
	String searchQuery = "";
	SearchView searchView;
	SearchView.OnQueryTextListener queryTextListener;

	public StationsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(CommonUtility.STATIONS_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		context = getActivity().getApplicationContext();
		activity = getActivity();

		queryTextListener = new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i("SearchView.OnQueryTextListener", query);
				searchQuery = query;
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

		@Override
		protected ArrayList<StationModel> doInBackground(Void... arg0) {

			stationsModelList = new ArrayList<StationModel>();

			HttpClient httpClient = new DefaultHttpClient();

			String API_URL = CommonUtility.HOST_URL
					+ "test/stations.php?n=30&name=" + searchQuery;
			Log.i(CommonUtility.STATIONS_TAG, API_URL);
			HttpGet get = new HttpGet(API_URL);

			String resultString = "Doing in background";

			try {
				Log.i(CommonUtility.STATIONS_TAG, "getting stations background");

				HttpResponse resp = httpClient.execute(get);

				resultString = EntityUtils.toString(resp.getEntity());

				StationModel model;
				JSONArray generalJobj = new JSONArray(resultString);

				for (int i = 0; i < generalJobj.length(); i++) {
					model = new StationModel();

					JSONObject jobj = generalJobj.getJSONObject(i);
					model.setUnitId(jobj.getString("unit_id"));
					model.setName(jobj.getString("station_name"));
					model.setCounty(jobj.getString("county"));
					model.setInstallationDate(jobj
							.getString("installation_date"));
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

			return stationsModelList;
		}

		@Override
		protected void onPostExecute(ArrayList<StationModel> result) {
			adapter = new StationsAdapter(context, stationsModelList);
			stationsListView.setAdapter(adapter);
		}
	}
}
