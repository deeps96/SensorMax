package com.deeps.sensormax.model.measurement;

import java.util.ArrayList;

import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.sensors.MySensor;

/**
 * @author Deeps
 */

public class MeasurementManager {

	private AudioMeasurement audioMeasurement;
	private GPSMeasurement gpsMeasurement;
	private ArrayList<Measurement> measurements;
	private ArrayList<SensorMeasurement> sensorMeasurements;
	private ThinkGearMeasurement thinkGearMeasurement;
	private DataHandlerActivity dataHandlerActivity;

	public MeasurementManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		measurements = new ArrayList<>();
		initSensorMeasurements();
		initAudioMeasurement();
		initGPSMeasurement();
		initThinkGearMeasurement();
	}

	private void initThinkGearMeasurement() {
		thinkGearMeasurement = new ThinkGearMeasurement(dataHandlerActivity);
		measurements.add(thinkGearMeasurement);
	}

	private void initGPSMeasurement() {
		gpsMeasurement = new GPSMeasurement(dataHandlerActivity);
		measurements.add(gpsMeasurement);
	}

	private void initAudioMeasurement() {
		audioMeasurement = new AudioMeasurement(dataHandlerActivity);
		measurements.add(audioMeasurement);
	}

	private void initSensorMeasurements() {
		sensorMeasurements = new ArrayList<>();
		for (MySensor s : dataHandlerActivity.getMyConfig()
				.getAvailableSensors()) {
			sensorMeasurements
					.add(new SensorMeasurement(dataHandlerActivity, s));
		}
		measurements.addAll(sensorMeasurements);
	}

	public void onResume() {
		for (Measurement m : measurements)
			if (m.getState() == Measurement.STATE_INTERRUPTED)
				m.resumeMeasuring(false);
	}

	public void onPause() {
		for (Measurement m : measurements)
			if (m.getState() == Measurement.STATE_RUNNING)
				m.interruptMeasuring(false);
	}

	public void onDestroy() {
		for (Measurement m : measurements)
			if (m.getState() != Measurement.STATE_STOPPED)
				m.stopMeasuring(false);
	}

	// Getter & Setter
	public ArrayList<SensorMeasurement> getSensorMeasurements() {
		return sensorMeasurements;
	}

	public AudioMeasurement getAudioMeasurement() {
		return audioMeasurement;
	}

	public GPSMeasurement getGpsMeasurement() {
		return gpsMeasurement;
	}

	public ArrayList<Measurement> getMeasurements() {
		return measurements;
	}

	public ThinkGearMeasurement getThinkGearMeasurement() {
		return thinkGearMeasurement;
	}

}
