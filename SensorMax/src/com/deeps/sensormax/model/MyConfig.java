package com.deeps.sensormax.model;

import java.security.MessageDigest;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.deeps.sensormax.model.sensors.StepDetector;

/**
 * @author Deeps
 */

public class MyConfig {

	private final String TAG = getClass().getName();

	private boolean isFirstAppStart, interruptMeasuringOnMinimize,
			isNetworkLocatingAllowed, showThinkGear, isScreenRotationBlocked,
			keepScreenONWhileMeasuring, showSummaryAtEnd, isBluetoothAvailable,
			recordWholeAudioSpectrum, useAutoScaling = true,
			showWholeGraphAtEnd = true;
	private int maxGraphViewShowTime, bufferSizeLocalMeasuring = 180000,
			defaultMeasuringInterval = 20,
			minDistanceDifferenceForUpdateInMeter = 0, groupMax = 1;
	private Editor editor;
	private SharedPreferences settings;
	private String deviceHash, serverAddress;
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
		isBluetoothAvailable = settings.getBoolean(
			"isBluetoothAvailable",
			false);
		deviceHash = settings.getString("deviceHash", null);
		if (deviceHash == null) { // if no clean install happend
			generateDeviceHash();
			editor.putString("deviceHash", deviceHash);
			editor.commit();
		}
		serverAddress = settings.getString("serverAddress", "");
		recordWholeAudioSpectrum = settings.getBoolean(
			"recordWholeAudioSpectrum",
			false);
		// apply settings
		dataHandlerActivity.blockScreenRotation(isScreenRotationBlocked);
	}

	private void onFirstAppStart() {
		editor.putBoolean("isFirstAppStart", false);
		generateDeviceHash();
		editor.putString("deviceHash", deviceHash);
		performFullSensorCheck(); // editor gets commited here
	}

	private void generateDeviceHash() {
		// http://stackoverflow.com/questions/5980658/how-to-sha1-hash-a-string-in-android
		WifiManager manager = (WifiManager) dataHandlerActivity
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(info.getMacAddress().getBytes());
			byte[] bytes = md.digest();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1);
				buffer.append(tmp);
			}
			deviceHash = buffer.toString();
		} catch (Exception e) {
		}
	}

	private void initAppSettings() {
		settings = dataHandlerActivity.getSharedPreferences(
			dataHandlerActivity.getResources().getString(R.string.app_name),
			Context.MODE_PRIVATE);
		editor = settings.edit();
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
				new RelativeHumidity(dataHandlerActivity),
				new StepDetector(dataHandlerActivity) };
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

	public String getDeviceHash() {
		return deviceHash;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		editor.putString("serverAddress", serverAddress);
		editor.commit();
		this.serverAddress = serverAddress;
	}

	public boolean isBluetoothAvailable() {
		return isBluetoothAvailable;
	}

	public boolean isLiveStreamWellConfigurated() {
		return serverAddress.length() > 0;
	}

	public boolean isRecordWholeAudioSpectrum() {
		return recordWholeAudioSpectrum;
	}

	public void setRecordWholeAudioSpectrum(boolean recordWholeAudioSpectrum) {
		editor.putBoolean("recordWholeAudioSpectrum", recordWholeAudioSpectrum);
		editor.commit();
		this.recordWholeAudioSpectrum = recordWholeAudioSpectrum;
	}

}
