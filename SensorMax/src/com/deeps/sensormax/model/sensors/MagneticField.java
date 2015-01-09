package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class MagneticField extends MySensor {

	public MagneticField(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_MAGNETIC_FIELD;
		name = dataHandlerActivity
				.getString(R.string.sensor_magnetic_field_name);
		axisLabels = new String[] {
				dataHandlerActivity.getString(R.string.x_in_mt),
				dataHandlerActivity.getString(R.string.y_in_mt),
				dataHandlerActivity.getString(R.string.z_in_mt) };
		measuringUnit = dataHandlerActivity.getString(R.string.mt);
	}

	@Override
	protected void initSensorBounds() {
		minValue = -sensor.getMaximumRange();
		maxValue = sensor.getMaximumRange();
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_magnetic_field);
	}
}
