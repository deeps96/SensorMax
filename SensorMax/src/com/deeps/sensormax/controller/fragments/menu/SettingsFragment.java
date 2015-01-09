package com.deeps.sensormax.controller.fragments.menu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.MyConfig;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class SettingsFragment extends SuperFragment {

	private MyConfig myConfig;

	public SettingsFragment() {
		super();
	}

	public SettingsFragment(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		title = dataHandlerActivity.getString(R.string.settings);
		myConfig = dataHandlerActivity.getMyConfig();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_settings, container, false);
		initUIComponents();
		return view;
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initInterruptMeasuringOnMinimzeCheckBox();
		initIsNetworkLocationAllowedCheckBox();
		initShowThinkGearCheckBox();
		initIsScreenRotationBlockedCheckBox();
		initKeepScreenOnWhileMeasuringCheckBox();
		initShowSummaryAtEndCheckBox();
		initMaxGraphViewShowTimeEditText();
		initAddressEditText();
		initDeviceIDEditText();
	}

	private void initDeviceIDEditText() {
		final EditText deviceIDEditText = (EditText) view
				.findViewById(R.id.deviceIDEditText);
		if (myConfig.getDeviceID() != null
				&& myConfig.getDeviceID().length() > 0) {
			deviceIDEditText.setText(myConfig.getDeviceID());
		}
		deviceIDEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				myConfig.setDeviceID(deviceIDEditText.getText().toString());
			}
		});
	}

	private void initAddressEditText() {
		final EditText addressEditText = (EditText) view
				.findViewById(R.id.addressEditText);
		if (myConfig.getLiveStreamURL() != null
				&& myConfig.getLiveStreamURL().length() > 0) {
			addressEditText.setText(myConfig.getLiveStreamURL());
		}
		addressEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				myConfig.setLiveStreamURL(addressEditText.getText().toString());
			}
		});
	}

	private void initMaxGraphViewShowTimeEditText() {
		final EditText maxGraphViewShowTimeEditText = (EditText) view
				.findViewById(R.id.maxGraphViewShowTimeEditText);
		maxGraphViewShowTimeEditText.setText(Integer.toString(myConfig
				.getMaxGraphViewShowTime()));
		maxGraphViewShowTimeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (maxGraphViewShowTimeEditText.getText().toString().length() > 0) {
					try {
						myConfig.setMaxGraphViewShowTime(Integer
								.parseInt(maxGraphViewShowTimeEditText
										.getText().toString()));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	private void initInterruptMeasuringOnMinimzeCheckBox() {
		CheckBox interruptMeasuringOnMinimizeCheckBox = (CheckBox) view
				.findViewById(R.id.interruptMeasuringOnMinimizeCheckBox);
		interruptMeasuringOnMinimizeCheckBox.setChecked(myConfig
				.isInterruptMeasuringOnMinimize());
		interruptMeasuringOnMinimizeCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setInterruptMeasuringOnMinimize(isChecked);
					}
				});
	}

	private void initIsNetworkLocationAllowedCheckBox() {
		CheckBox isNetworkLocationAllowedCheckBox = (CheckBox) view
				.findViewById(R.id.isNetworkLocatingAllowedCheckBox);
		isNetworkLocationAllowedCheckBox.setChecked(myConfig
				.isNetworkLocatingAllowed());
		isNetworkLocationAllowedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setNetworkLocatingAllowed(isChecked);
					}
				});
	}

	private void initShowThinkGearCheckBox() {
		CheckBox showThinkGearCheckBox = (CheckBox) view
				.findViewById(R.id.showThinkGearCheckBox);
		showThinkGearCheckBox.setChecked(myConfig.isShowThinkGear());
		showThinkGearCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setShowThinkGear(isChecked);
					}
				});
	}

	private void initIsScreenRotationBlockedCheckBox() {
		CheckBox isScreenRotationBlockedCheckBox = (CheckBox) view
				.findViewById(R.id.isScreenRotationBlockedCheckBox);
		isScreenRotationBlockedCheckBox.setChecked(myConfig
				.isScreenRotationBlocked());
		isScreenRotationBlockedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setScreenRotationBlocked(isChecked);
					}
				});
	}

	private void initKeepScreenOnWhileMeasuringCheckBox() {
		CheckBox keepScreenOnWhileMeasuringCheckBox = (CheckBox) view
				.findViewById(R.id.keepScreenONWhileMeasuringCheckBox);
		keepScreenOnWhileMeasuringCheckBox.setChecked(myConfig
				.isKeepScreenONWhileMeasuring());
		keepScreenOnWhileMeasuringCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setKeepScreenONWhileMeasuring(isChecked);
					}
				});
	}

	private void initShowSummaryAtEndCheckBox() {
		CheckBox showSummaryAtEndCheckBox = (CheckBox) view
				.findViewById(R.id.showSummaryAtEndCheckBox);
		showSummaryAtEndCheckBox.setChecked(myConfig.isShowSummaryAtEnd());
		showSummaryAtEndCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setShowSummaryAtEnd(isChecked);
					}
				});
	}

}
