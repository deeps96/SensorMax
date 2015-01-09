package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class Proximity extends MySensor {

	public Proximity(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_PROXIMITY;
		name = dataHandlerActivity.getString(R.string.sensor_proximity_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.s_in_cm) };
		measuringUnit = dataHandlerActivity.getString(R.string.cm);
	}

	@Override
	protected void initSensorBounds() {
		minValue = 0.0f;
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_proximity);
	}
}
