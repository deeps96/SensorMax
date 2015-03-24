package com.deeps.sensormax.model.measurement;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import ca.uol.aig.fftpack.RealDoubleFFT;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.measurement.AudioMeasurementFragment;
import com.deeps.sensormax.model.Utils;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class AudioMeasurement extends Measurement {

	public static final int FREQUENCY_INDEX = 0, DECIBLE_INDEX = 1,
			EXTRA_INFORMATION_COUNT = 2, BLOCK_SIZE = 1050;
	private final int sampleRate = 44100,
			channelConfiguration = AudioFormat.CHANNEL_IN_MONO,
			audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private final String TAG = getClass().getName();

	private boolean isListening;

	private RealDoubleFFT realDoubleFFT;
	private RecordAudio recordTask;
	private AudioMeasurementFragment audioMeasurementFragment;

	public AudioMeasurement(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		initialise();
	}

	@Override
	public void initialise() {
		super.initialise();
		realDoubleFFT = new RealDoubleFFT(BLOCK_SIZE);
	}

	@Override
	protected boolean isTriggerReleased(float[] modifiedData) {
		if (!isTriggerActive)
			return true;
		if (!isTriggerReleased) {
			boolean release = true;
			for (int i = 0; i < EXTRA_INFORMATION_COUNT; i++) {
				if (modifiedData[BLOCK_SIZE + i] < triggerValues[BLOCK_SIZE + i]) {
					release = false;
					break;
				}
			}
			isTriggerReleased = release;
		}
		return isTriggerReleased;
	}

	@Override
	public void zeroing() {
		for (int i = 0; i < zeroing.length - EXTRA_INFORMATION_COUNT; i++) {
			if (i == BLOCK_SIZE + DECIBLE_INDEX && zeroing[i] == 0.0f) {
				zeroing[i] += recentDataSet[i] - 25.0f; // reset to 25 db, not 0
			} else {
				zeroing[i] += recentDataSet[i] - zeroing[i];
			}
		}
	}

	@Override
	protected void updateUIValues(float[] modifiedData, int time) {
		audioMeasurementFragment.update(modifiedData, time);
	}

	@Override
	public String getCSV() {
		float subFrequency = sampleRate / BLOCK_SIZE;
		String[] header = new String[BLOCK_SIZE + EXTRA_INFORMATION_COUNT];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			header[i] = Float.toString(subFrequency * i);
		}
		header[BLOCK_SIZE + FREQUENCY_INDEX] = dataHandlerActivity
				.getString(R.string.frequency);
		header[BLOCK_SIZE + DECIBLE_INDEX] = dataHandlerActivity
				.getString(R.string.decible);

		return Utils.convertToCSV(
			header,
			data,
			time,
			highlightedMeasuringValues,
			dataCounter);
	}

	@Override
	protected int getAxisCount() {
		return BLOCK_SIZE + EXTRA_INFORMATION_COUNT; // 2 ... db and frequency
	}

	@Override
	protected void registerListener() {
		isListening = true;
		recordTask = new RecordAudio();
		recordTask.execute();
	}

	@Override
	protected void unregisterListener() {
		isListening = false;
		if (recordTask != null) {
			recordTask.cancel(true);
		}
	}

	private class RecordAudio extends AsyncTask<Void, double[], Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				int bufferSize = AudioRecord.getMinBufferSize(
					sampleRate,
					channelConfiguration,
					audioEncoding);

				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, sampleRate,
						channelConfiguration, audioEncoding, bufferSize);

				short[] buffer = new short[BLOCK_SIZE];
				double[] toTransform = new double[BLOCK_SIZE];

				audioRecord.startRecording();

				/*
				 * one loop contains the full sample rate - frequency | the
				 * blockSize determinates, how big the steps are
				 * 
				 * example: 1 * 44100 / 1024 = 43.1 Hz (with 1024 blocksize,
				 * 44100 sample rate, an index 1)
				 */

				while (isListening) {
					int bufferReadResult = audioRecord.read(
						buffer,
						0,
						BLOCK_SIZE);
					double average = 0, count = BLOCK_SIZE;
					for (int i = 0; i < BLOCK_SIZE && i < bufferReadResult; i++) {
						if (buffer[i] > 0) {
							average += buffer[i];
						} else {
							count--;
						}
						toTransform[i] = buffer[i] / 32768.0; // signed
															  // 16
					} // bit --> 2^16 / 2 + 1 (/ 2 --> neg/pos)
					realDoubleFFT.ft(toTransform);
					double magnitude[] = new double[BLOCK_SIZE / 2];

					for (int i = 0; i < magnitude.length; i++) {
						double R = toTransform[2 * i] * toTransform[2 * i];
						double I = toTransform[2 * i + 1]
								* toTransform[2 * i * 1];

						magnitude[i] = Math.sqrt(I + R);
					}
					int maxIndex = 0;
					double max = magnitude[0];
					for (int i = 1; i < magnitude.length; i++) {
						if (magnitude[i] > max) {
							max = magnitude[i];
							maxIndex = i;
						}
					}
					double[] data = new double[BLOCK_SIZE
							+ EXTRA_INFORMATION_COUNT];
					System.arraycopy(toTransform, 0, data, 0, BLOCK_SIZE);
					// dominant frequency
					data[BLOCK_SIZE + FREQUENCY_INDEX] = maxIndex * sampleRate
							/ (double) BLOCK_SIZE;
					/*
					 * http://stackoverflow.com/questions/10655703/what-does-
					 * androids
					 * -getmaxamplitude-function-for-the-mediarecorder-actually
					 * -gi
					 */

					// db
					data[BLOCK_SIZE + DECIBLE_INDEX] = 20 * Math
							.log10(((average / count) / 51805.5336) / 0.00002);
					publishProgress(data);
				}

				audioRecord.stop();

			} catch (Throwable t) {
				t.printStackTrace();
				Log.e(TAG, "Recording Failed");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(double[]... toTransform) {
			recentDataSet = new float[toTransform[0].length];
			for (int i = 0; i < recentDataSet.length; i++) {
				recentDataSet[i] = (float) toTransform[0][i];
			}
		}
	}

	// Getter & Setter
	public void setAudioMeasurementFragment(
			AudioMeasurementFragment audioMeasurementFragment) {
		this.audioMeasurementFragment = audioMeasurementFragment;
		this.localMeasurementFragment = audioMeasurementFragment;
	}

}
