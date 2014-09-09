package edu.wsu.weather.agweathernet.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

public class BaseFragment extends Fragment {
	Context context;
	Activity activity;
	private SharedPreferences prefs;

	public SharedPreferences getPrefs() {
		setPrefs();
		return prefs;
	}

	public void setPrefs() {
		if (prefs == null) {
			prefs = activity.getSharedPreferences(
					"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
		}
	}

	public String getUserName() {
		if (prefs == null) {
			setPrefs();
		}

		return prefs.getString("username", "");
	}
}
