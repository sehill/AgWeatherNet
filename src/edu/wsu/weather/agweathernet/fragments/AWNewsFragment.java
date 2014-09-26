package edu.wsu.weather.agweathernet.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.MainActivity;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.HttpRequestWrapper;
import edu.wsu.weather.agweathernet.helpers.NewRSSHandler;
import edu.wsu.weather.agweathernet.helpers.NewsAdapter;
import edu.wsu.weather.agweathernet.helpers.models.NewsEntry;

public class AWNewsFragment extends BaseFragment {
	ListView newsListView;
	NewsAdapter adapter;
	Context context;
	String entryType = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		activity = getActivity();
		context = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.news_listview, container,
				false);

		Bundle bundle = getArguments();
		if (bundle != null) {
			entryType = getArguments().getString("entry_type");
		}

		String title = "News & Outlooks";
		if (entryType == "News") {
			title = "News";
		} else {
			title = "Outlooks";
		}
		((MainActivity) activity).onSectionAttached(title);

		newsListView = (ListView) rootView.findViewById(R.id.news_listView);

		setEventListeners();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(CommonUtility.NEWS_TAG, "onResume()");
		loadServerData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void setEventListeners() {
	}

	private void loadServerData() {
		if (entryType == "News") {
			new RssService()
					.execute("http://bartlett.prosser.wsu.edu:8080/test/rss.php");
			// .execute("http://weather.wsu.edu/webservice/awnNews.RSS.class.php");
		} else /* if (entryType == "Outlooks") */{
			new RssService()// TODO URL NOT WORKING!
					.execute("http://bartlett.prosser.wsu.edu:8080/test/rss.php");
		}

	}

	public class RssService extends AsyncTask<String, Void, List<NewsEntry>> {
		protected ProgressDialog progressDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Stations");
			progressDialog.setMessage(CommonUtility.LOADING_PEASE_WAIT);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					HttpRequestWrapper.abortRequest();
					Log.i(CommonUtility.STATIONS_TAG, "HTTP GET Cancelled");
				}
			});

			progressDialog.show();
		};

		@Override
		protected List<NewsEntry> doInBackground(String... urls) {
			String feed = urls[0];
			Log.i(CommonUtility.NEWS_TAG, "Requesting url=" + feed);
			URL url = null;
			NewRSSHandler rh = new NewRSSHandler();
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();

				url = new URL(feed);

				xr.setContentHandler(rh);
				InputStreamReader inputStream = new InputStreamReader(
						url.openStream());
				BufferedReader reader = new BufferedReader(inputStream);
				StringBuilder out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				reader.close();
				// out.toString().replaceAll("&", "&amp;")
				xr.parse(feed);

				Log.e("ASYNC", "PARSING FINISHED");

			} catch (IOException e) {
				Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
				e.printStackTrace();

			} catch (SAXException e) {
				Log.e("RSS Handler SAX", e.toString());
				e.printStackTrace();

			} catch (ParserConfigurationException e) {
				Log.e("RSS Handler Parser Config", e.toString());

			}
			return rh.getList();
		}

		protected void onPostExecute(final List<NewsEntry> newsList) {
			adapter = new NewsAdapter(context, newsList, getFragmentManager());
			newsListView.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
}
