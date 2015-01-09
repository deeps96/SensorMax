package com.deeps.sensormax.model.sensors;

import android.hardware.Sensor;

import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public abstract class MySensor {

	protected boolean isAvailable;
	protected int type;
	protected float minValue, maxValue;
	protected String measuringUnit, name, additionalInfo;
	protected String[] axisLabels;
	protected Sensor sensor;

	protected DataHandlerActivity dataHandlerActivity;

	public MySensor(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		initAdditionalInfo();
	}

	protected abstract void initSensorBounds();

	protected abstract void initAdditionalInfo();

	// Getter & Setter
	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String[] getAxisLabels() {
		return axisLabels;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
		initSensorBounds();
	}

	public float getMinValue() {
		return minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public String getMeasuringUnit() {
		return measuringUnit;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

}
