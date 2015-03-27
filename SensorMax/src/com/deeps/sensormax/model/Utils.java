package com.deeps.sensormax.model;


/**
 * @author Deeps
 */

public class Utils {

	private static final String LINE_SEPERATOR = System
			.getProperty("line.separator");

	public static String convertHeaderToCSV(String[] header) {
		StringBuilder csvStringBuilder = new StringBuilder();
		csvStringBuilder.append("\"Zeit in ms:\";");
		for (String s : header) {
			csvStringBuilder.append("\"" + s + "\";");
		}
		csvStringBuilder.append("\"Markiert:\";");
		csvStringBuilder.append(LINE_SEPERATOR);
		return csvStringBuilder.toString();
	}

	public static String convertDataSetToCSV(float[] data, int time,
			boolean isHighlighted) {
		StringBuilder csvStringBuilder = new StringBuilder();
		csvStringBuilder.append("\"" + time + "\";");
		for (float f : data) {
			csvStringBuilder.append("\""
					+ Float.toString(f).replaceAll("\\.", ",") + "\";");
		}
		if (isHighlighted) {
			csvStringBuilder.append("\"x\";");
		} else {
			csvStringBuilder.append("\"\";");
		}
		csvStringBuilder.append(LINE_SEPERATOR);
		return csvStringBuilder.toString();
	}

}
