package edu.wsu.weather.agweathernet;

import java.text.DecimalFormat;

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
			"#.##");

}