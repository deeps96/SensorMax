package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class Pressure extends MySensor {

	public Pressure(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_PRESSURE;
		name = dataHandlerActivity.getString(R.string.sensor_pressure_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.p_in_hpa) };
		measuringUnit = dataHandlerActivity.getString(R.string.hpa);
	}

	@Override
	protected void initSensorBounds() {
		minValue = 0.0f;
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_pressure);
	}
}
