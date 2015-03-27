package com.deeps.sensormax.controller.fragments.menu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.MyConfig;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class LivestreamSettingsFragment extends SuperFragment {

	private MyConfig myConfig;

	public LivestreamSettingsFragment() {
		super();
	}

	public LivestreamSettingsFragment(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		title = dataHandlerActivity.getString(R.string.livestream_settings);
		myConfig = dataHandlerActivity.getMyConfig();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
			R.layout.fragment_livestream_settings,
			container,
			false);
		initUIComponents();
		return view;
	}

	@Override
	public boolean onBackPressed() {
		dataHandlerActivity.getGuiManager().changeContentFragment(
			dataHandlerActivity.getMyFragmentManager().getSettingsFragment(),
			true);
		return true;
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initDeviceIDTextView();
		initServerAddressEditText();
		initLinkToAccountButton();
	}

	private void initLinkToAccountButton() {
		Button linkToAccountButton = (Button) view
				.findViewById(R.id.linkDeviceButton);
		final EditText accountNameEditText = (EditText) view
				.findViewById(R.id.accountNameEditText);
		final EditText accountPasswordEditText = (EditText) view
				.findViewById(R.id.accountPasswordEditText);
		linkToAccountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataHandlerActivity.getLiveStreamManager()
						.connectDeviceToAccount(
							accountNameEditText.getText().toString(),
							accountPasswordEditText.getText().toString());
			}
		});
	}

	private void initServerAddressEditText() {
		EditText serverAddressEditText = (EditText) view
				.findViewById(R.id.serverEditText);
		serverAddressEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				myConfig.setServerAddress(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		serverAddressEditText.setText(myConfig.getServerAddress());
	}

	private void initDeviceIDTextView() {
		TextView deviceIDTextView = (TextView) view
				.findViewById(R.id.deviceIDTextView);
		deviceIDTextView.setText(dataHandlerActivity
				.getString(R.string.device_id)
				+ ":\r\n"
				+ myConfig.getDeviceHash());
	}

}
