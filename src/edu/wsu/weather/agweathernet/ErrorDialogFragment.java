package edu.wsu.weather.agweathernet;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class ErrorDialogFragment extends DialogFragment {
	private Dialog mDialog;

	public ErrorDialogFragment() {
		super();
		mDialog = null;
	}

	public void setDialog(Dialog dialog) {
		mDialog = dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return mDialog;
	}

	public void show(FragmentManager supportFragmentManager, String string) {

	}
}