package edu.wsu.weather.agweathernet.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.R;
import edu.wsu.weather.agweathernet.helpers.models.NewsEntry;

public class NewsAdapter extends BaseAdapter {

	Context ctx;
	List<NewsEntry> newsList;
	FragmentManager fm;

	public NewsAdapter(Context ctx, List<NewsEntry> newsList, FragmentManager fm) {
		this.ctx = ctx;
		this.newsList = newsList;
		this.fm = fm;
	}

	public void update(ArrayList<NewsEntry> newsList) {
		this.newsList = newsList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (newsList == null)
			return 0;
		return newsList.size();
	}

	@Override
	public Object getItem(int index) {
		if (newsList == null)
			return null;
		return newsList.get(index);
	}

	@Override
	public long getItemId(int i) {
		return (long) i;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;

		NewsEntry newsModel = newsList.get(position);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (view == null) {
			view = inflater.inflate(R.layout.news_list_row, parent, false);
		}

		TextView title = (TextView) view.findViewById(R.id.newsTitle);
		TextView pubDate = (TextView) view.findViewById(R.id.newsPubDate);
		TextView desc = (TextView) view.findViewById(R.id.newsDesc);

		title.setText(newsModel.getTitle());
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z");
		Date dateStr = new Date();
		try {
			dateStr = formatter.parse(newsModel.getPubDate());
			pubDate.setText(new SimpleDateFormat("MMM dd, yyyy")
					.format(dateStr).toString());
		} catch (ParseException e) {
			pubDate.setText(newsModel.getPubDate());
		}
		String descTxt = newsModel.getDescription();
		if (descTxt != null && descTxt.length() > 190) {
			descTxt = descTxt.substring(0, 190);
		}
		desc.setText(descTxt);

		return view;
	}

	@Override
	public boolean isEmpty() {
		return newsList.isEmpty();
	}
}
