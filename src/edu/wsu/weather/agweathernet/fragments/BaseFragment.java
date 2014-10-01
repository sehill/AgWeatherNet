package edu.wsu.weather.agweathernet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	Context context;
	Activity activity;
	private SharedPreferences prefs;

	public SharedPreferences getPrefs() throws Exception {
		setPrefs();
		return prefs;
	}

	public void setPrefs() {
		if (prefs == null) {
			if (activity == null) {
				activity = getActivity();
			}
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

	public String getPreferenceValue(String key, String alt) {
		if (prefs == null) {
			setPrefs();
		}

		return prefs.getString(key, alt);
	}
}
