package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class AmbientTemperature extends MySensor {

	public AmbientTemperature(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_AMBIENT_TEMPERATURE;
		name = dataHandlerActivity
				.getString(R.string.sensor_ambient_temperature_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.t_in_c) };
		measuringUnit = dataHandlerActivity.getString(R.string.c);
	}

	@Override
	protected void initSensorBounds() {
		minValue = -sensor.getMaximumRange();
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_ambient_temperature);
	}
}
