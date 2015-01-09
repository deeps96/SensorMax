package com.deeps.sensormax.model;

import android.os.Bundle;

import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.MeasurementManager;

/**
 * @author Deeps
 */

public class ModelManager {

	private DataHandlerActivity dataHandlerActivity;
	private MeasurementManager measurementManager;
	private MyToneGenerator myToneGenerator;

	public ModelManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
	}

	public void initEnvironment() {
		measurementManager = new MeasurementManager(dataHandlerActivity);
		myToneGenerator = new MyToneGenerator();
	}

	public void onCreate(Bundle savedInstanceState) {
	}

	public void onResume() {
		measurementManager.onResume();
	}

	public void onPause() {
		measurementManager.onPause();
	}

	public void onDestroy() {
		measurementManager.onDestroy();
	}

	// Setter & Getter
	public MeasurementManager getMeasurementManager() {
		return measurementManager;
	}

	public MyToneGenerator getMyToneGenerator() {
		return myToneGenerator;
	}

}
