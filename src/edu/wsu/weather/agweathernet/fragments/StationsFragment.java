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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.StationModel;
import edu.wsu.weather.agweathernet.helpers.StationsAdapter;

public class StationsFragment extends BaseFragment {
	ListView stationsListView;
	StationsAdapter adapter;
	ArrayList<StationModel> stationsModelList;

	public StationsFragment() {
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
		View rootView = inflater.inflate(R.layout.stations_listview, container,
				false);

		stationsListView = (ListView) rootView.findViewById(R.id.stations_list);

		loadServerData();

		setEventListeners();
		// ((MainActivity) getActivity()).setActionBarTitle("Stations");
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
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached("Stations");
	}

	private void setEventListeners() {
		stationsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(context, "item click", Toast.LENGTH_SHORT)
						.show();
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

			String API_URL = CommonUtility.HOST_URL + "test/stations.php?n=10";

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
