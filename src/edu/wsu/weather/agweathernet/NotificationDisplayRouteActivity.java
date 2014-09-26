package edu.wsu.weather.agweathernet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationDisplayRouteActivity extends Activity {

	ImageView imageView;
	TextView notiTitle;
	TextView notiText;
	TextView openActivity;
	Intent intent;
	NotiDisplayModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_display_route);

		initializeProperties();

		initializeModel();

		initializeContent(model);

		setClickListeners();

		setTitle("Notification");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initializeModel() {
		model = new NotiDisplayModel();
		Bundle extras = getIntent().getExtras();
		model.message = extras.getString("message");
		model.objId = extras.getString("alert_id");
		model.title = extras.getString("title");
		model.type = extras.getString("type");
	}

	private void setClickListeners() {
		openActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(intent);
				finish();
			}
		});
	}

	private void initializeContent(NotiDisplayModel model) {

		notiTitle.setText(model.title);
		notiText.setText(model.message);

		switch (model.type) {
		case "alert":
			// TODO open single alert fragment.
			// int alertImageId = getResources().getIdentifier("alert192",
			// "drawable", "edu.wsu.weather.agweathernet");
			// imageView.setImageResource(alertImageId);
			// openActivity.setText("Open original alert");
			// intent = new Intent();
			// intent.putExtra("id", model.objId);
			// intent.setClass(NotificationDisplayRouteActivity.this,
			// SingleAlertActivity.class);
			break;
		default:
			break;
		}
	}

	private void initializeProperties() {
		imageView = (ImageView) findViewById(R.id.notiImage);
		notiTitle = (TextView) findViewById(R.id.notiTitle);
		notiText = (TextView) findViewById(R.id.notiText);
		openActivity = (TextView) findViewById(R.id.openActivity);

		styleFields();
	}

	private void styleFields() {

		openActivity.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Custom.ttf");

		notiText.setTypeface(tf);
		notiTitle.setTypeface(tf);
		openActivity.setTypeface(tf);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_display_route, menu);
		return true;
	}

	class NotiDisplayModel {
		String message;
		String title;
		String objId;
		String type;
	}
}
