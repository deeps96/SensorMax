package com.deeps.sensormax.controller.fragments;

import java.util.Arrays;

import android.support.v4.app.Fragment;

import com.deeps.sensormax.controller.fragments.measurement.AudioMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurement.GPSMeasuringFragment;
import com.deeps.sensormax.controller.fragments.measurement.GroupMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurement.LocalMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurement.SensorMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurement.ThinkGearMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.AudioMeasurementInfoFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.GPSMeasurementInfoFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.MeasurementInfoFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.SensorMeasurementInfoFragment;
import com.deeps.sensormax.controller.fragments.measurementinfo.ThinkGearMeasurementInfoFragment;
import com.deeps.sensormax.controller.fragments.menu.AboutFragment;
import com.deeps.sensormax.controller.fragments.menu.HomeFragment;
import com.deeps.sensormax.controller.fragments.menu.SensorOverviewFragment;
import com.deeps.sensormax.controller.fragments.menu.SettingsFragment;
import com.deeps.sensormax.controller.fragments.menu.ToneGeneratorFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.MeasurementManager;

/**
 * @author Deeps
 */

public class MyFragmentManager {

	private final int ADDITIONAL_LOCAL_MEASUREMENTS = 3, EXTRA_COUNT = 6,
			SENSOR_OVERVIEW = 0, HOME = 1, TONE_GENERATOR = 2, SETTINGS = 3,
			ABOUT = 4, GROUP_MEASUREMENT = 5, AUDIO_MEASUREMENT = 0,
			GPS_MEASUREMENT = 1, THINK_GEAR_MEASUREMENT = 2;

	private int availableSensors;
	private DataHandlerActivity dataHandlerActivity;
	private Fragment[] extraFragments;

	private LocalMeasurementFragment[] localMeasurementFragments;
	private MeasurementInfoFragment[] measurementInfoFragments;

	public MyFragmentManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		initMeasurementFragments();
		initMeasurementInfoFragments(); // has to be after measurementfragments
		assignInfoFragmentsToMeasurementFragments();
		initExtraFragments();
	}

	private void initExtraFragments() {
		extraFragments = new Fragment[EXTRA_COUNT];
		// extraPages
		extraFragments[SENSOR_OVERVIEW] = new SensorOverviewFragment(
				dataHandlerActivity);
		extraFragments[HOME] = new HomeFragment(dataHandlerActivity);
		extraFragments[TONE_GENERATOR] = new ToneGeneratorFragment(
				dataHandlerActivity, dataHandlerActivity.getModelManager()
						.getMyToneGenerator());
		extraFragments[SETTINGS] = new SettingsFragment(dataHandlerActivity);
		extraFragments[ABOUT] = new AboutFragment(dataHandlerActivity);
		extraFragments[GROUP_MEASUREMENT] = new GroupMeasurementFragment(
				dataHandlerActivity);

		// assigning managers
		dataHandlerActivity
				.getModelManager()
				.getMyToneGenerator()
				.setToneGeneratorFragment(
					(ToneGeneratorFragment) extraFragments[TONE_GENERATOR]);
	}

	public void initMeasurementFragments() {
		MeasurementManager measurementManager = dataHandlerActivity
				.getModelManager().getMeasurementManager();
		availableSensors = dataHandlerActivity.getMyConfig()
				.getAvailableSensors().size();

		localMeasurementFragments = new LocalMeasurementFragment[availableSensors
				+ ADDITIONAL_LOCAL_MEASUREMENTS];
		// classical sensorfragments
		for (int i = 0; i < availableSensors; i++) {
			localMeasurementFragments[i] = new SensorMeasurementFragment(
					dataHandlerActivity, measurementManager
							.getSensorMeasurements().get(i));
			measurementManager
					.getSensorMeasurements()
					.get(i)
					.setLocalMeasurementFragment(
						(SensorMeasurementFragment) localMeasurementFragments[i]);
		}

		// Additional local measurements
		localMeasurementFragments[availableSensors + AUDIO_MEASUREMENT] = new AudioMeasurementFragment(
				dataHandlerActivity, measurementManager.getAudioMeasurement());
		localMeasurementFragments[availableSensors + GPS_MEASUREMENT] = new GPSMeasuringFragment(
				dataHandlerActivity, measurementManager.getGpsMeasurement());

		// assigning managers
		measurementManager
				.getAudioMeasurement()
				.setAudioMeasurementFragment(
					(AudioMeasurementFragment) localMeasurementFragments[availableSensors
							+ AUDIO_MEASUREMENT]);
		measurementManager.getGpsMeasurement().setGpsMeasuringFragment(
			(GPSMeasuringFragment) localMeasurementFragments[availableSensors
					+ GPS_MEASUREMENT]);

		// optional fragments
		localMeasurementFragments[availableSensors + THINK_GEAR_MEASUREMENT] = new ThinkGearMeasurementFragment(
				dataHandlerActivity,
				measurementManager.getThinkGearMeasurement());
		measurementManager
				.getThinkGearMeasurement()
				.setThinkGearFragment(
					(ThinkGearMeasurementFragment) localMeasurementFragments[availableSensors
							+ THINK_GEAR_MEASUREMENT]);
	}

	private void initMeasurementInfoFragments() {
		measurementInfoFragments = new MeasurementInfoFragment[localMeasurementFragments.length];
		for (int iSensor = 0; iSensor < dataHandlerActivity.getMyConfig()
				.getAvailableSensors().size(); iSensor++) {
			measurementInfoFragments[iSensor] = new SensorMeasurementInfoFragment(
					dataHandlerActivity, dataHandlerActivity.getMyConfig()
							.getAvailableSensors().get(iSensor), this);
		}

		// additional local measurements
		measurementInfoFragments[availableSensors + AUDIO_MEASUREMENT] = new AudioMeasurementInfoFragment(
				dataHandlerActivity, this);
		measurementInfoFragments[availableSensors + GPS_MEASUREMENT] = new GPSMeasurementInfoFragment(
				dataHandlerActivity, this);
		measurementInfoFragments[availableSensors + THINK_GEAR_MEASUREMENT] = new ThinkGearMeasurementInfoFragment(
				dataHandlerActivity, this);
	}

	private void assignInfoFragmentsToMeasurementFragments() {
		for (int iMeasurement = 0; iMeasurement < localMeasurementFragments.length; iMeasurement++) {
			localMeasurementFragments[iMeasurement]
					.setMeasurementInfoFragment(measurementInfoFragments[iMeasurement]);
		}
	}

	// Getter & Setter
	public SensorOverviewFragment getSensorOverviewFragment() {
		return (SensorOverviewFragment) extraFragments[SENSOR_OVERVIEW];
	}

	public HomeFragment getHomeFragment() {
		return (HomeFragment) extraFragments[HOME];
	}

	public ToneGeneratorFragment getTonGeneratorFragment() {
		return (ToneGeneratorFragment) extraFragments[TONE_GENERATOR];
	}

	public SettingsFragment getSettingsFragment() {
		return (SettingsFragment) extraFragments[SETTINGS];
	}

	public AboutFragment getAboutFragment() {
		return (AboutFragment) extraFragments[ABOUT];
	}

	public LocalMeasurementFragment[] getLocalMeasurementFragments() {
		if (!dataHandlerActivity.getMyConfig().isShowThinkGear()) {
			return Arrays.copyOfRange(
				localMeasurementFragments,
				0,
				localMeasurementFragments.length - 1);
		}
		return localMeasurementFragments;
	}

	public GroupMeasurementFragment getGroupMeasurementFragment() {
		return (GroupMeasurementFragment) extraFragments[GROUP_MEASUREMENT];
	}

	public GPSMeasuringFragment getGPSMeasuringFragment() {
		return (GPSMeasuringFragment) localMeasurementFragments[availableSensors
				+ GPS_MEASUREMENT];
	}

	public ThinkGearMeasurementFragment getThinkGearFragment() {
		return (ThinkGearMeasurementFragment) localMeasurementFragments[availableSensors
				+ THINK_GEAR_MEASUREMENT];
	}

	public AudioMeasurementFragment getAudioMeasurementFragment() {
		return (AudioMeasurementFragment) localMeasurementFragments[availableSensors
				+ AUDIO_MEASUREMENT];
	}

}
