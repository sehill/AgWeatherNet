package edu.wsu.weather.agweathernet.helpers;

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
import android.util.Log;
import android.widget.ListView;
import edu.wsu.weather.agweathernet.AlertsAdapter;
import edu.wsu.weather.agweathernet.AlertsModel;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.fragments.AlertsFragment;

public class AlertsLoader extends
		AsyncTask<Void, Integer, ArrayList<AlertsModel>> {
	protected ProgressDialog progressDialog;
	ArrayList<AlertsModel> alertModelList;
	Activity activity;
	ListView alertsListView;
	AlertsFragment fragment;

	public AlertsLoader(Activity activity, ListView alertsListView,
			AlertsFragment fragment) {
		this.activity = activity;
		this.alertsListView = alertsListView;
		this.fragment = fragment;
	}

	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("My Alerts");
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
	protected ArrayList<AlertsModel> doInBackground(Void... arg0) {
		alertModelList = new ArrayList<AlertsModel>();

		// TODO take shared preferences object out for common use.
		SharedPreferences prefs = activity.getSharedPreferences(
				"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
		String username = prefs.getString("username", "");
		String authToken = prefs.getString("auth_token", "");

		String API_URL = CommonUtility.HOST_URL + "test/alerts.php";
		API_URL += "?uname=" + username;
		API_URL += "&auth_token=" + authToken;

		Log.i(CommonUtility.ALERTS_ACT_STR, "API_URL = " + API_URL);

		String resultString = "Doing in background";

		try {
			Log.i(CommonUtility.ALERTS_ACT_STR, "getting alerts background");

			resultString = HttpRequestWrapper.getString(
					((AgWeatherNetApp) activity.getApplication())
							.getHttpClient(), ((AgWeatherNetApp) activity
							.getApplication()).getHttpContext(), API_URL);

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
					"alerts list retrieved, size() = " + alertModelList.size());

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
		Log.i("AlertsActivity", "alerts retrieved, size() = " + result.size());
		AlertsAdapter adapter;
		adapter = new AlertsAdapter(activity.getApplicationContext(),
				alertModelList, fragment.getFragmentManager());
		alertsListView.setAdapter(adapter);
		progressDialog.dismiss();
	}
}
