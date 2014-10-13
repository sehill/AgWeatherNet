package edu.wsu.weather.agweathernet;

import java.text.DecimalFormat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtility {
	public static final String HOST_URL = "http://bartlett.prosser.wsu.edu:8080/";
	public static final String SENDER_ID = "112870244412";
	public final static String TAG = "GeneralAppTag";
	public final static String API_KEY = "AIzaSyBkNDtqEOrjMhE6x8S_dxFI31LAjRm8YBU";

	public final static String LOGIN_ACT_STR = "LoginFragment";
	public final static String ALERTS_ACT_STR = "AlertsFragment";
	public final static String SINGLE_ALERT_ACT_STR = "SingleAlertActivity";
	public final static String STATIONS_TAG = "StationsFragment";
	public final static String SINGLE_STATION_TAG = "SingleStationFragment";
	public final static String HTTP_REQUEST_WRAPPER = "HttpRequestWrapper";
	public static final String MAIN_ACTIVITY = "MainActivity";
	public static final String NAVIGATION_DRAWER_FRAG_TAG = "NavigationDrawerFragment";
	public static final String HOME_FRAG_TAG = "HomeFragment";
	public static final DecimalFormat DEF_DECIMAL_FORMAT = new DecimalFormat(
			"0.00");
	public static final DecimalFormat DECIMAL_FORMAT_SINGLE = new DecimalFormat(
			"0.0");
	public static final DecimalFormat DECIMAL_FORMAT_NONE = new DecimalFormat(
			"0");
	public static final String MAP_FRAG_TAG = "MapFragment";
	public static final String LOADING_PEASE_WAIT = "Loading... Please Wait";
	public static final int MULTIPLY_LAT_LNG_BY = -1;
	public static final String NEWS_TAG = "AWNewsFragment";
	public static final String CURR_WEAHTER_ADAPTER = "CurrentWeatherAdapter";
	public static final String CurrentWeatherFragment = "CurrentWeatherFragment";
	public static final String DONATION_FRAG = "DonationFragment";

	/**
	 * Checks if the device has Internet connection.
	 * 
	 * @return <code>true</code> if the phone is connected to the Internet.
	 */
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}
}