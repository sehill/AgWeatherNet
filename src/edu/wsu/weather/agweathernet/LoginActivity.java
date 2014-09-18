package edu.wsu.weather.agweathernet;

import static android.widget.Toast.LENGTH_LONG;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.wsu.weather.agweathernet.helpers.AgWeatherNetApp;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;

public class LoginActivity extends Activity {

	TextView loginTip;
	EditText username;
	EditText password;
	Button loginButton;

	SharedPreferences prefs;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private Context context;
	String SENDER_ID = "112870244412";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	public String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		checkAlertConnection();

		initializeFields();

		setEventListeners();

		// Check device for Play Services APK. If check succeeds,
		// proceed with GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);
			Log.i(CommonUtility.LOGIN_ACT_STR, regid);
			if (regid.isEmpty()) {
				registerInBackground();
			} else {
				Log.i(CommonUtility.LOGIN_ACT_STR, regid);
			}
		} else {
			Log.i(CommonUtility.LOGIN_ACT_STR,
					"No valid Google Play Services APK found.");
		}

		redirectIfLoggedIn();
	}

	private boolean checkAlertConnection() {
		Log.i(CommonUtility.LOGIN_ACT_STR, "Checking connection");
		boolean hasConnection = CommonUtility.hasConnection(this);
		boolean hasServerConnection = false;
		if (!hasConnection) {
			try {
				Log.i(CommonUtility.LOGIN_ACT_STR, "No connection");
				Toast.makeText(
						this,
						"No internet connection. Connect to the internet and try again.",
						LENGTH_LONG).show();
			} catch (Exception e) {
				Log.d(CommonUtility.LOGIN_ACT_STR,
						"Show Dialog: " + e.getMessage());
			}
		} else {
			// TODO check connection to the server.
			hasServerConnection = true;
		}
		return hasConnection && hasServerConnection;
		// Log.i(CommonUtility.LOGIN_ACT_STR, "End of checking connection");
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(CommonUtility.LOGIN_ACT_STR,
						"This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(CommonUtility.LOGIN_ACT_STR, "Saving regId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(CommonUtility.LOGIN_ACT_STR, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(CommonUtility.LOGIN_ACT_STR, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.i(CommonUtility.LOGIN_ACT_STR, msg);

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// showToast(msg);
				Log.i(CommonUtility.LOGIN_ACT_STR,
						"registerInBackground() onPostExecute msg = " + msg);
			}
		}.execute(null, null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		return getSharedPreferences(LoginActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		Log.i(CommonUtility.LOGIN_ACT_STR, "sendRegistrationIdToBackend()");

		final String uname;

		uname = prefs.getString("username", "");

		new AsyncTask<Void, Void, JSONObject>() {
			@Override
			protected JSONObject doInBackground(Void... params) {
				HttpClient httpClient = ((AgWeatherNetApp) getApplication())
						.getHttpClient();

				String API_URL = CommonUtility.HOST_URL + "test/regid.php";
				API_URL += "?regid=" + regid;
				API_URL += "&uname=" + uname;
				Log.i(CommonUtility.LOGIN_ACT_STR, API_URL);
				HttpGet get = new HttpGet(API_URL);

				try {
					HttpResponse resp = httpClient.execute(get,
							((AgWeatherNetApp) getApplication())
									.getHttpContext());
					String resultString = EntityUtils
							.toString(resp.getEntity());
					JSONObject jsonObj = new JSONObject(resultString);
					return jsonObj;
				} catch (Exception ex) {
					Log.e(CommonUtility.LOGIN_ACT_STR, ex.getMessage());
				}
				return null;
			}

			protected void onPostExecute(JSONObject result) {
				String resp = null;
				try {
					resp = result.getString("result");
				} catch (JSONException e) {
					resp = null;
				}
				if (resp == null || resp.isEmpty()) { // TO DO do something
														// because server did
														// not
														// receive it.
				}
			};
		}.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	protected void redirectIfLoggedIn() {
		String defVal = "empty";
		String userId = prefs.getString("userId", defVal);
		String authToken = prefs.getString("auth_token", defVal);

		if (!userId.equals(defVal) && !authToken.equals(defVal)) {
			Log.i(CommonUtility.LOGIN_ACT_STR,
					"User already logged in. UserId = " + userId
							+ " and auth = " + authToken);
			openActivity(MainActivity.class);

		} else {
			Log.i("LoginAcitvity", "user not logged in");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void setEventListeners() {
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkAlertConnection()) {
					Log.i(CommonUtility.LOGIN_ACT_STR,
							"Calling user login task");
					UserLoginTask ult = new UserLoginTask(
							LoginActivity.this.username.getText().toString(),
							LoginActivity.this.password.getText().toString());
					ult.execute();
				}
			}
		});
	}

	protected void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

	@SuppressWarnings("rawtypes")
	protected void openActivity(Class cls) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), cls);
		startActivity(intent);
		finish();
	}

	private void initializeFields() {
		context = getApplicationContext();

		loginTip = (TextView) findViewById(R.id.loginTip);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);

		prefs = this.getSharedPreferences("edu.wsu.weather.agweathernet",
				Context.MODE_PRIVATE);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Custom.ttf");

		loginTip.setTypeface(tf);
		username.setTypeface(tf);
		password.setTypeface(tf);
		loginButton.setTypeface(tf);
	}

	public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

		private final String username;
		private final String password;

		UserLoginTask(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			Log.i(CommonUtility.LOGIN_ACT_STR, "executing user login task...");

			String API_URL = CommonUtility.HOST_URL + "test/login.php";

			API_URL += "?uname=" + username + "&passwd=" + password;

			Log.i(CommonUtility.LOGIN_ACT_STR, API_URL);

			try {
				String resultString = HttpRequestWrapper.getString(
						((AgWeatherNetApp) getApplication()).getHttpClient(),
						((AgWeatherNetApp) getApplication()).getHttpContext(),
						API_URL);

				JSONObject jsonObj = new JSONObject(resultString);

				return jsonObj;

			} catch (Exception ex) {
				Log.e(CommonUtility.LOGIN_ACT_STR, ex.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				Log.i(CommonUtility.LOGIN_ACT_STR,
						"onPostExecute - user login task...");
				String res = result.getString("result");
				Log.i(CommonUtility.LOGIN_ACT_STR, result.toString());
				if (res == null) {// something went wrong
					Toast.makeText(getApplicationContext(),
							"Something went wrong. Try again",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (res.equals("ok")) {

					prefs.edit()
							.putString("userId", result.getString("userid"))
							.apply();

					prefs.edit()
							.putString("auth_token",
									result.getString("auth_token")).apply();

					Log.i(CommonUtility.LOGIN_ACT_STR,
							result.getString("auth_token"));

					prefs.edit().putString("username", username).apply();

					sendRegistrationIdToBackend();

					openActivity(MainActivity.class);

				} else if (res.equals("wu")) { // Wrong user account
					LoginActivity.this.password
							.setError(getString(R.string.incorrect_login_combination));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
