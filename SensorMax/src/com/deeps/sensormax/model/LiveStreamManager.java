package com.deeps.sensormax.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.widget.Toast;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.measurement.Measurement;
import com.deeps.sensormax.model.measurement.SensorMeasurement;
import com.deeps.sensormax.model.sensors.MySensor;
import com.deeps.sensormax.view.DialogManager;

/**
 * @author Deeps
 */

public class LiveStreamManager implements Runnable {

	private final int DELAY_TIME_IN_MS = 10000;
	private final String TAG = getClass().getName();

	private boolean delay;
	private HttpContext httpContext;
	private Queue<Task> tasks;

	private DataHandlerActivity dataHandlerActivity;
	private Measurement[] groupMembers;

	public LiveStreamManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(
			ClientContext.COOKIE_STORE,
			new BasicCookieStore());
		tasks = new LinkedList<>();
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			Task currentTask = tasks.peek();
			if (currentTask != null) {
				switch (sendPostRequest(currentTask.getPostBody())) {
					case HttpStatus.SC_OK:
						tasks.poll();
						if (currentTask.getType() == Task.TYPE_SUMMARY_LAST) {
							showToast(dataHandlerActivity
									.getString(R.string.overview_sent));
						}
						break;
					case -1:
						showToast(dataHandlerActivity
								.getString(R.string.no_internet_connection_retry_in_delay));
						delay = true;
						break;
				}
			}
			try {
				if (delay) {
					Thread.sleep(DELAY_TIME_IN_MS);
					delay = false;
				} else {
					Thread.sleep(5L);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void showToast(final String text) {
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(dataHandlerActivity, text, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public void connectDeviceToAccount(final String userName,
			final String password) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> postBody = new LinkedList<>();
				postBody.add(new BasicNameValuePair("hash", dataHandlerActivity
						.getMyConfig().getDeviceHash()));
				postBody.add(new BasicNameValuePair("user", userName));
				postBody.add(new BasicNameValuePair("password", password));
				postBody.add(new BasicNameValuePair("connect", "true"));
				int responseCode = sendPostRequest(postBody);
				String response = null;
				switch (responseCode) {
					case HttpStatus.SC_OK:
						response = dataHandlerActivity
								.getString(R.string.response_account_connection_already_set);
						break;
					case HttpStatus.SC_ACCEPTED:
						response = dataHandlerActivity
								.getString(R.string.response_account_connection_created);
						break;
					case HttpStatus.SC_INTERNAL_SERVER_ERROR:
						response = dataHandlerActivity
								.getString(R.string.response_account_connection_internal_server_error);
						break;
					case HttpStatus.SC_UNAUTHORIZED:
						response = dataHandlerActivity
								.getString(R.string.response_account_connection_wrong_password);
						break;
					case HttpStatus.SC_CREATED:
						response = dataHandlerActivity
								.getString(R.string.response_account_and_connection_created);
						break;
					case -1:
						response = dataHandlerActivity
								.getString(R.string.no_internet_connection);
						break;
				}
				DialogManager.showConfirmDialog(dataHandlerActivity, response);
			}
		}).start();
	}

	// SessionManagement
	public void startNewSession() {
		if (groupMembers.length == 0) {
			return;
		}
		endCurrentSession();
		List<NameValuePair> postBody = new LinkedList<>();
		postBody.add(new BasicNameValuePair("hash", dataHandlerActivity
				.getMyConfig().getDeviceHash()));
		for (int iMeasurement = 0; iMeasurement < groupMembers.length; iMeasurement++) {
			if (groupMembers[iMeasurement] instanceof SensorMeasurement) {
				MySensor mySensor = ((SensorMeasurement) groupMembers[iMeasurement])
						.getSensor();
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][type]", mySensor.getName()));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][name]", mySensor.getSensor().getName()));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][vendor]", mySensor.getSensor().getVendor()));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][resolution]", mySensor.getSensor().getResolution()
						+ " " + mySensor.getMeasuringUnit()));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][range]", mySensor.getMinValue() + " "
						+ mySensor.getMeasuringUnit() + " bis "
						+ mySensor.getMaxValue() + " "
						+ mySensor.getMeasuringUnit()));
			} else {
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][type]", groupMembers[iMeasurement].getTitle()));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][name]", "nA"));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][vendor]", "nA"));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][resolution]", "nA"));
				postBody.add(new BasicNameValuePair("sensors[" + iMeasurement
						+ "][range]", "nA"));
			}
		}
		tasks.add(new Task(postBody, Task.TYPE_SESSION));
	}

	public void endCurrentSession() {
		List<NameValuePair> postBody = new LinkedList<>();
		postBody.add(new BasicNameValuePair("logoutRequested", "true"));
		tasks.add(new Task(postBody, Task.TYPE_SESSION));
	}

	public void sendRealTimeData(String measurementTitle, float[] data,
			long time, boolean isHighlighted) {
		tasks.add(new Task(createPostBodyForDataInsertion(
			measurementTitle,
			data,
			time,
			isHighlighted)));
	}

	private List<NameValuePair> createPostBodyForDataInsertion(
			String measurementTitle, float[] data, long time,
			boolean isHighlighted) {
		int sensorID = getSensorIDByMeasurementTitle(measurementTitle);
		List<NameValuePair> postBody = new LinkedList<>();
		postBody.add(new BasicNameValuePair("sensorID", Integer
				.toString(sensorID)));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date resultdate = new Date(time);
		postBody.add(new BasicNameValuePair("timestamp", sdf.format(resultdate)));
		for (int iData = 0; iData < data.length; iData++) {
			postBody.add(new BasicNameValuePair("data[" + iData + "]", Float
					.toString(data[iData])));
		}
		postBody.add(new BasicNameValuePair("highlighted", "true"));
		return postBody;
	}

	private int getSensorIDByMeasurementTitle(String measurementTitle) {
		for (int iMeasurement = 0; iMeasurement < groupMembers.length; iMeasurement++) {
			if (groupMembers[iMeasurement].getTitle().equals(measurementTitle))
				return iMeasurement;
		}
		return -1;
	}

	public void sendSummaryData(String measurementTitle, float[] min,
			float[] max, float[] avg, long measuringTime, long time,
			boolean wasLivestreamEnabled) {
		if (!wasLivestreamEnabled) {
			startNewSession();
		}

		List<NameValuePair> postBody = createPostBodyForDataInsertion(
			measurementTitle,
			min,
			time,
			false);
		postBody.add(new BasicNameValuePair("min", "true"));
		tasks.add(new Task(postBody, Task.TYPE_SUMMARY));

		postBody = createPostBodyForDataInsertion(
			measurementTitle,
			avg,
			time,
			false);
		postBody.add(new BasicNameValuePair("avg", "true"));
		tasks.add(new Task(postBody, Task.TYPE_SUMMARY));

		postBody = createPostBodyForDataInsertion(
			measurementTitle,
			max,
			time,
			false);
		postBody.add(new BasicNameValuePair("max", "true"));
		tasks.add(new Task(postBody, Task.TYPE_SUMMARY_LAST));

		if (!wasLivestreamEnabled) {
			endCurrentSession();
		}
	}

	private int sendPostRequest(List<NameValuePair> postBody) {
		if (!dataHandlerActivity.getMyConfig().isLiveStreamWellConfigurated()) {
			return -1;
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost(dataHandlerActivity
				.getMyConfig().getServerAddress());
		httpPostRequest
				.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");
		httpPostRequest.setHeader(
			"Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPostRequest.setHeader(
			"Accept-Language",
			"de-de, de, en;q=0.5, fr;q=0.2");
		httpPostRequest.setHeader("Connection", "keep-alive");
		try {
			httpPostRequest.setEntity(new UrlEncodedFormEntity(postBody));

			HttpResponse httpResponse = httpClient.execute(
				httpPostRequest,
				httpContext);

			// ResponseHandler<String> responseHandler = new
			// BasicResponseHandler();
			// String responseBody =
			// responseHandler.handleResponse(httpResponse);
			// System.out.println(responseBody);
			// System.out.println(httpResponse.getStatusLine().getStatusCode());

			return httpResponse.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public void setGroupMembers(Measurement[] groupMembers) {
		this.groupMembers = groupMembers;
	}

	private class Task {

		private final static int TYPE_DATA = 0, TYPE_SUMMARY = 1,
				TYPE_SESSION = 2, TYPE_SUMMARY_LAST = 3;

		private List<NameValuePair> postBody;
		private int type;

		public Task(List<NameValuePair> postBody) {
			this(postBody, TYPE_DATA);
		}

		public Task(List<NameValuePair> postBody, int type) {
			this.postBody = postBody;
			this.type = type;
		}

		public List<NameValuePair> getPostBody() {
			return postBody;
		}

		public int getType() {
			return type;
		}
	}

}
