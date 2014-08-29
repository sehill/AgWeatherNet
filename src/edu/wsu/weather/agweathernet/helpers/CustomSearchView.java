package edu.wsu.weather.agweathernet.helpers;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;

public class CustomSearchView extends SearchView implements OnClickListener,
		android.view.View.OnKeyListener {

	public CustomSearchView(Context context) {
		super(context);
		this.setOnSearchClickListener(this);
		this.setSubmitButtonEnabled(true);
		this.setOnClickListener(this);
		this.setOnKeyListener(this);
	}

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		Log.i("CustomSearchView", this.getQuery().toString());
		return false;
	}

	@Override
	public void onClick(View arg0) {
		Log.i("CustomSearchView", this.getQuery().toString());

	}

	
	// @Override
	// public boolean onQueryTextChange(String newText) {
	// // Do something
	// return true;
	// }
	//
	// @Override
	// public boolean onQueryTextSubmit(String query) {
	// // Do something
	// return true;
	// }

}
