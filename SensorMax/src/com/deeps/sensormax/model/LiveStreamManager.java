package com.deeps.sensormax.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class LiveStreamManager implements Runnable {

	private final String TAG = getClass().getName();

	private Queue<Task> tasks;

	private DataHandlerActivity dataHandlerActivity;

	public LiveStreamManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		tasks = new LinkedList<>();
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			if (!tasks.isEmpty()) {
				Task currentTask = tasks.peek();
				if (sendData(
					currentTask.getUrl(),
					currentTask.isShowConfirmDialog())) {
					tasks.remove();
				}
			}
			try {
				Thread.sleep(5L);
			} catch (InterruptedException e) {
			}
		}

	}

	private boolean sendData(String pUrl, boolean showConfirm) {
		try {
			URL url = new URL(pUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.addRequestProperty(
				"Accept-Language",
				"de,en-US;q=0.7,en;q=0.3");
			connection
					.addRequestProperty(
						"Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				if (showConfirm) {
					showConfirmDialog(dataHandlerActivity
							.getString(R.string.data_successfully_sended));
				}
				Log.i(TAG, "Data successfully sended - " + pUrl);
				return true;
			} else {
				if (showConfirm) {
					showConfirmDialog(dataHandlerActivity
							.getString(R.string.data_not_sent));
				}
				Log.e(
					TAG,
					"Errorcode from Server - " + connection.getResponseCode()
							+ "\n" + pUrl);
			}
		} catch (IOException e) {
			if (showConfirm) {
				showConfirmDialog(dataHandlerActivity
						.getString(R.string.data_not_sent));
			}
			Log.e(TAG, "Error while sending data - " + e.getMessage() + "\n"
					+ pUrl);
		}
		return false;
	}

	public void sendRealTimeData(String sensorName, float[] data, long time,
			boolean isHighlighted) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(dataHandlerActivity.getMyConfig()
				.getLiveStreamURL());
		stringBuilder.append("?deviceid="
				+ dataHandlerActivity.getMyConfig().getDeviceID() + "&sensor="
				+ sensorName + "&highlighted=" + isHighlighted + "&time="
				+ time);
		for (int iData = 0; iData < data.length; iData++) {
			stringBuilder.append("&value" + iData + "=" + data[iData]);
		}
		tasks.add(new Task(stringBuilder.toString(), false));
	}

	public void sendSummaryData(String sensorName, float[] min, float[] max,
			float[] avg, long measuringTime) {
		String phpParams = "?deviceid="
				+ dataHandlerActivity.getMyConfig().getDeviceID() + "&sensor="
				+ sensorName + "&mtime=" + measuringTime;
		for (int iData = 0; iData < min.length; iData++) {
			phpParams += "&min" + iData + "=" + min[iData];
			phpParams += "&max" + iData + "=" + max[iData];
			phpParams += "&avg" + iData + "=" + avg[iData];
		}
		final String urlString = dataHandlerActivity.getMyConfig()
				.getLiveStreamURL() + phpParams;
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendData(urlString, true); // no queue, because we want to send
										   // it NOW
			}
		}).start();

	}

	private void showConfirmDialog(final String text) {
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						dataHandlerActivity);

				TextView messageTextView = new TextView(dataHandlerActivity);
				messageTextView.setTextSize(22);
				SpannableString spanString = new SpannableString(text);
				spanString.setSpan(
					new StyleSpan(Typeface.BOLD),
					0,
					spanString.length(),
					0);
				messageTextView.setText(spanString);
				messageTextView.setGravity(Gravity.CENTER);
				dialog.setView(messageTextView);
				dialog.setPositiveButton(
					dataHandlerActivity.getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
						}
					});
				dialog.show();
			}
		});
	}

	private class Task {
		private boolean showConfirmDialog;
		private String url;

		public Task(String url, boolean showConfirmDialog) {
			this.url = url;
			this.showConfirmDialog = showConfirmDialog;
		}

		public boolean isShowConfirmDialog() {
			return showConfirmDialog;
		}

		public String getUrl() {
			return url;
		}

	}

}
