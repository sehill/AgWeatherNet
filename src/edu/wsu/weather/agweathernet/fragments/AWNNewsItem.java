package edu.wsu.weather.agweathernet.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;

public class AWNNewsItem extends BaseFragment {

	String link;
	String entryType;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.news_item_webview, container,
				false);
		final ProgressDialog progressDialog = new ProgressDialog(activity);

		Bundle bundle = getArguments();
		if (bundle != null) {
			link = getArguments().getString("link");
			entryType = getArguments().getString("entry_type");
		}

		String title = "News & Outlooks";
		if (entryType == "News") {
			title = "News";
		} else {
			title = "Outlooks";
		}
		((MainActivity) activity).onSectionAttached(title);

		final WebView webView = (WebView) rootView
				.findViewById(R.id.newsItemWebView);
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
				progressDialog.dismiss();
			}
		});
		Uri url = Uri.parse(link);
		String queryParam = url.getQueryParameter("page");
		progressDialog.setMessage(CommonUtility.LOADING_PEASE_WAIT);
		progressDialog.show();
		webView.loadUrl(CommonUtility.HOST_URL + "?page=" + queryParam);

		return rootView;
	}
}
