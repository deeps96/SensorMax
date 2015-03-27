package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class StepDetector extends MySensor {

	public StepDetector(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		type = Sensor.TYPE_STEP_DETECTOR;
		name = dataHandlerActivity
				.getString(R.string.sensor_step_detector_name);
		axisLabels = new String[] { dataHandlerActivity
				.getString(R.string.step) };
		measuringUnit = dataHandlerActivity.getString(R.string.unitless);
	}

	@Override
	protected void initSensorBounds() {
		minValue = 0;
		maxValue = 1;
	}

	@Override
	protected void initAdditionalInfo() {
		additionalInfo = dataHandlerActivity
				.getString(R.string.infotext_step_counter);
	}

}
