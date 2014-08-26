package edu.wsu.weather.agweathernet;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import edu.wsu.weather.agweathernet.helpers.SpinnerModel;

public class SingleAlertActivity extends Activity {

	private EditText editName;
	private Spinner alertEventSpinner;
	private EditText editTresholdValue;
	private Spinner unitTypeSpinner;
	private Spinner alertMethodSpinner;
	private EditText editAddress;
	private Spinner deliveryStatusSpinner;
	private EditText editStartTime;
	private EditText editServiceProvider;
	private Spinner stationsSpinner;

	private Button saveAlert;

	private RelativeLayout singleAlertLayout;

	private String alertId;

	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_alert);

		initializeProperties();

		setSpinner(stationsSpinner, "test/commonquery.php?qname=stations");
		setSpinner(alertEventSpinner, "test/commonquery.php?qname=alertevents");
		setSpinner(unitTypeSpinner, "test/commonquery.php?qname=unittypes");
		setSpinner(alertMethodSpinner,
				"test/commonquery.php?qname=alertmethods");
		setSpinner(deliveryStatusSpinner,
				"test/commonquery.php?qname=deliverystatuses");

		setEventListeners();

		setAlertId();

		if (alertId == null || alertId.isEmpty()) {
			setAlertEnabled(true);
		} else {
			getAlertById();
		}

	}

	private void setSpinner(final Spinner sp, final String url) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(CommonUtility.HOST_URL + url);
				try {
					HttpResponse response = client.execute(get);
					String respString = EntityUtils.toString(response
							.getEntity());
					return respString;
				} catch (Exception ex) {
					Log.e(CommonUtility.SINGLE_ALERT_ACT_STR, ex.getMessage());
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				List<SpinnerModel<String>> options = new ArrayList<SpinnerModel<String>>();
				try {
					JSONArray jArr = new JSONArray(result);
					JSONObject jObj;
					for (int i = 0; i < jArr.length(); i++) {
						jObj = jArr.getJSONObject(i);
						options.add(new SpinnerModel<String>(jObj
								.getString("key"), jObj.getString("value")));
					}
					Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, options.size()
							+ "");
				} catch (JSONException ex) {
					Log.e(CommonUtility.SINGLE_ALERT_ACT_STR, ex.getMessage());
				}

				KeyValueSpinner adapter = new KeyValueSpinner(
						SingleAlertActivity.this, options);

				sp.setAdapter(adapter);

				Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "Adapter set");
			};
		}.execute();
	}

	private void setAlertId() {
		Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "gettin intent");
		Intent intent = getIntent();
		Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "got intent, getting extras");
		Bundle extras = intent.getExtras();
		Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "got extras ");
		if (extras != null && extras.containsKey("id")) {
			Log.i(CommonUtility.SINGLE_ALERT_ACT_STR,
					"extras: " + extras.toString());
			alertId = extras.getString("id");
			Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "extra id: " + alertId);
		}
	}

	private void setEventListeners() {
		saveAlert.setOnClickListener(onAlertSave());
		alertEventSpinner.setOnItemSelectedListener(spinnerItemSelected());
	}

	private OnItemSelectedListener spinnerItemSelected() {
		return new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				KeyValueSpinner adapter = (KeyValueSpinner) alertEventSpinner
						.getAdapter();
				String eventId = adapter.getIdFromIndex(position);
				if (eventId.equals("highrainreport")) {
					editStartTime.setVisibility(View.VISIBLE);
				} else {
					editStartTime.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.single_alert, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.editAlert:
			setAlertEnabled(true);
			return true;
		case R.id.newAlert:
			Intent intent = new Intent();
			intent.setClass(SingleAlertActivity.this, SingleAlertActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setAlertEnabled(boolean enabled) {
		saveAlert.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
		int childrenQuantity = singleAlertLayout.getChildCount();
		for (int i = 0; i < childrenQuantity; i++) {
			View layoutView = singleAlertLayout.getChildAt(i);
			if (layoutView instanceof EditText) {
				((EditText) layoutView).setEnabled(enabled);
			} else if (layoutView instanceof Spinner) {
				((Spinner) layoutView).setEnabled(enabled);
			}
		}

	}

	private OnClickListener onAlertSave() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, String>() {
					@SuppressWarnings("unchecked")
					@Override
					protected String doInBackground(Void... params) {
						HttpClient httpClient = new DefaultHttpClient();

						HttpPost post = new HttpPost(CommonUtility.HOST_URL
								+ "test/savealert.php");

						Log.i("TRR", "post URL is " + CommonUtility.HOST_URL
								+ "test/savealert.php");
						try {
							List<NameValuePair> nameValues = new ArrayList<NameValuePair>();

							if (alertId == null || alertId.isEmpty()) {
								alertId = "-1";

								String uname = prefs.getString("username", "");
								nameValues.add(new BasicNameValuePair("uname",
										uname));
							}
							Log.i("TRR", alertId);
							nameValues
									.add(new BasicNameValuePair("id", alertId));

							nameValues.add(new BasicNameValuePair("repname",
									editName.getText().toString()));
							nameValues
									.add(new BasicNameValuePair(
											"station",
											(String) ((SpinnerModel<String>) stationsSpinner
													.getSelectedItem()).getId()));

							nameValues
									.add(new BasicNameValuePair(
											"event",
											(String) ((SpinnerModel<String>) alertEventSpinner
													.getSelectedItem()).getId()));

							nameValues.add(new BasicNameValuePair("threshold",
									editTresholdValue.getText().toString()));
							nameValues.add(new BasicNameValuePair("starttime",
									editStartTime.getText().toString()));
							nameValues
									.add(new BasicNameValuePair(
											"unit",
											(String) ((SpinnerModel<String>) unitTypeSpinner
													.getSelectedItem()).getId()));
							nameValues
									.add(new BasicNameValuePair(
											"method",
											(String) ((SpinnerModel<String>) alertMethodSpinner
													.getSelectedItem()).getId()));
							nameValues.add(new BasicNameValuePair("address",
									editAddress.getText().toString()));
							nameValues
									.add(new BasicNameValuePair(
											"delivery",
											(String) ((SpinnerModel<String>) deliveryStatusSpinner
													.getSelectedItem()).getId()));
							nameValues.add(new BasicNameValuePair("provider",
									editServiceProvider.getText().toString()));
							Log.i("TRR", nameValues.size() + "");

							post.setEntity(new UrlEncodedFormEntity(nameValues));

							Log.i("TRR", "entity set");

							HttpResponse resp = httpClient.execute(post);

							if (resp != null) {
								Log.i("TRR", "" + resp.toString());
							}

							String result = EntityUtils.toString(resp
									.getEntity());

							Log.i("TRR", "result = " + result);

							return result;

						} catch (Exception e) {
							Log.i("TRR", "uuu" + e.getMessage());
							e.printStackTrace();
							return "empty" + e.getMessage();
						}
					}

					@Override
					protected void onPostExecute(String result) {
						Log.i("TRR", "EEE" + result);
					}
				}.execute();

				Toast.makeText(getApplicationContext(), "Alert has been saved",
						Toast.LENGTH_SHORT).show();
				setAlertEnabled(false);
			}
		};
	}

	private void initializeProperties() {
		editName = (EditText) findViewById(R.id.editName);

		alertEventSpinner = (Spinner) findViewById(R.id.alertEventList);
		alertEventSpinner.setEnabled(false);

		editTresholdValue = (EditText) findViewById(R.id.editTresholdValue);

		unitTypeSpinner = (Spinner) findViewById(R.id.unitTypeList);
		unitTypeSpinner.setEnabled(false);

		alertMethodSpinner = (Spinner) findViewById(R.id.alertMethodList);
		alertMethodSpinner.setEnabled(false);

		editAddress = (EditText) findViewById(R.id.editAddress);

		deliveryStatusSpinner = (Spinner) findViewById(R.id.deliveryStatusList);
		deliveryStatusSpinner.setEnabled(false);

		editServiceProvider = (EditText) findViewById(R.id.editServiceProvider);
		editStartTime = (EditText) findViewById(R.id.editStartTime);

		stationsSpinner = (Spinner) findViewById(R.id.stationsList);
		stationsSpinner.setEnabled(false);

		saveAlert = (Button) findViewById(R.id.saveAlert);
		singleAlertLayout = (RelativeLayout) findViewById(R.id.singleAlertLayout);

		prefs = this.getSharedPreferences("edu.wsu.weather.agweathernet",
				Context.MODE_PRIVATE);
	}

	private void getAlertById() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				String url = CommonUtility.HOST_URL + "test/getalert.php?id="
						+ alertId;
				HttpClient webClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				try {
					HttpResponse response = webClient.execute(get);
					String respString = EntityUtils.toString(response
							.getEntity());
					return respString;
				} catch (Exception ex) {
					Log.e(CommonUtility.SINGLE_ALERT_ACT_STR, ex.getMessage());
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, result);
					JSONArray jsonArr = new JSONArray(result);
					JSONObject jsonObj = jsonArr.getJSONObject(0);
					AlertsModel selectedModel = new AlertsModel();
					selectedModel.Id = alertId;
					selectedModel.alertEvent = jsonObj.getString("alert_event");
					selectedModel.reportName = jsonObj.getString("report_name");
					selectedModel.tresholdValue = jsonObj
							.getString("threshold_value");
					selectedModel.station = jsonObj.getString("station");
					selectedModel.unit = jsonObj.getString("unit");
					selectedModel.alertMethod = jsonObj
							.getString("alert_method");
					selectedModel.address = jsonObj.getString("addr_txt");
					selectedModel.deliveryStatus = jsonObj
							.getString("delivery_status");
					selectedModel.startTime = jsonObj.getString("start_time");
					selectedModel.serviceProvider = jsonObj
							.getString("service_provider");

					setFieldsContent(selectedModel);
				} catch (JSONException e) {
					Log.e(CommonUtility.SINGLE_ALERT_ACT_STR, e.getMessage());
				}
			}
		}.execute();

	}

	private void setFieldsContent(AlertsModel model) {

		KeyValueSpinner spinnerAdapter = (KeyValueSpinner) stationsSpinner
				.getAdapter();
		int selectedPosition = spinnerAdapter.getIndexById(model.station);
		stationsSpinner.setSelection(selectedPosition);

		spinnerAdapter = (KeyValueSpinner) alertEventSpinner.getAdapter();
		selectedPosition = spinnerAdapter.getIndexById(model.alertEvent);
		alertEventSpinner.setSelection(selectedPosition);
		if (model.alertEvent != null
				&& model.alertEvent.equals("highrainreport")) {
			editStartTime.setVisibility(View.VISIBLE);
		}

		spinnerAdapter = (KeyValueSpinner) unitTypeSpinner.getAdapter();
		selectedPosition = spinnerAdapter.getIndexById(model.unit);
		unitTypeSpinner.setSelection(selectedPosition);

		spinnerAdapter = (KeyValueSpinner) alertMethodSpinner.getAdapter();
		selectedPosition = spinnerAdapter.getIndexById(model.alertMethod);
		alertMethodSpinner.setSelection(selectedPosition);

		spinnerAdapter = (KeyValueSpinner) deliveryStatusSpinner.getAdapter();
		selectedPosition = spinnerAdapter.getIndexById(model.deliveryStatus);
		deliveryStatusSpinner.setSelection(selectedPosition);

		editName.setText(model.reportName);
		editTresholdValue.setText(model.tresholdValue);
		editAddress.setText(model.address);
		editServiceProvider.setText(model.serviceProvider);
		editStartTime.setText(model.startTime);
	}
}
