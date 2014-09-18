package edu.wsu.weather.agweathernet.helpers;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import edu.wsu.weather.agweathernet.CommonUtility;

public class HttpRequestWrapper {

	public HttpRequestWrapper() {
	}

	public static String getString(HttpClient httpClient,
			HttpContext localContext, String url) throws ParseException,
			IOException {
		HttpGet get = new HttpGet(url);

		String resultString = "Doing in background";

		Log.i(CommonUtility.HTTP_REQUEST_WRAPPER,
				"getString() executing... url: " + url);

		HttpResponse resp = httpClient.execute(get, localContext);

		resultString = EntityUtils.toString(resp.getEntity());

		return resultString;
	}

}