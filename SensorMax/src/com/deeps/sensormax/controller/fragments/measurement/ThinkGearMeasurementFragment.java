package com.deeps.sensormax.controller.fragments.measurement;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.ThinkGearMeasurement;
import com.deeps.sensormax.view.TableView;

/**
 * @author Deeps
 */

public class ThinkGearMeasurementFragment extends LocalMeasurementFragment {

	private final String TAG = getClass().getName();

	private TableView tableView;
	private ThinkGearMeasurement thinkGearMeasurement;

	public ThinkGearMeasurementFragment() {
		super();
	}

	public ThinkGearMeasurementFragment(DataHandlerActivity dataHandlerActivity,
			ThinkGearMeasurement thinkGearMeasurement) {
		super(dataHandlerActivity, thinkGearMeasurement);
		this.thinkGearMeasurement = thinkGearMeasurement;
		this.title = dataHandlerActivity.getString(R.string.think_gear);
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
	}

	@Override
	public void update(final float[] data, final long time) {
		super.update(data, time);
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tableView.updateData(data, time);
				uiTasks--;
			}
		});
	}

	private void disableInfoBox() {
		LinearLayout infoBoxLinearLayout = (LinearLayout) view
				.findViewById(R.id.infoBox);
		infoBoxLinearLayout.setVisibility(View.GONE);
	}

	@Override
	protected void resetView() {
		tableView.reset();
	}

	@Override
	protected void initMeasuredValuesTextViews() { // are invisible (/gone)
		disableInfoBox();
	}

	@Override
	protected void initCustomView() {
		tableView = new TableView(dataHandlerActivity);
		customView = tableView;
		tableView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		String[] header = new String[ThinkGearMeasurement.CHANNEL_COUNT
				+ ThinkGearMeasurement.EXTRA_INFORMATION_COUNT];
		String channelShort = dataHandlerActivity
				.getString(R.string.channel_short);
		for (int i = 0; i < ThinkGearMeasurement.CHANNEL_COUNT; i++) {
			header[i] = channelShort + Integer.toString(i + 1);
		}
		header[ThinkGearMeasurement.HEART_RATE_INDEX] = dataHandlerActivity
				.getString(R.string.heart_rate);
		tableView.setHeader(header);
		resetView();

		LinearLayout graphLayout = (LinearLayout) view
				.findViewById(R.id.graphLayout);
		graphLayout.removeAllViews();
		graphLayout.addView(tableView);
	}

	@Override
	protected void initCheckBoxGroup() { // are invisible
	}

	@Override
	protected void initTriggerEditTexts() {
		LinearLayout additionalTriggerLayout = (LinearLayout) view
				.findViewById(R.id.additionalTriggerLayout);
		additionalTriggerLayout.setVisibility(View.VISIBLE);

		final EditText[] triggerEditTexts = new EditText[ThinkGearMeasurement.CHANNEL_COUNT
				+ ThinkGearMeasurement.EXTRA_INFORMATION_COUNT];
		triggerEditTexts[0] = (EditText) view
				.findViewById(R.id.triggerEditText1);
		triggerEditTexts[1] = (EditText) view
				.findViewById(R.id.triggerEditText2);
		triggerEditTexts[2] = (EditText) view
				.findViewById(R.id.triggerEditText3);
		triggerEditTexts[3] = (EditText) view
				.findViewById(R.id.triggerEditText4);
		triggerEditTexts[4] = (EditText) view
				.findViewById(R.id.triggerEditText5);
		triggerEditTexts[5] = (EditText) view
				.findViewById(R.id.triggerEditText6);
		triggerEditTexts[6] = (EditText) view
				.findViewById(R.id.triggerEditText7);
		triggerEditTexts[7] = (EditText) view
				.findViewById(R.id.triggerEditText8);
		triggerEditTexts[8] = (EditText) view
				.findViewById(R.id.triggerEditText9);

		String channelShort = dataHandlerActivity
				.getString(R.string.channel_short);
		for (int iChannelID = 0; iChannelID < ThinkGearMeasurement.CHANNEL_COUNT; iChannelID++) {
			final int currentID = iChannelID;
			triggerEditTexts[currentID].setHint(channelShort
					+ Integer.toString(currentID + 1));
			triggerEditTexts[currentID]
					.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
						}

						@Override
						public void afterTextChanged(Editable s) {
							try {
								if (triggerEditTexts[currentID].getText()
										.toString().length() > 0) {
									thinkGearMeasurement.setTriggerValue(
										currentID,
										Integer.parseInt(triggerEditTexts[currentID]
												.getText().toString()));
								}
							} catch (NumberFormatException e) {
								Log.e(TAG, e.getMessage());
							}
						}
					});
		}
		triggerEditTexts[ThinkGearMeasurement.HEART_RATE_INDEX]
				.setHint(dataHandlerActivity.getString(R.string.heart_rate));
		triggerEditTexts[ThinkGearMeasurement.HEART_RATE_INDEX]
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						try {
							if (triggerEditTexts[ThinkGearMeasurement.HEART_RATE_INDEX]
									.getText().toString().length() > 0) {
								thinkGearMeasurement
										.setTriggerValue(
											ThinkGearMeasurement.HEART_RATE_INDEX,
											Integer.parseInt(triggerEditTexts[ThinkGearMeasurement.HEART_RATE_INDEX]
													.getText().toString()));
							}
						} catch (NumberFormatException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				});

	}

}
