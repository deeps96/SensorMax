package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class Gyroscope extends MySensor {

	public Gyroscope(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_GYROSCOPE;
		name = dataHandlerActivity.getString(R.string.sensor_gyroscope_name);
		axisLabels = new String[] {
				dataHandlerActivity.getString(R.string.x_in_rads),
				dataHandlerActivity.getString(R.string.y_in_rads),
				dataHandlerActivity.getString(R.string.z_in_rads) };
		measuringUnit = dataHandlerActivity.getString(R.string.rads);
	}

	@Override
	protected void initSensorBounds() {
		minValue = -sensor.getMaximumRange();
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_gyroscope);
	}
}
