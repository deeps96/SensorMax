package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class Accelerometer extends MySensor {

	public Accelerometer(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_ACCELEROMETER;
		name = dataHandlerActivity
				.getString(R.string.sensor_accelerometer_name);
		axisLabels = new String[] {
				dataHandlerActivity.getString(R.string.x_in_ms2),
				dataHandlerActivity.getString(R.string.y_in_ms2),
				dataHandlerActivity.getString(R.string.z_in_ms2) };
		measuringUnit = dataHandlerActivity.getString(R.string.ms2);
	}

	@Override
	protected void initSensorBounds() {
		minValue = -sensor.getMaximumRange();
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_acceleration);
	}
}
