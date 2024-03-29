package edu.wsu.weather.agweathernet;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.wsu.weather.agweathernet.fragments.AWNewsFragment;
import edu.wsu.weather.agweathernet.fragments.AlertsFragment;
import edu.wsu.weather.agweathernet.fragments.CurrentWeatherFragment;
import edu.wsu.weather.agweathernet.fragments.DonationFragment;
import edu.wsu.weather.agweathernet.fragments.HomeFragment;
import edu.wsu.weather.agweathernet.fragments.MapFragment;
import edu.wsu.weather.agweathernet.fragments.StationsFragment;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	public Map<String, String> extras;
	public int selectedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(CommonUtility.MAIN_ACTIVITY, "onCreate");
		Bundle bundle = getIntent().getExtras();
		Log.i(CommonUtility.MAIN_ACTIVITY, "getIntent().getExtras() bundle = "
				+ bundle);
		if (bundle != null) {
			String openFragment = bundle.getString("fragment");
			switch (openFragment) {
			case "stations":
				selectedPosition = 2; // TODO replace
				extras = new HashMap<String, String>();
				extras.put("searchQuery", bundle.getString("searchQuery"));
				Log.i(CommonUtility.MAIN_ACTIVITY, "stations extras and position set");
				break;
			case "some other":
				// TODO do something
				break;
			default:
				break;
			}
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(CommonUtility.MAIN_ACTIVITY, "onCreate");

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);

		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onBackPressed() {
		int fragments = getSupportFragmentManager().getBackStackEntryCount();
		if (fragments == 1) {
			finish();
		}
		super.onBackPressed();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position,
			Map<String, String> extras) {

		Log.i(CommonUtility.MAIN_ACTIVITY,
				"onNavigationDrawerItemSelected position = " + position);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		Fragment newFrag;

		switch (position) {
		case 0:
			newFrag = new HomeFragment();
			break;
		case 1:
			newFrag = new AlertsFragment();
			break;
		case 2:
			newFrag = new StationsFragment();
			break;
		case 3:
			newFrag = new MapFragment();
			break;
		case 4:
			newFrag = new AWNewsFragment();
			if (extras == null) {
				extras = new HashMap<String, String>();
			}
			extras.put("entry_type", "News");
			break;
		case 5:
			newFrag = new AWNewsFragment();
			if (extras == null) {
				extras = new HashMap<String, String>();
			}
			extras.put("entry_type", "Outlooks");
			break;
		case 6:
			newFrag = new CurrentWeatherFragment();
			break;
		case 7:
			newFrag = new DonationFragment();
			break;
		default:
			newFrag = new HomeFragment();
			break;
		}

		Log.i(CommonUtility.MAIN_ACTIVITY,
				"onNavigationDrawerItemSelected() extras=" + extras);
		if (extras != null && extras.size() > 0) {
			Bundle args = new Bundle();
			for (String s : extras.keySet()) {
				args.putString(s, extras.get(s));
			}
			newFrag.setArguments(args);
			Log.i(CommonUtility.MAIN_ACTIVITY,
					"onNavigationDrawerItemSelected() newFrag args set");
		}

		transaction.replace(R.id.container, newFrag);

		transaction.addToBackStack(null);

		Log.i(CommonUtility.MAIN_ACTIVITY, "transaction.replace completed");

		transaction.commit();
	}

	public void onSectionAttached(String title) {
		mTitle = title;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			SharedPreferences prefs = this.getSharedPreferences(
					"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
			prefs.edit().clear().commit();
			Toast.makeText(getApplicationContext(), "You have been logged out",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setActionBarTitle(String title) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}
}
