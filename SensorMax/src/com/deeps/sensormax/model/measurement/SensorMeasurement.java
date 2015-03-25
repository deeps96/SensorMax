package com.deeps.sensormax.model.measurement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.deeps.sensormax.controller.fragments.measurement.SensorMeasurementFragment;
import com.deeps.sensormax.model.Utils;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.sensors.MySensor;

/**
 * @author Deeps
 */

public class SensorMeasurement extends Measurement implements
		SensorEventListener {

	protected MySensor sensor;

	protected SensorMeasurementFragment sensorMeasurementFragment;

	public SensorMeasurement(DataHandlerActivity dataHandlerActivity,
			MySensor sensor) {
		super(dataHandlerActivity);
		this.sensor = sensor;
		initialise();
	}

	@Override
	public String getCSV() {
		return Utils.convertToCSV(
			sensor.getAxisLabels(),
			data,
			time,
			highlightedMeasuringValues,
			dataCounter);
	}

	@Override
	protected void updateUIValues(float[] modifiedData, int time) {
		if (sensorMeasurementFragment != null) {
			sensorMeasurementFragment.update(modifiedData, time);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
			recentDataSet[0]++;
			return;
		}
		if (event.sensor.getType() == sensor.getType()) {
			for (int i = 0; i < recentDataSet.length; i++) {
				recentDataSet[i] = event.values[i];
			}
		}
	}

	// Setter & Getter
	public void setLocalMeasurementFragment(
			SensorMeasurementFragment sensorMeasurementFragment) {
		this.sensorMeasurementFragment = sensorMeasurementFragment;
		this.localMeasurementFragment = sensorMeasurementFragment;
	}

	@Override
	protected void registerListener() {
		sensorManager.registerListener(
			this,
			sensor.getSensor(),
			measuringIntervalInMS);
	}

	@Override
	protected int getAxisCount() {
		return sensor.getAxisLabels().length;
	}

	@Override
	protected void unregisterListener() {
		sensorManager.unregisterListener(this);
	}

	// Getter & Setter
	public MySensor getSensor() {
		return sensor;
	}

	@Override
	public void setMeasuringIntervalInMS(int measuringIntervalInMS) {
		super.setMeasuringIntervalInMS(measuringIntervalInMS);
		unregisterListener();
		registerListener();
	}

}
