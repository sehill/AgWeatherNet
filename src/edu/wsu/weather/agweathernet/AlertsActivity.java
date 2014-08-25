package edu.wsu.weather.agweathernet;

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

import com.google.android.gms.internal.ct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AlertsActivity extends Activity {
	ListView alertsListView;
	AlertsAdapter adapter;
	ArrayList<AlertsModel> alertModelList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alerts_listview);

		alertsListView = (ListView) findViewById(R.id.alerts_list);

		loadServerData();

		setEventListeners();
	}

	private void setEventListeners() {
		alertsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();

				intent.setClass(AlertsActivity.this, SingleAlertActivity.class);

				AlertsModel selectedModel = (AlertsModel) alertModelList
						.get(position);

				Log.i(CommonUtility.ALERTS_ACT_STR, "model = " + selectedModel);

				intent.putExtra("id", selectedModel.Id);

				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadServerData();
		Log.i(CommonUtility.ALERTS_ACT_STR, "onResume()");
	}

	@Override
	protected void onDestroy() {
		Log.i(CommonUtility.ALERTS_ACT_STR, "onDestroy()");
		super.onDestroy();
		finish();
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
			SharedPreferences prefs = AlertsActivity.this.getSharedPreferences(
					"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
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
			adapter = new AlertsAdapter(getApplicationContext(), alertModelList);
			alertsListView.setAdapter(adapter);
		}
	}

	// private void reload() {
	// Log.i(CommonUtility.ALERTS_ACT_STR, "reload()");
	// loadServerData();
	// adapter.update(alertModelList);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alerts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.newAlert:
			Intent intent = new Intent();
			intent.setClass(AlertsActivity.this, SingleAlertActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
