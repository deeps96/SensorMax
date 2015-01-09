package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class LinearAcceleration extends MySensor {

	public LinearAcceleration(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_LINEAR_ACCELERATION;
		name = dataHandlerActivity
				.getString(R.string.sensor_linear_acceleration_name);
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
				.getString(R.string.infotext_linear_acceleration);
	}
}
