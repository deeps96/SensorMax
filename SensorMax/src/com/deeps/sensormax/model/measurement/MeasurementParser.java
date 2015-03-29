package com.deeps.sensormax.model.measurement;

import com.deeps.sensormax.model.interfaces.MyParserInterface;

/**
 * @author Deeps
 */

public class MeasurementParser implements MyParserInterface {

	private int currentDataIndex;

	private Measurement measurement;

	public MeasurementParser(Measurement measurement) {
		this.measurement = measurement;
		currentDataIndex = -1;
	}

	@Override
	public String getNext() {
		String csv = null;
		if (currentDataIndex == -1) {
			csv = measurement.getCSVHeader();
		} else {
			csv = measurement.getCSV(currentDataIndex);
		}
		currentDataIndex++;
		return csv;
	}

	@Override
	public boolean hasNext() {
		return currentDataIndex < measurement.getDataCounter();
	}

	@Override
	public int getSize() {
		return measurement.getDataCounter();
	}

	@Override
	public int getCurrentPosition() {
		return currentDataIndex;
	}
}
