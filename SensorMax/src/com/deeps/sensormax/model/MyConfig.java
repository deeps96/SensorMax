package com.deeps.sensormax.model;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.SensorManager;
import android.util.Log;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.sensors.Accelerometer;
import com.deeps.sensormax.model.sensors.AmbientTemperature;
import com.deeps.sensormax.model.sensors.Gravity;
import com.deeps.sensormax.model.sensors.Gyroscope;
import com.deeps.sensormax.model.sensors.Light;
import com.deeps.sensormax.model.sensors.LinearAcceleration;
import com.deeps.sensormax.model.sensors.MagneticField;
import com.deeps.sensormax.model.sensors.MySensor;
import com.deeps.sensormax.model.sensors.Pressure;
import com.deeps.sensormax.model.sensors.Proximity;
import com.deeps.sensormax.model.sensors.RelativeHumidity;

/**
 * @author Deeps
 */

public class MyConfig {

	private final String TAG = getClass().getName();

	private boolean isFirstAppStart, interruptMeasuringOnMinimize,
			isNetworkLocatingAllowed, showThinkGear, isScreenRotationBlocked,
			keepScreenONWhileMeasuring, showSummaryAtEnd, isBluetoothAvailable,
			useAutoScaling = true, showWholeGraphAtEnd = true;
	private int maxGraphViewShowTime, bufferSizeLocalMeasuring = 180000,
			defaultMeasuringInterval = 20,
			minDistanceDifferenceForUpdateInMeter = 0, groupMax = 1;
	private Editor editor;
	private SharedPreferences settings;
	private String deviceID, liveStreamURL;
	private ArrayList<MySensor> availableSensors;

	private DataHandlerActivity dataHandlerActivity;
	private MySensor[] allSensors;

	public MyConfig(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		initValues();
	}

	private void initValues() {
		initAllSensors();
		initAppSettings();
		isFirstAppStart = settings.getBoolean("isFirstAppStart", true);
		if (isFirstAppStart) {
			onFirstAppStart();
		} else {
			onFurtherAppStart();
		}
		loadAllParams();
	}

	private void onFurtherAppStart() {
		loadAvailableSensors();
	}

	private void loadAllParams() {
		deviceID = settings.getString("deviceID", null);
		if (deviceID == null) { // happens if app got updated & no datawipe
			assignDeviceID();
			setDeviceID(deviceID);
		}
		interruptMeasuringOnMinimize = settings.getBoolean(
			"interruptMeasuringOnMinimize",
			true);
		isNetworkLocatingAllowed = settings.getBoolean(
			"isNetworkLocatingAllowed",
			true);
		showThinkGear = settings.getBoolean("showThinkGear", false);
		isScreenRotationBlocked = settings.getBoolean(
			"isScreenRotationBlocked",
			false);
		keepScreenONWhileMeasuring = settings.getBoolean(
			"keepScreenONWhileMeasuring",
			false);
		showSummaryAtEnd = settings.getBoolean("showSummaryAtEnd", true);
		maxGraphViewShowTime = settings.getInt("maxGraphViewShowTime", 4000);
		liveStreamURL = settings.getString("liveStreamURL", null);
		isBluetoothAvailable = settings.getBoolean(
			"isBluetoothAvailable",
			false);
		// apply settings
		dataHandlerActivity.blockScreenRotation(isScreenRotationBlocked);
	}

	private void onFirstAppStart() {
		editor.putBoolean("isFirstAppStart", false);
		assignDeviceID();
		editor.putString("deviceID", deviceID);
		performFullSensorCheck(); // editor gets commited here
	}

	private void initAppSettings() {
		settings = dataHandlerActivity.getSharedPreferences(
			dataHandlerActivity.getResources().getString(R.string.app_name),
			Context.MODE_PRIVATE);
		editor = settings.edit();
	}

	private void assignDeviceID() {
		deviceID = UUID.randomUUID().toString().substring(0, 10); // String has
																  // a length
																  // from 1 to
																  // 10 in this
																  // case (i
																  // think)
	}

	private void initAllSensors() {
		allSensors = new MySensor[] { new Accelerometer(dataHandlerActivity),
				new AmbientTemperature(dataHandlerActivity),
				new Gravity(dataHandlerActivity),
				new Gyroscope(dataHandlerActivity),
				new Light(dataHandlerActivity),
				new LinearAcceleration(dataHandlerActivity),
				new MagneticField(dataHandlerActivity),
				new Pressure(dataHandlerActivity),
				new Proximity(dataHandlerActivity),
				new RelativeHumidity(dataHandlerActivity) };
	}

	private void loadAvailableSensors() {
		SensorManager sensorManager = (SensorManager) dataHandlerActivity
				.getSystemService(Context.SENSOR_SERVICE);
		availableSensors = new ArrayList<>();
		for (MySensor s : allSensors) {
			if (settings.getBoolean(s.getName(), false)) {
				s.setAvailable(true);
				s.setSensor(sensorManager.getDefaultSensor(s.getType()));
				availableSensors.add(s);
			}
		}
	}

	private void performFullSensorCheck() {
		SensorManager sensorManager = (SensorManager) dataHandlerActivity
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			availableSensors = new ArrayList<>();
			for (MySensor s : allSensors) {
				if (sensorManager.getDefaultSensor(s.getType()) != null) {
					editor.putBoolean(s.getName(), true);
					s.setAvailable(true);
					s.setSensor(sensorManager.getDefaultSensor(s.getType()));
					availableSensors.add(s);
				} else {
					editor.putBoolean(s.getName(), false);
				}
			}
		} else {
			Log.e(TAG, "SensorManager not found");
		}

		if (BluetoothAdapter.getDefaultAdapter() != null) {
			isBluetoothAvailable = true;
			editor.putBoolean("isBluetoothAvailable", true);
		}
		editor.commit();
	}

	// Getter & Setter
	public int getBufferSizeLocalMeasuring() {
		return bufferSizeLocalMeasuring;
	}

	public boolean isFirstAppStart() {
		return isFirstAppStart;
	}

	public ArrayList<MySensor> getAvailableSensors() {
		return availableSensors;
	}

	public int getDefaultMeasuringInterval() {
		return defaultMeasuringInterval;
	}

	public boolean isInterruptMeasuringOnMinimize() {
		return interruptMeasuringOnMinimize;
	}

	public int getMaxGraphViewShowTime() {
		return maxGraphViewShowTime;
	}

	public int getMinDistanceDifferenceForUpdateInMeter() {
		return minDistanceDifferenceForUpdateInMeter;
	}

	public boolean isNetworkLocatingAllowed() {
		return isNetworkLocatingAllowed;
	}

	public boolean isShowThinkGear() {
		return showThinkGear;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public boolean isScreenRotationBlocked() {
		return isScreenRotationBlocked;
	}

	public boolean isKeepScreenONWhileMeasuring() {
		return keepScreenONWhileMeasuring;
	}

	public boolean isUseAutoScaling() {
		return useAutoScaling;
	}

	public boolean isShowWholeGraphAtEnd() {
		return showWholeGraphAtEnd;
	}

	public boolean isShowSummaryAtEnd() {
		return showSummaryAtEnd;
	}

	public int getGroupMax() {
		return groupMax;
	}

	public void setDeviceID(String deviceID) {
		editor.putString("deviceID", deviceID);
		editor.commit();
		this.deviceID = deviceID;
	}

	public void setInterruptMeasuringOnMinimize(
			boolean interruptMeasuringOnMinimize) {
		editor.putBoolean(
			"interruptMeasuringOnMinimize",
			interruptMeasuringOnMinimize);
		editor.commit();
		this.interruptMeasuringOnMinimize = interruptMeasuringOnMinimize;
	}

	public void setNetworkLocatingAllowed(boolean isNetworkLocatingAllowed) {
		editor.putBoolean("isNetworkLocatingAllowed", isNetworkLocatingAllowed);
		editor.commit();
		this.isNetworkLocatingAllowed = isNetworkLocatingAllowed;
	}

	public void setShowThinkGear(boolean showThinkGear) {
		editor.putBoolean("showThinkGear", showThinkGear);
		editor.commit();
		if (this.showThinkGear) {
			dataHandlerActivity.getMyFragmentManager()
					.getSensorOverviewFragment().updateGroupID(false, 10);
		}
		this.showThinkGear = showThinkGear;
	}

	public void setScreenRotationBlocked(boolean isScreenRotationBlocked) {
		editor.putBoolean("isScreenRotationBlocked", isScreenRotationBlocked);
		editor.commit();
		dataHandlerActivity.blockScreenRotation(isScreenRotationBlocked);
		this.isScreenRotationBlocked = isScreenRotationBlocked;
	}

	public void setKeepScreenONWhileMeasuring(boolean keepScreenONWhileMeasuring) {
		editor.putBoolean(
			"keepScreenONWhileMeasuring",
			keepScreenONWhileMeasuring);
		editor.commit();
		this.keepScreenONWhileMeasuring = keepScreenONWhileMeasuring;
	}

	public void setShowSummaryAtEnd(boolean showSummaryAtEnd) {
		editor.putBoolean("showSummaryAtEnd", showSummaryAtEnd);
		editor.commit();
		this.showSummaryAtEnd = showSummaryAtEnd;
	}

	public void setMaxGraphViewShowTime(int maxGraphViewShowTime) {
		editor.putInt("maxGraphViewShowTime", maxGraphViewShowTime);
		editor.commit();
		this.maxGraphViewShowTime = maxGraphViewShowTime;
	}

	public String getLiveStreamURL() {
		return liveStreamURL;
	}

	public void setLiveStreamURL(String liveStreamURL) {
		editor.putString("liveStreamURL", liveStreamURL);
		editor.commit();
		this.liveStreamURL = liveStreamURL;
	}

	public boolean isLiveStreamWellConfigurated() {
		return liveStreamURL != null && liveStreamURL.startsWith("http")
				&& deviceID != null && deviceID.length() > 0;
	}

	public boolean isBluetoothAvailable() {
		return isBluetoothAvailable;
	}

}
