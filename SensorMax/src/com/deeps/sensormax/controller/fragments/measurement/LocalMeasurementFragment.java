package com.deeps.sensormax.controller.fragments.measurement;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.MeasurementInfoFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.interfaces.OnStatusChangedListener;
import com.deeps.sensormax.model.interfaces.UICodeWrapper;
import com.deeps.sensormax.model.measurement.Measurement;

/**
 * @author Deeps
 */

public abstract class LocalMeasurementFragment extends SuperFragment implements
		OnStatusChangedListener {

	protected boolean isGraphInFullscreenMode, isFullscreenModeEnabled,
			isLiveStreamButtonActionBlocked;
	protected GestureDetector gestureDetector;
	private ScrollView interfaceLayout;
	protected View fullscreenView, customView;

	protected Measurement measurement;
	protected MeasurementInfoFragment measurementInfoFragment;

	public LocalMeasurementFragment() {
		super();
	}

	public LocalMeasurementFragment(DataHandlerActivity dataHandlerActivity,
			Measurement measurement) {
		super(dataHandlerActivity);
		this.measurement = measurement;
		isFullscreenModeEnabled = true;
		measurement.registerOnStatusChangedListener(this);
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initInterfaceLayout();
		initMeasuredValuesTextViews();
		initCustomView();
		initGestureListener();
		initCheckBoxGroup();
		initStartButton();
		initStopButton();
		initSaveDataButton();
		initZeroingButton();
		initHighlightButton();
		initMeasuringIntervalEditText();
		initMeasuringTimeEditText();
		initMeasuringTimeToggle();
		initTriggerToggle();
		initTriggerEditTexts();
		initEnableLiveStreamButton();
	}

	@Override
	public void onStop() {
		((ToggleButton) view.findViewById(R.id.enableLiveStreamToggleButton))
				.setChecked(false);
		super.onStop();
	}

	private void initEnableLiveStreamButton() {
		ToggleButton enableLiveStreamButton = (ToggleButton) view
				.findViewById(R.id.enableLiveStreamToggleButton);
		enableLiveStreamButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						measurement.setLiveStreamEnabled(isChecked);
						if (!isLiveStreamButtonActionBlocked) {
							ArrayList<LocalMeasurementFragment> groupMembersFragments = dataHandlerActivity
									.getMyFragmentManager()
									.getGroupMeasurementFragment()
									.getGroupMembers();
							for (LocalMeasurementFragment fragment : groupMembersFragments) {
								if (fragment.getTitle() != title) {
									fragment.setLiveStreamButtonCheckedWithoutUpdate(isChecked);
								}
							}
							if (isChecked) {
								Measurement[] groupMembers = new Measurement[groupMembersFragments
										.size()];
								for (int iGroupMember = 0; iGroupMember < groupMembers.length; iGroupMember++) {
									groupMembers[iGroupMember] = groupMembersFragments
											.get(iGroupMember).getMeasurement();
								}
								dataHandlerActivity.getLiveStreamManager()
										.startNewSession(groupMembers);
							} else {
								dataHandlerActivity.getLiveStreamManager()
										.endCurrentSession();
							}
						} else {
							isLiveStreamButtonActionBlocked = false;
						}
					}
				});

		enableLiveStreamButton.setEnabled(dataHandlerActivity.getMyConfig()
				.isLiveStreamWellConfigurated());
	}

	public void setLiveStreamButtonCheckedWithoutUpdate(boolean isChecked) {
		isLiveStreamButtonActionBlocked = true;
		((ToggleButton) view.findViewById(R.id.enableLiveStreamToggleButton))
				.setChecked(isChecked);
	}

	private void initInterfaceLayout() {
		interfaceLayout = (ScrollView) view.findViewById(R.id.interfaceLayout);
	}

	@Override
	public boolean onBackPressed() {
		if (isGraphInFullscreenMode) {
			triggerFullscreenMode();
		} else {
			dataHandlerActivity.getGuiManager().changeContentFragment(
				dataHandlerActivity.getMyFragmentManager()
						.getSensorOverviewFragment(),
				true);
		}
		return true;
	}

	protected void initGestureListener() {
		gestureDetector = new GestureDetector(dataHandlerActivity,
				new GestureListener());
		customView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	private void initHighlightButton() {
		final Button highlightButton = (Button) view
				.findViewById(R.id.highlightButton);
		highlightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				measurement.highlightDataSet();
			}
		});
	}

	private void initZeroingButton() {
		final Button zeroingButton = (Button) view
				.findViewById(R.id.zeroingButton);
		zeroingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				measurement.zeroing();
				Toast
						.makeText(
							dataHandlerActivity,
							dataHandlerActivity
									.getString(R.string.info_press_long_to_reset_calibration),
							Toast.LENGTH_SHORT).show();
			}
		});
		zeroingButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				measurement.resetZeroing();
				return true;
			}
		});
	}

	private void initSaveDataButton() {
		final Button saveDataButton = (Button) view
				.findViewById(R.id.saveButton);
		saveDataButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (measurement.getState() == Measurement.STATE_STOPPED) {
					measurement.saveDataUsingDialog();
				} else {
					Toast
							.makeText(
								dataHandlerActivity,
								dataHandlerActivity
										.getString(R.string.request_stop_measuring_first),
								Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initMeasuringTimeToggle() {
		ToggleButton measuringTimeToggle = (ToggleButton) view
				.findViewById(R.id.measuringTimeToggleButton);
		measuringTimeToggle
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						measurement.setMeasuringTimeActive(isChecked);
					}
				});
	}

	private void initMeasuringTimeEditText() {
		final EditText measuringTimeEditText = (EditText) view
				.findViewById(R.id.measuringTimeEditText);
		measuringTimeEditText.addTextChangedListener(new TextWatcher() {

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
				if (measuringTimeEditText.getText().toString().length() > 0) {
					try {
						measurement.setMeasuringTimeInMS(Integer
								.parseInt(measuringTimeEditText.getText()
										.toString()));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	private void initMeasuringIntervalEditText() {
		final EditText measuringIntervalEditText = (EditText) view
				.findViewById(R.id.measuringIntervalEditText);
		measuringIntervalEditText.addTextChangedListener(new TextWatcher() {

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
				if (measuringIntervalEditText.getText().toString().length() > 0) {
					try {
						measurement.setMeasuringIntervalInMS(Integer
								.parseInt(measuringIntervalEditText.getText()
										.toString()));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	protected void initStopButton() {
		final Button stopButton = (Button) view.findViewById(R.id.stopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				measurement.stopMeasuring(true);
			}
		});
	}

	private void initTriggerToggle() {
		ToggleButton triggerToggle = (ToggleButton) view
				.findViewById(R.id.triggerToggleButton);
		triggerToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				measurement.setTriggerActive(isChecked);
			}
		});
	}

	protected void initStartButton() {
		final Button startPauseButton = (Button) view
				.findViewById(R.id.startPauseButton);
		startPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (measurement.getState()) {
					case Measurement.STATE_STOPPED:
						resetView();
						measurement.startNewMeasuring();
						break;
					case Measurement.STATE_PAUSED:
						measurement.resumeMeasuring(true);
						break;
					case Measurement.STATE_RUNNING:
						measurement.pauseMeasuring(true);
						break;
				}
				updateButtons();
			}
		});
	}

	private void updateButtons() {
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateStartButton();
				updateStopButton();
			}
		});

	}

	private void updateStopButton() {
		final Button stopButton = (Button) view.findViewById(R.id.stopButton);
		stopButton
				.setEnabled(measurement.getState() != Measurement.STATE_STOPPED);
	}

	protected void updateStartButton() {
		final Button startPauseButton = (Button) view
				.findViewById(R.id.startPauseButton);
		switch (measurement.getState()) {
			case Measurement.STATE_RUNNING:
				startPauseButton.setText(dataHandlerActivity
						.getString(R.string.pause));
				break;
			case Measurement.STATE_PAUSED:
				startPauseButton.setText(dataHandlerActivity
						.getString(R.string.continue_measuring));
				break;
			case Measurement.STATE_STOPPED:
				startPauseButton.setText(dataHandlerActivity
						.getString(R.string.start));
				break;
		}
	}

	@Override
	public void onStatusChanged(int oldState) {
		if (oldState == Measurement.STATE_STOPPED
				&& measurement.getState() == Measurement.STATE_RUNNING) {
			dataHandlerActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					resetView();
				}
			});
		}
		updateButtons();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.fragment_measurement, container, false);
		initUIComponents();
		return view;
	}

	public void triggerFullscreenMode() {
		if (!isFullscreenModeEnabled) {
			return;
		}
		isGraphInFullscreenMode = !isGraphInFullscreenMode;

		if (isGraphInFullscreenMode) {
			goFullscreen();
		} else {
			goNormal();
		}
	}

	protected void goFullscreen() {
		interfaceLayout.setVisibility(View.GONE);
	}

	protected void goNormal() {
		interfaceLayout.setVisibility(View.VISIBLE);
	}

	public void onMeasuringStopped() {
		waitTillUITasksAreDoneAndExecuteUICode(new UICodeWrapper() {
			@Override
			public void runUICode() {
				resetView();
			}
		});
	}

	protected abstract void resetView();

	protected abstract void initMeasuredValuesTextViews();

	protected abstract void initCustomView();

	protected abstract void initCheckBoxGroup();

	protected abstract void initTriggerEditTexts();

	// Getter & Setter
	public Measurement getMeasurement() {
		return measurement;
	}

	public MeasurementInfoFragment getMeasurementInfoFragment() {
		return measurementInfoFragment;
	}

	public void setMeasurementInfoFragment(
			MeasurementInfoFragment measurementInfoFragment) {
		this.measurementInfoFragment = measurementInfoFragment;
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			triggerFullscreenMode();
			return true;
		}

	}

}
