package com.deeps.sensormax.controller.fragments.measurement;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.AudioMeasurement;
import com.deeps.sensormax.view.AudioSpectrumView;

/**
 * @author Deeps
 */

public class AudioMeasurementFragment extends LocalMeasurementFragment {

	private final String TAG = getClass().getName();

	private String frequencyStandardText, dbStandardText, timeStandardText;
	private TextView timeTextView, dbTextView, frequencyTextView;

	private AudioMeasurement audioMeasurement;
	private AudioSpectrumView audioSpectrumView;

	public AudioMeasurementFragment() {
		super();
	}

	public AudioMeasurementFragment(DataHandlerActivity dataHandlerActivity,
			AudioMeasurement audioMeasurement) {
		super(dataHandlerActivity, audioMeasurement);
		this.audioMeasurement = audioMeasurement;
		this.title = dataHandlerActivity.getString(R.string.audio_analysis);
		frequencyStandardText = dataHandlerActivity
				.getString(R.string.frequency) + ":\n";
		dbStandardText = dataHandlerActivity.getString(R.string.decible)
				+ ":\n";
		timeStandardText = dataHandlerActivity.getString(R.string.time_in_ms)
				+ ":\n";
	}

	@Override
	public void update(final float[] data, final long time) {
		super.update(data, time);
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateExtraInformationTextViews(data, time);
				updateAudioSpectrumView(data);
				uiTasks--;
			}
		});
	}

	private void updateAudioSpectrumView(float[] data) {
		audioSpectrumView.updateData(0, AudioMeasurement.BLOCK_SIZE, data);
	}

	private void updateExtraInformationTextViews(float[] data, long time) {
		timeTextView.setText(timeStandardText + time);
		frequencyTextView.setText(frequencyStandardText
				+ data[AudioMeasurement.BLOCK_SIZE
						+ AudioMeasurement.FREQUENCY_INDEX]);
		dbTextView.setText(dbStandardText
				+ data[AudioMeasurement.BLOCK_SIZE
						+ AudioMeasurement.DECIBLE_INDEX]);
	}

	@Override
	public void resetView() {
		audioSpectrumView.reset();
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		disableLiveStreamButton();
	}

	private void disableLiveStreamButton() {
		ToggleButton enableLiveStreamButton = (ToggleButton) view
				.findViewById(R.id.enableLiveStreamToggleButton);
		enableLiveStreamButton.setEnabled(false);
	}

	@Override
	protected void initMeasuredValuesTextViews() {
		timeTextView = (TextView) view.findViewById(R.id.liveStreamTextView);
		dbTextView = (TextView) view.findViewById(R.id.addressTextView);
		frequencyTextView = (TextView) view.findViewById(R.id.textView3);
		updateExtraInformationTextViews(new float[AudioMeasurement.BLOCK_SIZE
				+ AudioMeasurement.EXTRA_INFORMATION_COUNT], 0L);
	}

	@Override
	protected void initCustomView() {
		audioSpectrumView = new AudioSpectrumView(dataHandlerActivity,
				AudioMeasurement.BLOCK_SIZE);
		customView = audioSpectrumView;
		audioSpectrumView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LinearLayout graphLayout = (LinearLayout) view
				.findViewById(R.id.graphLayout);
		graphLayout.removeAllViews();
		graphLayout.addView(audioSpectrumView);
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

		triggerEditText1.setVisibility(View.GONE);

		triggerEditText2.setHint(dataHandlerActivity
				.getString(R.string.frequency));
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
				try {
					if (triggerEditText2.getText().toString().length() > 0) {
						audioMeasurement.setTriggerValue(
							AudioMeasurement.BLOCK_SIZE
									+ AudioMeasurement.FREQUENCY_INDEX,
							Integer.parseInt(triggerEditText2.getText()
									.toString()));
					}
				} catch (NumberFormatException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		});
		triggerEditText3.setHint(dataHandlerActivity
				.getString(R.string.decible));
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
						audioMeasurement.setTriggerValue(
							AudioMeasurement.BLOCK_SIZE
									+ AudioMeasurement.DECIBLE_INDEX,
							Integer.parseInt(triggerEditText3.getText()
									.toString()));
					}
				} catch (NumberFormatException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		});

	}

	public AudioSpectrumView getAudioSpectrumView() {
		return audioSpectrumView;
	}

}
