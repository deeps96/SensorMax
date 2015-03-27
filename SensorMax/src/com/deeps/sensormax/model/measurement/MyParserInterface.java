package com.deeps.sensormax.model.measurement;

/**
 * @author Deeps
 */

public interface MyParserInterface {

	public String getNext();

	public boolean hasNext();

	public int getSize();

	public int getCurrentPosition();

}
