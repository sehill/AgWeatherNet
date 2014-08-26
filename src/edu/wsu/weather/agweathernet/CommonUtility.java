package edu.wsu.weather.agweathernet;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import edu.wsu.weather.agweathernet.fragments.*;

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

	public final static Map<Integer, Fragment> NAVIGATION_FRAGMENTS;
	static {
		NAVIGATION_FRAGMENTS = new HashMap<Integer, Fragment>();
		NAVIGATION_FRAGMENTS.put(0, new HomeFragment());
		NAVIGATION_FRAGMENTS.put(1, new AlertsFragment());
		NAVIGATION_FRAGMENTS.put(2, new StationsFragment());
		
	}

}
