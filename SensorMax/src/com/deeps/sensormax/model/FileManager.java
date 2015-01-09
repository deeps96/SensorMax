package com.deeps.sensormax.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.FileController;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class FileManager {

	private final String DIR_NAME = "messungen", TAG = getClass().getName(),
			APP_CONFIG_FILE_NAME = "appconfig.txt";

	private String rootDir;

	private FileController fileController;
	private DataHandlerActivity dataHandlerActivity;

	public FileManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		rootDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + dataHandlerActivity.getString(R.string.app_name);
		new File(rootDir).mkdirs();
		fileController = new FileController(dataHandlerActivity, this);
	}

	public File saveAppFile(String filePath, String content,
			boolean isFilePathAbsolute, boolean showConfirm) {
		String root = filePath;
		if (!isFilePathAbsolute) {
			root = rootDir + "/" + filePath;
		}
		File file = new File(root);
		if (file.exists()) {
			if (showConfirm) {
				Toast.makeText(
					dataHandlerActivity,
					dataHandlerActivity
							.getString(R.string.info_file_already_existing),
					Toast.LENGTH_SHORT).show();
			}
			return null;
		} else {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(file);
				writer.write(content);
				writer.flush();
				if (showConfirm) {
					Toast
							.makeText(
								dataHandlerActivity,
								dataHandlerActivity
										.getString(R.string.info_save_action_successful),
								Toast.LENGTH_SHORT).show();
				}
				writer.close();
				dataHandlerActivity.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.fromFile(file))); // makes data visible threw
												   // mtp
				return file;
			} catch (FileNotFoundException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		if (showConfirm) {
			Toast.makeText(
				dataHandlerActivity,
				dataHandlerActivity
						.getString(R.string.info_save_action_unsuccessful),
				Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	public boolean saveMeasurement(String relativeFilePath, String content) {
		String root = rootDir + "/" + DIR_NAME;
		File dirs = new File(root
				+ "/"
				+ relativeFilePath.substring(
					0,
					relativeFilePath.lastIndexOf("/")));
		dirs.mkdirs();
		return saveAppFile(root + "/" + relativeFilePath, content, true, true) != null;
	}

	public FileController getFileController() {
		return fileController;
	}

	public String loadFile(InputStream inputStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = null, content = "";
			while ((line = reader.readLine()) != null) {
				content += line + "\r\n";
			}
			reader.close();
			return content;
		} catch (IOException e) {
			Log.e(TAG, "Could not load the demanded file ");
		}
		return null;
	}

	public String loadFile(File file) {
		try {
			return loadFile(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			Log.e(
				TAG,
				"Could not load the demanded file " + file.getAbsolutePath());
		}
		return null;
	}

	public File getAppConfigFile() {
		return new File(rootDir, APP_CONFIG_FILE_NAME);
	}
}
