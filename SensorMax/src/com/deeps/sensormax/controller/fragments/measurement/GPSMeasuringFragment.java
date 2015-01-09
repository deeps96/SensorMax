package com.deeps.sensormax.controller.fragments.measurement;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.GPSMeasurement;
import com.deeps.sensormax.view.SatelliteCounterView;

/**
 * @author Deeps
 */

public class GPSMeasuringFragment extends LocalMeasurementFragment {

	private final String TAG = getClass().getName();

	private String latitudeStandardText, longitudeStandardText,
			speedStandardText;
	private TextView latitudeTextView, longitudeTextView, speedTextView;

	private GPSMeasurement gpsMeasurement;
	private SatelliteCounterView satelliteCounterView;

	public GPSMeasuringFragment() {
		super();
	}

	public GPSMeasuringFragment(DataHandlerActivity dataHandlerActivity,
			GPSMeasurement gpsMeasurement) {
		super(dataHandlerActivity, gpsMeasurement);
		this.gpsMeasurement = gpsMeasurement;
		this.title = dataHandlerActivity.getString(R.string.gps_tracking);
		isFullscreenModeEnabled = false;
		latitudeStandardText = dataHandlerActivity.getString(R.string.latitude)
				+ ":\n";
		longitudeStandardText = dataHandlerActivity
				.getString(R.string.longitude) + ":\n";
		speedStandardText = dataHandlerActivity.getString(R.string.speed)
				+ ":\n";
	}

	@Override
	public void update(final float[] data, final long time) {
		super.update(data, time);
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateExtraInformationTextViews(data, time);
				updateSatelliteCounterView(data);
				uiTasks--;
			}

		});
	}

	private void updateSatelliteCounterView(float[] data) {
		satelliteCounterView
				.updateData((int) data[GPSMeasurement.SATELLITES_COUNT_INDEX]);
	}

	private void updateExtraInformationTextViews(float[] data, long time) {
		latitudeTextView.setText(latitudeStandardText
				+ data[GPSMeasurement.LATITUDE_INDEX]);
		longitudeTextView.setText(longitudeStandardText
				+ data[GPSMeasurement.LONGITUDE_INDEX]);
		speedTextView.setText(speedStandardText
				+ data[GPSMeasurement.SPEED_INDEX]);
	}

	@Override
	protected void initMeasuredValuesTextViews() {
		latitudeTextView = (TextView) view.findViewById(R.id.liveStreamTextView);
		longitudeTextView = (TextView) view.findViewById(R.id.addressTextView);
		speedTextView = (TextView) view.findViewById(R.id.textView3);
		updateExtraInformationTextViews(new float[3], 0L);
	}

	@Override
	protected void initCustomView() {
		satelliteCounterView = new SatelliteCounterView(dataHandlerActivity);
		customView = satelliteCounterView;
		LinearLayout graphLayout = (LinearLayout) view
				.findViewById(R.id.graphLayout);
		graphLayout.removeAllViews();
		graphLayout.addView(satelliteCounterView);
	}

	@Override
	protected void resetView() {
		satelliteCounterView.reset();
	}

	@Override
	protected void initCheckBoxGroup() {
		LinearLayout checkBoxLayout = (LinearLayout) view
				.findViewById(R.id.checkBoxLinearLayout);
		checkBoxLayout.setVisibility(View.GONE);
	}

	@Override
	protected void initTriggerEditTexts() {
		final EditText triggerEditText1 = (EditText) view
				.findViewById(R.id.triggerEditText1);
		final EditText triggerEditText2 = (EditText) view
				.findViewById(R.id.triggerEditText2);
		final EditText triggerEditText3 = (EditText) view
				.findViewById(R.id.triggerEditText3);

		triggerEditText1.setHint(dataHandlerActivity
				.getString(R.string.latitude));
		triggerEditText1.addTextChangedListener(new TextWatcher() {

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
				if (triggerEditText1.getText().toString().length() > 0) {
					try {
						gpsMeasurement.setTriggerValue(
							GPSMeasurement.LATITUDE_INDEX,
							Integer.parseInt(triggerEditText1.getText()
									.toString()));
					} catch (NumberFormatException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
		});

		triggerEditText2.setHint(dataHandlerActivity
				.getString(R.string.longitude));
		triggerEditText2.addTextChangedListener(new TextWatcher() {

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
				if (triggerEditText2.getText().toString().length() > 0) {
					try {
						gpsMeasurement.setTriggerValue(
							GPSMeasurement.LONGITUDE_INDEX,
							Integer.parseInt(triggerEditText2.getText()
									.toString()));
					} catch (NumberFormatException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
		});
		triggerEditText3.setHint(dataHandlerActivity.getString(R.string.speed));
		triggerEditText3.addTextChangedListener(new TextWatcher() {

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
				try {
					if (triggerEditText3.getText().toString().length() > 0) {
						gpsMeasurement.setTriggerValue(
							GPSMeasurement.SPEED_INDEX,
							Integer.parseInt(triggerEditText3.getText()
									.toString()));
					}
				} catch (NumberFormatException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		});
	}

}
