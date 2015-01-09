package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class RelativeHumidity extends MySensor {

	public RelativeHumidity(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_RELATIVE_HUMIDITY;
		name = dataHandlerActivity
				.getString(R.string.sensor_relative_humidity_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.phi_in_procent) };
		measuringUnit = dataHandlerActivity.getString(R.string.procent);
	}

	@Override
	protected void initSensorBounds() {
		minValue = 0.0f;
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_relative_humidity);
	}
}
