package edu.wsu.weather.agweathernet.helpers;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Application;

public class AgWeatherNetApp extends Application {
	private HttpClient httpClient = new DefaultHttpClient();
	private CookieStore cookieStore = new BasicCookieStore();
	private HttpContext localContext = new BasicHttpContext();

	@Override
	public void onCreate() {
		super.onCreate();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public HttpContext getHttpContext() {
		return localContext;
	}
}