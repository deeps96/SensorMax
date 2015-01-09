package com.deeps.sensormax.controller.fragments.measurementinfo;

import com.deeps.sensormax.controller.fragments.MyFragmentManager;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.sensors.MySensor;

/**
 * @author Deeps
 */

public class SensorMeasurementInfoFragment extends MeasurementInfoFragment {

	private MySensor mySensor;

	public SensorMeasurementInfoFragment(
			DataHandlerActivity dataHandlerActivity, MySensor mySensor,
			MyFragmentManager myFragmentManager) {
		super(dataHandlerActivity, myFragmentManager);
		this.mySensor = mySensor;
		title = mySensor.getName();
		initSensorValues();
		initAdditionalInformation();
	}

	private void initSensorValues() {
		showSensorInfoLayout = true;
		type = mySensor.getName();
		vendor = mySensor.getSensor().getVendor();
		name = mySensor.getSensor().getName();
		version = mySensor.getSensor().getVersion();
		power = mySensor.getSensor().getPower();
		resolution = mySensor.getSensor().getResolution();
		minRange = mySensor.getMinValue();
		maxRange = mySensor.getMaxValue();
		unit = mySensor.getMeasuringUnit();
	}

	private void initAdditionalInformation() {
		additionalInformation = mySensor.getAdditionalInfo();
	}
}
