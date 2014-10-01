package edu.wsu.weather.agweathernet.fragments;

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
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import edu.wsu.weather.agweathernet.AlertsModel;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.KeyValueSpinner;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.SpinnerModel;

public class SingleAlertFragment extends BaseFragment {

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
	String unameQstring;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		activity = getActivity();
		context = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_single_alert,
				container, false);

		initializeProperties(rootView);

		unameQstring = "uname=" + prefs.getString("username", "")
				+ "&auth_token=" + prefs.getString("auth_token", "") + "&";
		setSpinner(stationsSpinner, "test/commonquery.php?" + unameQstring
				+ "qname=stations");
		setSpinner(alertEventSpinner, "test/commonquery.php?" + unameQstring
				+ "qname=alertevents");
		setSpinner(unitTypeSpinner, "test/commonquery.php?" + unameQstring
				+ "qname=unittypes");
		setSpinner(alertMethodSpinner, "test/commonquery.php?" + unameQstring
				+ "qname=alertmethods");
		setSpinner(deliveryStatusSpinner, "test/commonquery.php?"
				+ unameQstring + "qname=deliverystatuses");

		setEventListeners();

		((MainActivity) activity).onSectionAttached("Alert");

		// TODO if arguments not set, show message and redirect back;
		alertId = getArguments().getString("id");

		if (alertId == null || alertId.isEmpty()) {
			setAlertEnabled(true);
		} else {
			getAlertById();
		}

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.single_alert, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void setSpinner(final Spinner sp, final String url) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					String respString = HttpRequestWrapper.getString(
							((AgWeatherNetApp) activity.getApplication())
									.getHttpClient(),
							((AgWeatherNetApp) activity.getApplication())
									.getHttpContext(), CommonUtility.HOST_URL
									+ url);
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

				KeyValueSpinner adapter = new KeyValueSpinner(getActivity(),
						options);

				sp.setAdapter(adapter);

				Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, "Adapter set");
			};
		}.execute();
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
						HttpClient httpClient = ((AgWeatherNetApp) activity
								.getApplication()).getHttpClient();

						HttpPost post = new HttpPost(CommonUtility.HOST_URL
								+ "test/savealert.php?auth_token="
								+ getPreferenceValue("auth_token", "")
								+ "&uname="
								+ getPreferenceValue("username", ""));

						Log.i("TRR", "post URL is " + CommonUtility.HOST_URL
								+ "test/savealert.php");
						try {
							List<NameValuePair> nameValues = new ArrayList<NameValuePair>();

							if (alertId == null || alertId.isEmpty()) {
								alertId = "-1";
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

							HttpResponse resp = httpClient
									.execute(post, ((AgWeatherNetApp) activity
											.getApplication()).getHttpContext());

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

				Toast.makeText(context, "Alert has been saved",
						Toast.LENGTH_SHORT).show();
				setAlertEnabled(false);
			}
		};
	}

	private void initializeProperties(View rootView) {
		editName = (EditText) rootView.findViewById(R.id.editName);
		Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, (editName == null) + "");
		alertEventSpinner = (Spinner) rootView
				.findViewById(R.id.alertEventList);
		alertEventSpinner.setEnabled(false);

		editTresholdValue = (EditText) rootView
				.findViewById(R.id.editTresholdValue);

		unitTypeSpinner = (Spinner) rootView.findViewById(R.id.unitTypeList);
		unitTypeSpinner.setEnabled(false);

		alertMethodSpinner = (Spinner) rootView
				.findViewById(R.id.alertMethodList);
		alertMethodSpinner.setEnabled(false);

		editAddress = (EditText) rootView.findViewById(R.id.editAddress);

		deliveryStatusSpinner = (Spinner) rootView
				.findViewById(R.id.deliveryStatusList);
		deliveryStatusSpinner.setEnabled(false);

		editServiceProvider = (EditText) rootView
				.findViewById(R.id.editServiceProvider);
		editStartTime = (EditText) rootView.findViewById(R.id.editStartTime);

		stationsSpinner = (Spinner) rootView.findViewById(R.id.stationsList);
		stationsSpinner.setEnabled(false);

		saveAlert = (Button) rootView.findViewById(R.id.saveAlert);
		singleAlertLayout = (RelativeLayout) rootView
				.findViewById(R.id.singleAlertLayout);

		prefs = activity.getSharedPreferences("edu.wsu.weather.agweathernet",
				Context.MODE_PRIVATE);
	}

	private void getAlertById() {

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
						Log.i(CommonUtility.STATIONS_TAG, "HTTP GET Cancelled");
					}
				});

				progressDialog.show();
			};

			@Override
			protected String doInBackground(Void... params) {

				String url = CommonUtility.HOST_URL + "test/getalert.php?"
						+ unameQstring + "id=" + alertId + "&auth_token="
						+ getPreferenceValue("auth_token", "");
				Log.i(CommonUtility.SINGLE_ALERT_ACT_STR, url);
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
				progressDialog.dismiss();
			}
		}.execute();

	}

	private void setFieldsContent(AlertsModel model) {
		editName.setText(model.reportName);

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

		editTresholdValue.setText(model.tresholdValue);
		editAddress.setText(model.address);
		editServiceProvider.setText(model.serviceProvider);
		editStartTime.setText(model.startTime);
	}
}
