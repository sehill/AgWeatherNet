package edu.wsu.weather.agweathernet.helpers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.ParseException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.wsu.weather.agweathernet.CommonUtility;
import edu.wsu.weather.agweathernet.R;

public class StationsAdapter extends BaseAdapter {
	Context ctx;
	ArrayList<StationModel> stationList;
	public ImageLoader imageLoader;

	public StationsAdapter(Context ctx, ArrayList<StationModel> stationList) {
		this.ctx = ctx;
		this.stationList = stationList;
		imageLoader = new ImageLoader(ctx);
	}

	public void update(ArrayList<StationModel> stationList) {
		this.stationList = stationList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (stationList == null)
			return 0;
		return stationList.size();
	}

	@Override
	public Object getItem(int index) {
		if (stationList == null)
			return null;
		return stationList.get(index);
	}

	@Override
	public long getItemId(int i) {
		return (long) i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		StationModel stationModel = stationList.get(position);
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			view = inflater.inflate(R.layout.station_list_row, parent, false);
		}

		ImageView stationImg = (ImageView) view.findViewById(R.id.thumbnail);

		String uri = CommonUtility.HOST_URL + "images/stationpics/thumbs/tn_"
				+ stationModel.getUnitId() + "_N.JPG";
		imageLoader.DisplayImage(uri, stationImg);

		ImageView favourite = (ImageView) view
				.findViewById(R.id.addToFavourites);
		if (stationModel.isFavourite()) {
			setFavouriteImage(favourite, R.drawable.star);
		} else {
			setFavouriteImage(favourite, R.drawable.star_disabled);
		}
		setFavouriteClickListener(favourite, stationModel);

		TextView name = (TextView) view.findViewById(R.id.name);
		TextView city = (TextView) view.findViewById(R.id.stationCity);
		TextView state = (TextView) view.findViewById(R.id.stationState);
		TextView county = (TextView) view.findViewById(R.id.countye);
		TextView installationDate = (TextView) view
				.findViewById(R.id.installationDate);

		name.setText(stationModel.getName());
		if (stationModel.getCity() != null && !stationModel.getCity().isEmpty()
				&& !stationModel.getCity().equals("null")) {
			city.setText(stationModel.getCity() + ",");
		}
		state.setText(stationModel.getState());
		county.setText(stationModel.getCounty() + ",");
		installationDate.setText(stationModel.getInstallationDate());

		return view;
	}

	@Override
	public boolean isEmpty() {
		return stationList.isEmpty();
	}

	private void setFavouriteClickListener(final ImageView favourite,
			final StationModel stationModel) {
		favourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int favImage;
				final String url;
				// TODO take shared preferences object out for common use.
				SharedPreferences prefs = ctx.getSharedPreferences(
						"edu.wsu.weather.agweathernet", Context.MODE_PRIVATE);
				String username = prefs.getString("username", "");
				if (stationModel.isFavourite()) {
					favImage = R.drawable.star_disabled;
					url = "test/commonmanager.php?qname=rem_fav&uname="
							+ username + "&stationId="
							+ stationModel.getUnitId();
				} else {
					favImage = R.drawable.star;
					url = "test/commonmanager.php?qname=add_fav&uname="
							+ username + "&stationId="
							+ stationModel.getUnitId();
				}
				new AsyncTask<Void, Void, String>() {
					protected String doInBackground(Void... arg0) {
						try {
							String response = HttpRequestWrapper
									.getString(CommonUtility.HOST_URL + url);
							return response;
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return "";
					};

					protected void onPostExecute(String result) {
						setFavouriteImage(favourite, favImage);
					};
				}.execute();
			}
		});
	}

	private void setFavouriteImage(ImageView favourite, int id) {
		favourite.setImageDrawable(ctx.getResources().getDrawable(id));
	}
}
