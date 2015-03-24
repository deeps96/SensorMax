package com.deeps.sensormax.model.measurement;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.measurement.ThinkGearMeasurementFragment;
import com.deeps.sensormax.model.Utils;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGRawMulti;

/**
 * @author Deeps
 */

public class ThinkGearMeasurement extends Measurement {

	public static final int CHANNEL_COUNT = 8, HEART_RATE_INDEX = 8,
			EXTRA_INFORMATION_COUNT = 1;

	private boolean isBluetoothEnabledOnStart, isConnected;
	private BluetoothAdapter bluetoothAdapter;
	private int recentHeartRate = 0;

	private TGDevice tgDevice;
	private ThinkGearMeasurementFragment thinkGearFragment;

	public ThinkGearMeasurement(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		initialise();

		if (dataHandlerActivity.getMyConfig().isBluetoothAvailable()) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			isBluetoothEnabledOnStart = bluetoothAdapter.isEnabled();
			tgDevice = new TGDevice(bluetoothAdapter, new ThinkGearHandler());
		}
	}

	@Override
	protected void updateUIValues(float[] modifiedData, int time) {
		thinkGearFragment.update(modifiedData, time);
	}

	@Override
	protected boolean isTriggerReleased(float[] modifiedData) {
		if (!isConnected) {
			return false;
		}
		return super.isTriggerReleased(modifiedData);
	}

	@Override
	public String getCSV() {
		String[] header = new String[getAxisCount()];
		String channelShort = dataHandlerActivity
				.getString(R.string.channel_short);
		for (int i = 0; i < CHANNEL_COUNT; i++) {
			header[i] = channelShort + Integer.toString(i);
		}
		header[CHANNEL_COUNT + 0] = dataHandlerActivity
				.getString(R.string.heart_rate);
		return Utils.convertToCSV(
			header,
			data,
			time,
			highlightedMeasuringValues,
			dataCounter);

	}

	@Override
	protected int getAxisCount() {
		return CHANNEL_COUNT + EXTRA_INFORMATION_COUNT;
	}

	@Override
	protected void registerListener() {
		// note: device has to be linked to the think gear device
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
		if (tgDevice.getState() != TGDevice.STATE_CONNECTING
				&& tgDevice.getState() != TGDevice.STATE_CONNECTED) {
			tgDevice.connect(true);
		}
	}

	@Override
	protected void unregisterListener() {
		tgDevice.stop();
		if (bluetoothAdapter != null && !isBluetoothEnabledOnStart)
			bluetoothAdapter.disable();
	}

	// Setter & Getter

	public void setThinkGearFragment(
			ThinkGearMeasurementFragment thinkGearFragment) {
		this.thinkGearFragment = thinkGearFragment;
		this.localMeasurementFragment = thinkGearFragment;
	}

	private class ThinkGearHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TGDevice.MSG_STATE_CHANGE:
					switch (msg.arg1) {
						case TGDevice.STATE_CONNECTING:
							Toast
									.makeText(
										dataHandlerActivity,
										dataHandlerActivity
												.getString(R.string.info_think_gear_connecting),
										Toast.LENGTH_SHORT).show();
							break;
						case TGDevice.STATE_CONNECTED:
							isConnected = true;
							tgDevice.start();
							break;
						case TGDevice.STATE_NOT_FOUND:
							stopMeasuring(true);
							Toast
									.makeText(
										dataHandlerActivity,
										dataHandlerActivity
												.getString(R.string.error_think_gear_not_found),
										Toast.LENGTH_SHORT).show();
							break;
						case TGDevice.STATE_NOT_PAIRED:
							stopMeasuring(true);
							Toast
									.makeText(
										dataHandlerActivity,
										dataHandlerActivity
												.getString(R.string.request_think_gear_pair),
										Toast.LENGTH_SHORT).show();
							break;
						case TGDevice.STATE_DISCONNECTED:
							stopMeasuring(true);
							Toast
									.makeText(
										dataHandlerActivity,
										dataHandlerActivity
												.getString(R.string.info_think_gear_disconnected),
										Toast.LENGTH_SHORT).show();
							break;
					}
					break;
				case TGDevice.MSG_POOR_SIGNAL:
					Toast
							.makeText(
								dataHandlerActivity,
								dataHandlerActivity
										.getString(R.string.info_think_gear_poor_signal),
								Toast.LENGTH_SHORT).show();
					break;
				case TGDevice.MSG_HEART_RATE:
					recentHeartRate = msg.arg1;
					break;
				case TGDevice.MSG_LOW_BATTERY:
					Toast
							.makeText(
								dataHandlerActivity,
								dataHandlerActivity
										.getString(R.string.info_think_gear_battery_low),
								Toast.LENGTH_SHORT).show();
					break;
				case TGDevice.MSG_RAW_MULTI:
					TGRawMulti rawM = (TGRawMulti) msg.obj;
					recentDataSet[0] = rawM.ch1;
					recentDataSet[1] = rawM.ch2;
					recentDataSet[2] = rawM.ch3;
					recentDataSet[3] = rawM.ch4;
					recentDataSet[4] = rawM.ch5;
					recentDataSet[5] = rawM.ch6;
					recentDataSet[6] = rawM.ch7;
					recentDataSet[7] = rawM.ch8;
					recentDataSet[8] = recentHeartRate;
			}
		}

	}
}
