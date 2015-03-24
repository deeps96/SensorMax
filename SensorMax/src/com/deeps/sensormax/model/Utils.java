package com.deeps.sensormax.model;

/**
 * @author Deeps
 */

public class Utils {

	private static final String LINE_SEPERATOR = System
			.getProperty("line.separator");

	public static String convertToCSV(String[] header, float[][] data,
			int[] time, boolean[] isHighlighted, int dataCounter) {
		String csv = "\"Zeit in ms:\";";
		for (String s : header) {
			csv += "\"" + s + "\";";
		}
		if (isHighlighted != null) {
			csv += "\"Markiert:\";";
		}
		csv += LINE_SEPERATOR;
		for (int i = 0; i < dataCounter; i++) {
			csv += "\"" + (time[i] - time[0]) + "\";";
			for (float f : data[i]) {
				csv += "\"" + Float.toString(f).replaceAll("\\.", ",") + "\";";
			}
			if (isHighlighted != null) {
				if (isHighlighted[i]) {
					csv += "\"x\";";
				} else {
					csv += "\"\";";
				}
			}
			csv += LINE_SEPERATOR;
		}
		return csv;
	}

}
