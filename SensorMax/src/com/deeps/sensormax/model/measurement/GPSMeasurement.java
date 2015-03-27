package com.deeps.sensormax.model.measurement;

import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.measurement.GPSMeasuringFragment;
import com.deeps.sensormax.model.Utils;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class GPSMeasurement extends Measurement implements LocationListener,
		Listener {

	public static final int LATITUDE_INDEX = 0, LONGITUDE_INDEX = 1,
			SPEED_INDEX = 2, SATELLITES_COUNT_INDEX = 3,
			EXTRA_INFORMATION_COUNT = 4; // latitude, longitude, speed, amount
										 // of satellites
	private final String TAG = getClass().getName();

	private boolean hasUserEnabledGPS, hasUserEnabledNetwork, isConnected,
			isNetworkAllowed;
	private int satelliteCounter;
	private Location location;
	private LocationManager locationManager;
	private long minDistanceDifferenceForUpdate;

	private GPSMeasuringFragment gpsMeasuringFragment;

	/*
	 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
	 */

	public GPSMeasurement(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		minDistanceDifferenceForUpdate = dataHandlerActivity.getMyConfig()
				.getMinDistanceDifferenceForUpdateInMeter();
		isNetworkAllowed = dataHandlerActivity.getMyConfig()
				.isNetworkLocatingAllowed();
		initialise();
	}

	@Override
	protected void updateUIValues(float[] modifiedData, int time) {
		gpsMeasuringFragment.update(modifiedData, time);
	}

	@Override
	public String getCSVHeader() {
		String[] header = new String[getAxisCount()];
		header[LATITUDE_INDEX] = dataHandlerActivity
				.getString(R.string.latitude);
		header[LONGITUDE_INDEX] = dataHandlerActivity
				.getString(R.string.longitude);
		header[SPEED_INDEX] = dataHandlerActivity.getString(R.string.speed);
		header[SATELLITES_COUNT_INDEX] = dataHandlerActivity
				.getString(R.string.satellite_count);
		return Utils.convertHeaderToCSV(header);
	}

	@Override
	public String getCSV(int currentDataIndex) {
		Utils.convertDataSetToCSV(
			data[currentDataIndex],
			time[currentDataIndex],
			highlightedMeasuringValues[currentDataIndex]);
		return null;
	}

	@Override
	protected int getAxisCount() {
		return EXTRA_INFORMATION_COUNT;
	}

	@Override
	protected void registerListener() {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				registerGPS();
			}
		});
	}

	private void registerGPS() {
		try {
			locationManager = (LocationManager) dataHandlerActivity
					.getSystemService(Context.LOCATION_SERVICE);
			if (isNetworkAllowed) {
				hasUserEnabledNetwork = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			}
			hasUserEnabledGPS = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (hasUserEnabledNetwork) {
				locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					measuringIntervalInMS,
					minDistanceDifferenceForUpdate,
					this);
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				onLocationChanged(location);
			}
			if (hasUserEnabledGPS) {
				locationManager.addGpsStatusListener(this);
				locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					measuringIntervalInMS,
					minDistanceDifferenceForUpdate,
					this);
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				onLocationChanged(location);
			} else {
				showGPSSettingsAlert();
				stopMeasuring(true);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void pauseMeasuring(boolean updateGroupState) {
		setState(STATE_PAUSED, updateGroupState);
	}

	@Override
	public void resumeMeasuring(boolean updateGroupState) {
		setState(STATE_RUNNING, updateGroupState);
	}

	@Override
	protected void unregisterListener() {
		if (locationManager != null) {
			location = null;
			locationManager.removeUpdates(this);
			locationManager.removeGpsStatusListener(this);
		}
	}

	public void showGPSSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				dataHandlerActivity);

		alertDialog.setTitle(dataHandlerActivity
				.getString(R.string.gps_settings_alert_title));

		alertDialog.setMessage(dataHandlerActivity
				.getString(R.string.gps_settings_alert_content));

		alertDialog.setPositiveButton(
			dataHandlerActivity.getString(R.string.settings),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					dataHandlerActivity.startActivity(intent);
				}
			});

		alertDialog.setNegativeButton(
			dataHandlerActivity.getString(R.string.cancel),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		recentDataSet[SATELLITES_COUNT_INDEX] = satelliteCounter;
		if (location == null)
			return;
		location.set(location);
		recentDataSet[LATITUDE_INDEX] = (float) location.getLatitude();
		recentDataSet[LONGITUDE_INDEX] = (float) location.getLongitude();
		recentDataSet[SPEED_INDEX] = location.getSpeed();

		/*
		 * recentDataSet[SATELLITES_COUNT] = location.getExtras().getInt(
		 * "satellites");
		 */

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onGpsStatusChanged(int event) {
		if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS
				|| event == GpsStatus.GPS_EVENT_FIRST_FIX) {
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
			Iterator<GpsSatellite> sat = satellites.iterator();
			int counter = 0;
			while (sat.hasNext()) {
				GpsSatellite satellite = sat.next();
				if (satellite.usedInFix())
					counter++;
			}
			satelliteCounter = counter;
		}
		switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (location == null) {
					isConnected = false;
				} else {
					isConnected = (SystemClock.elapsedRealtime()
							- location.getTime() < 3000);
				}
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				isConnected = true;
				break;
		}
		onLocationChanged(location);
	}

	// Setter & Getter

	public boolean isConnected() {
		return isConnected;
	}

	public void setGpsMeasuringFragment(
			GPSMeasuringFragment gpsMeasuringFragment) {
		this.gpsMeasuringFragment = gpsMeasuringFragment;
		this.localMeasurementFragment = gpsMeasuringFragment;
	}

}
