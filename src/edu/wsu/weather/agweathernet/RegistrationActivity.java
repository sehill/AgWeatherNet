package edu.wsu.weather.agweathernet;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends ActionBarActivity {
	Button regButton;
	EditText fullName;
	EditText userName;
	EditText email;
	EditText confirmEmail;
	EditText password;
	EditText confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		setTitle("Registration");

		initializeFields();

		setEventListeners();
	}

	private void setEventListeners() {
		regButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void initializeFields() {
		regButton = (Button) findViewById(R.id.regButton);
		fullName = (EditText) findViewById(R.id.regFullName);
		userName = (EditText) findViewById(R.id.regUserName);
		email = (EditText) findViewById(R.id.regEmail);
		confirmEmail = (EditText) findViewById(R.id.regConfirmEmail);
		password = (EditText) findViewById(R.id.regPassword);
		confirmPassword = (EditText) findViewById(R.id.regConfirmPassword);
	}
}
