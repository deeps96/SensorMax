package com.deeps.sensormax.controller.fragments.menu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
		initLivestreamSettingsButton();
		initRecordWholeAudioSpectrumCheckBox();
	}

	private void initRecordWholeAudioSpectrumCheckBox() {
		CheckBox recordWholeAudioSpectrumCheckBox = (CheckBox) view
				.findViewById(R.id.recordWholeAudioSpectrumCheckBox);
		recordWholeAudioSpectrumCheckBox.setChecked(myConfig
				.isRecordWholeAudioSpectrum());
		recordWholeAudioSpectrumCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myConfig.setRecordWholeAudioSpectrum(isChecked);
					}
				});
	}

	private void initLivestreamSettingsButton() {
		Button livestreamButton = (Button) view
				.findViewById(R.id.livestreamButton);
		livestreamButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataHandlerActivity.getGuiManager().changeContentFragment(
					dataHandlerActivity.getMyFragmentManager()
							.getLivestreamSettingsFragment(),
					false);
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
		if (myConfig.isBluetoothAvailable()) {
			showThinkGearCheckBox.setChecked(myConfig.isShowThinkGear());
			showThinkGearCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							myConfig.setShowThinkGear(isChecked);
						}
					});
		} else {
			showThinkGearCheckBox.setEnabled(false);
		}
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
