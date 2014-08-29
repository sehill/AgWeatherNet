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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.wsu.weather.agweathernet.AlertsAdapter;
import edu.wsu.weather.agweathernet.AlertsModel;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;

public class AlertsFragment extends BaseFragment {
	ListView alertsListView;
	AlertsAdapter adapter;
	ArrayList<AlertsModel> alertModelList;

	public AlertsFragment() {
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
		View rootView = inflater.inflate(R.layout.alerts_listview, container,
				false);

		alertsListView = (ListView) rootView.findViewById(R.id.alerts_list);

		loadServerData();
		((MainActivity) activity).onSectionAttached("My Alerts");
		setEventListeners();
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alerts, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.newAlert:
			SingleAlertFragment newFrag = new SingleAlertFragment();
			Bundle args = new Bundle();
			args.putString("id", "");
			newFrag.setArguments(args);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.container, newFrag);
			ft.addToBackStack(null);
			ft.commit();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ((MainActivity) activity).onSectionAttached("My Alerts");
	}

	private void setEventListeners() {
		alertsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlertsModel selectedModel = (AlertsModel) alertModelList
						.get(position);
				Log.i(CommonUtility.ALERTS_ACT_STR, "model = " + selectedModel);

				SingleAlertFragment newFrag = new SingleAlertFragment();

				Bundle args = new Bundle();

				args.putString("id", selectedModel.Id);

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
		Log.i(CommonUtility.ALERTS_ACT_STR, "loadServerData()");
		new AlertsLoader().execute();
	}

	private class AlertsLoader extends
			AsyncTask<Void, Integer, ArrayList<AlertsModel>> {

		@Override
		protected ArrayList<AlertsModel> doInBackground(Void... arg0) {
			alertModelList = new ArrayList<AlertsModel>();
			HttpClient httpClient = new DefaultHttpClient();

			// TODO take shared preferences object out for common use.
			SharedPreferences prefs = AlertsFragment.this.getActivity()
					.getSharedPreferences("edu.wsu.weather.agweathernet",
							Context.MODE_PRIVATE);
			String username = prefs.getString("username", "");

			String API_URL = CommonUtility.HOST_URL + "test/alerts.php";
			API_URL += "?uname=" + username;

			Log.i(CommonUtility.ALERTS_ACT_STR, "API_URL = " + API_URL);
			HttpGet get = new HttpGet(API_URL);

			String resultString = "Doing in background";

			try {
				Log.i(CommonUtility.ALERTS_ACT_STR, "getting alerts background");

				HttpResponse resp = httpClient.execute(get);

				resultString = EntityUtils.toString(resp.getEntity());

				AlertsModel model;
				JSONArray generalJobj = new JSONArray(resultString);

				for (int i = 0; i < generalJobj.length(); i++) {
					model = new AlertsModel();

					JSONObject jobj = generalJobj.getJSONObject(i);

					model.Id = jobj.getString("id");
					model.reportName = jobj.getString("report_name");
					model.stationId = jobj.getString("station_id");
					model.station = jobj.getString("station");
					model.alertEvent = jobj.getString("alert_event");
					model.unit = jobj.getString("unit");
					model.tresholdValue = jobj.getString("threshold_value");
					model.startTime = jobj.getString("start_time");
					model.address = jobj.getString("addr_txt");
					model.objid = jobj.getString("id");
					model.alertMethod = jobj.getString("alert_method");
					model.deliveryStatus = jobj.getString("delivery_status");
					model.serviceProvider = jobj.getString("service_provider");

					alertModelList.add(model);
				}
				Log.i(CommonUtility.ALERTS_ACT_STR,
						"alerts list retrieved, size() = "
								+ alertModelList.size());

			} catch (ClientProtocolException clientProtEx) {
				clientProtEx.printStackTrace();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return alertModelList;
		}

		@Override
		protected void onPostExecute(ArrayList<AlertsModel> result) {
			Log.i("AlertsActivity",
					"alerts retrieved, size() = " + result.size());
			adapter = new AlertsAdapter(context, alertModelList);
			alertsListView.setAdapter(adapter);
		}
	}
}
