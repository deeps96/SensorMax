package com.deeps.sensormax.model.measurement;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;

import com.deeps.sensormax.controller.fragments.measurement.LocalMeasurementFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.interfaces.OnStatusChangedListener;

/**
 * @author Deeps
 */

public abstract class Measurement implements Runnable {

	protected final String TAG = getClass().getName();
	public static final int STATE_STOPPED = 0, STATE_RUNNING = 1,
			STATE_PAUSED = 2, STATE_INTERRUPTED = 3;

	protected boolean isTriggerActive = false, isMeasuringTimeActive = false,
			isTriggerReleased, isTriggerReleasedFirstTime, isLiveStreamEnabled;
	protected boolean[] highlightedMeasuringValues;
	protected int dataCounter, maxDataCounter, measuringIntervalInMS,
			measuringTimeInMS, state = 0, groupID;
	protected int[] time;
	protected float[] recentDataSet, triggerValues, zeroing, average, min, max;
	protected float[][] data;
	protected SensorManager sensorManager;
	protected ArrayList<OnStatusChangedListener> onStatusChangedListener;

	protected DataHandlerActivity dataHandlerActivity;
	protected LocalMeasurementFragment localMeasurementFragment;

	public Measurement(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		this.sensorManager = (SensorManager) dataHandlerActivity
				.getSystemService(Context.SENSOR_SERVICE);
		// initialise() need to be called from the children class!
	}

	public void initialise() {
		onStatusChangedListener = new ArrayList<>();
		maxDataCounter = dataHandlerActivity.getMyConfig()
				.getBufferSizeLocalMeasuring();
		measuringIntervalInMS = dataHandlerActivity.getMyConfig()
				.getDefaultMeasuringInterval();
		triggerValues = new float[getAxisCount()];
		for (int i = 0; i < triggerValues.length; i++)
			triggerValues[i] = -Float.MAX_VALUE;
		recentDataSet = new float[getAxisCount()];
		zeroing = new float[getAxisCount()];
		min = new float[getAxisCount()];
		max = new float[getAxisCount()];
	}

	@Override
	public void run() {
		isTriggerReleasedFirstTime = true;
		dataCounter = 0;
		data = new float[maxDataCounter][];
		highlightedMeasuringValues = new boolean[maxDataCounter];
		time = new int[maxDataCounter];
		min = new float[getAxisCount()];
		max = new float[getAxisCount()];
		for (int i = 0; i < min.length; i++) {
			min[i] = Float.MAX_VALUE;
		}
		for (int i = 0; i < max.length; i++) {
			max[i] = -Float.MAX_VALUE;
		}
		isTriggerReleased = false;
		registerListener();
		while (state != STATE_STOPPED && !isTimeLimitExceeded()) {
			if (isMemoryExceeded()) {
				clearLocalBuffer();
			}
			if (state == STATE_RUNNING) {
				float[] modifiedData = recentDataSet.clone();
				for (int i = 0; i < modifiedData.length; i++) {
					modifiedData[i] -= zeroing[i];
				}
				if (isTriggerReleased(modifiedData)) {
					if (isTriggerReleasedFirstTime) {
						updateGroupMemberState(state);
						isTriggerReleasedFirstTime = false;
					}
					calculateMax(modifiedData);
					calculateMin(modifiedData);
					data[dataCounter] = modifiedData;
					if (dataCounter > 0) {
						time[dataCounter] = time[dataCounter - 1]
								+ measuringIntervalInMS;
					}
					updateUIValues(modifiedData, time[dataCounter]);
					if (isLiveStreamEnabled) {
						sendDataToLiveStream(
							modifiedData,
							System.currentTimeMillis(),
							highlightedMeasuringValues[dataCounter]);
					}
					dataCounter++;
				}
			}
			try {
				Thread.sleep(measuringIntervalInMS);
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	protected void sendDataToLiveStream(float[] data, long time,
			boolean isHighlighted) {
		dataHandlerActivity.getLiveStreamManager().sendRealTimeData(
			localMeasurementFragment.getMeasurement().getTitle(),
			data,
			time,
			isHighlighted);
	}

	protected void calculateMin(float[] modifiedData) {
		for (int i = 0; i < modifiedData.length; i++) {
			min[i] = Math.min(min[i], modifiedData[i]);
		}
	}

	protected void calculateMax(float[] modifiedData) {
		for (int i = 0; i < modifiedData.length; i++) {
			max[i] = Math.max(max[i], modifiedData[i]);
		}
	}

	public void calculateAverage() {
		average = new float[getAxisCount()];
		for (int i = 0; i < dataCounter; i++) {
			for (int j = 0; j < average.length; j++) {
				average[j] += data[i][j];
			}
		}
		for (int i = 0; i < average.length; i++) {
			average[i] /= dataCounter;
		}
	}

	protected boolean isTriggerReleased(float[] modifiedData) {
		if (!isTriggerActive)
			return true;
		if (!isTriggerReleased) {
			boolean release = true;
			for (int i = 0; i < modifiedData.length; i++) {
				if (modifiedData[i] < triggerValues[i]) {
					release = false;
					break;
				}
			}
			isTriggerReleased = release;
		}
		return isTriggerReleased;
	}

	protected boolean isTimeLimitExceeded() {
		if (isMeasuringTimeActive
				&& (dataCounter > 0 && time[dataCounter - 1] >= measuringTimeInMS)) {
			stopMeasuring(true);
			return true;
		}
		return false;
	}

	protected boolean isMemoryExceeded() {
		return dataCounter > maxDataCounter;
	}

	public void resumeMeasuring(boolean updateGroupState) {
		setState(STATE_RUNNING, updateGroupState);
		registerListener();
	}

	public void pauseMeasuring(boolean updateGroupState) {
		setState(STATE_PAUSED, updateGroupState);
		unregisterListener();
	}

	public void interruptMeasuring(boolean updateGroupState) {
		setState(STATE_INTERRUPTED, updateGroupState);
		if (dataHandlerActivity.getMyConfig().isInterruptMeasuringOnMinimize())
			unregisterListener();
	}

	public void startNewMeasuring() {
		setState(STATE_RUNNING, false);
		if (dataHandlerActivity.getMyConfig().isKeepScreenONWhileMeasuring()) {
			dataHandlerActivity.runOnUiThread(new Runnable() {// if started by
															  // trigger
						@Override
						public void run() {
							dataHandlerActivity.getWindow().addFlags(
								WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						}
					});

		}
		new Thread(this).start();
	}

	public void stopMeasuring(boolean updateGroupState) {
		stopMeasuring(updateGroupState, true);
	}

	public void stopMeasuring(boolean updateGroupState, boolean updateUI) {
		unregisterListener();
		dataHandlerActivity.runOnUiThread(new Runnable() { // if stopped by
														   // itsself (time)
					@Override
					public void run() {
						dataHandlerActivity.getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // yeah
																			 // without
																			 // checking
																			 // myconfig
					}
				});

		setState(STATE_STOPPED, updateGroupState);
		if (updateUI) {
			localMeasurementFragment.onMeasuringStopped();
		}
	}

	public void zeroing() {
		for (int i = 0; i < zeroing.length; i++)
			zeroing[i] += recentDataSet[i] - zeroing[i];
	}

	public void resetZeroing() {
		zeroing = new float[zeroing.length];
	}

	public void highlightDataSet() {
		if (state != STATE_STOPPED) {
			highlightedMeasuringValues[dataCounter] = true;
		}
	}

	protected void clearLocalBuffer() {
		// TODO this method can be overwritten, to save the data - array into a
		// file
		data = new float[maxDataCounter][];
		int lastTime = time[maxDataCounter];
		time = new int[maxDataCounter];
		time[0] = lastTime;
		dataCounter = 0;
	}

	private void updateGroupMemberState(final int newState) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (groupID != 0 && newState != STATE_INTERRUPTED) { // 0 ... no
																	 // group
					// selected
					for (Measurement m : dataHandlerActivity.getModelManager()
							.getMeasurementManager().getMeasurements()) {
						if (m.getGroupID() == groupID
								&& m.getState() != newState) {
							switch (newState) {
								case STATE_PAUSED:
									m.pauseMeasuring(false);
									break;
								case STATE_RUNNING:
									m.startNewMeasuring();
									break;
								case STATE_STOPPED:
									m.stopMeasuring(false);
									break;
							}
						}
					}
				}
			}
		}).start();

	}

	public void saveDataUsingDialog() {
		dataHandlerActivity
				.getFileManager()
				.getFileController()
				.showSaveMeasurementDialog(
					localMeasurementFragment.getTitle(),
					getCSV());
	}

	// Abstract methods

	protected abstract void updateUIValues(float[] modifiedData, int time);

	public abstract String getCSV();

	protected abstract int getAxisCount();

	protected abstract void registerListener();

	protected abstract void unregisterListener();

	// Getter & Setter
	protected void setState(int state, boolean updateGroupMembers) {
		int oldState = this.state;
		this.state = state;
		if (updateGroupMembers) {
			updateGroupMemberState(state);
		}
		for (OnStatusChangedListener listener : onStatusChangedListener) {
			listener.onStatusChanged(oldState);
		}
	}

	public void registerOnStatusChangedListener(OnStatusChangedListener listener) {
		onStatusChangedListener.add(listener);
	}

	public void unregisterOnStatusChangedListener(
			OnStatusChangedListener listener) {
		onStatusChangedListener.remove(listener);
	}

	public int getState() {
		return state;
	}

	public float[][] getData() {
		return data;
	}

	public int[] getTime() {
		return time;
	}

	public float[] getAverage() {
		return average;
	}

	public float[] getRecentDataSet() {
		return recentDataSet;
	}

	public void setMeasuringIntervalInMS(int measuringIntervalInMS) {
		this.measuringIntervalInMS = measuringIntervalInMS;
	}

	public void setMeasuringTimeInMS(int measuringTimeInMS) {
		this.measuringTimeInMS = measuringTimeInMS;
	}

	public void setTriggerValue(int index, float triggerValue) {
		triggerValues[index] = triggerValue;
	}

	public void setMeasuringTimeActive(boolean isMeasuringTimeActive) {
		this.isMeasuringTimeActive = isMeasuringTimeActive;
	}

	public void setTriggerActive(boolean isTriggerActive) {
		this.isTriggerActive = isTriggerActive;
	}

	public int getMaxDataCounter() {
		return maxDataCounter;
	}

	public int getDataCounter() {
		return dataCounter;
	}

	public float[] getMin() {
		return min;
	}

	public float[] getMax() {
		return max;
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public boolean isLiveStreamEnabled() {
		return isLiveStreamEnabled;
	}

	public void setLiveStreamEnabled(boolean isLiveStreamEnabled) {
		this.isLiveStreamEnabled = isLiveStreamEnabled;
	}

	public String getTitle() {
		return localMeasurementFragment.getTitle();
	}

}
