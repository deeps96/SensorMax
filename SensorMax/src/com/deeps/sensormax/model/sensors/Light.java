package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class Light extends MySensor {

	public Light(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_LIGHT;
		name = dataHandlerActivity.getString(R.string.sensor_light_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.e_in_lux) };
		measuringUnit = dataHandlerActivity.getString(R.string.lux);
	}

	@Override
	protected void initSensorBounds() {
		minValue = 0.0f;
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity.getString(R.string.infotext_light);
	}
}
