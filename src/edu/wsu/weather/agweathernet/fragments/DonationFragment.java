package edu.wsu.weather.agweathernet.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;

public class DonationFragment extends BaseFragment {
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.donation_webview, container,
				false);

		((MainActivity) activity).onSectionAttached("Give to WSU");

		Log.i(CommonUtility.DONATION_FRAG, "onCreateView");
		final WebView webView = (WebView) rootView
				.findViewById(R.id.donationWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		final String spineDivId = "spine";
		final String toolbarDivId = "toolbar";
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				webView.loadUrl("javascript:(function(){ "
						+ "document.getElementById('"
						+ spineDivId
						+ "').parentElement.removeChild(document.getElementById('"
						+ spineDivId
						+ "'));"
						+ "document.getElementById('"
						+ toolbarDivId
						+ "').parentElement.removeChild(document.getElementById('"
						+ toolbarDivId
						+ "'));"
						+ "document.getElementById('main').firstChild.style.marginTop = '-30px'; })()");
			}
		});
		webView.loadUrl("http://bartlett.prosser.wsu.edu:8080/?p=90350");

		return rootView;
	}
}
