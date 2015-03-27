package com.deeps.sensormax.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.FileManager;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.Measurement;

/**
 * @author Deeps
 */

public class FileController {

	private FileManager fileManager;
	private DataHandlerActivity dataHandlerActivity;

	public FileController(DataHandlerActivity dataHandlerActivity,
			FileManager fileManager) {
		this.dataHandlerActivity = dataHandlerActivity;
		this.fileManager = fileManager;
	}

	public void showSaveMeasurementDialog(final Measurement measurement) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				dataHandlerActivity);

		alertDialog.setTitle(dataHandlerActivity
				.getString(R.string.save_measurement_dialog_title));
		alertDialog.setMessage(dataHandlerActivity
				.getString(R.string.save_measurement_dialog_content));

		final EditText fileNameEditText = new EditText(dataHandlerActivity);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(50, 50, 50, 50);
		fileNameEditText.setLayoutParams(params);

		LinearLayout linearLayout = new LinearLayout(dataHandlerActivity);
		linearLayout.addView(fileNameEditText);

		alertDialog.setView(linearLayout);

		alertDialog.setPositiveButton(
			dataHandlerActivity.getString(R.string.save),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					fileManager.saveMeasurement(measurement, fileNameEditText
							.getText().toString());

				}
			});

		alertDialog.setNegativeButton(
			dataHandlerActivity.getString(R.string.cancel),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			});

		alertDialog.show();
	}

}
