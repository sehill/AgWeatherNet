package edu.wsu.weather.agweathernet;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SearchableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("Searchable");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);
		Log.i("Searchable", "onCreate");
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i("Searchable", "handleIntent  q = " + query);

			Intent newIntent = new Intent();
			newIntent.setClass(getApplicationContext(), MainActivity.class);
			newIntent.putExtra("fragment", "stations");
			newIntent.putExtra("searchQuery", query);
			startActivity(newIntent);
			// finish();
		}
	}
}
