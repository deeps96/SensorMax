package com.deeps.sensormax.model;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.deeps.sensormax.controller.fragments.menu.ToneGeneratorFragment;

/**
 * @author Deeps
 */

public class MyToneGenerator implements Runnable {

	/*
	 * http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-
	 * android
	 */

	private final int sampleRate = 44100,
			channelConfiguration = AudioFormat.CHANNEL_OUT_MONO,
			audioFormat = AudioFormat.ENCODING_PCM_16BIT,
			streamType = AudioManager.STREAM_MUSIC,
			audioMode = AudioTrack.MODE_STREAM;

	private AudioTrack audioTrack;
	private boolean isPlaying, isInterval;
	private int currentFrequency, durationInMS, startFrequency, endFrequency,
			numSamples;
	private long startTime;

	private ToneGeneratorFragment toneGeneratorFragment;

	@Override
	public void run() {
		numSamples = (int) Math.round(sampleRate * 0.1);
		audioTrack = new AudioTrack(streamType, sampleRate,
				channelConfiguration, audioFormat, AudioTrack.getMinBufferSize(
					sampleRate,
					channelConfiguration,
					audioFormat), audioMode);
		startTime = System.currentTimeMillis();
		while (isPlaying && !isTimeLimitExceeded()) {
			if (isInterval) {
				currentFrequency = startFrequency
						+ (int) (Math
								.round(((double) (System.currentTimeMillis() - startTime) / (double) (durationInMS))
										* (endFrequency - startFrequency)));
			} else {
				currentFrequency = startFrequency;
			}

			double[] sample = new double[numSamples];
			for (int i = 0; i < numSamples; ++i) {
				sample[i] = Math.sin(2 * Math.PI * i
						/ (sampleRate / currentFrequency));
			}
			int idx = 0;

			byte generatedSound[] = new byte[2 * numSamples];
			for (double dVal : sample) {
				short val = (short) (dVal * 32767);
				generatedSound[idx++] = (byte) (val & 0x00ff);
				generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
			}
			audioTrack.write(generatedSound, 0, numSamples);
			audioTrack.play();
		}
		if (toneGeneratorFragment != null) {
			toneGeneratorFragment.generatingStopped();
		}
	}

	public void startGenerating() {
		new Thread(this).start();
		isPlaying = true;
	}

	public void stopGenerating() {
		isPlaying = false;
	}

	private boolean isTimeLimitExceeded() {
		return System.currentTimeMillis() - startTime >= durationInMS;
	}

	// Getter & Setter

	public void setIsInterval(boolean isInterval) {
		this.isInterval = isInterval;
	}

	public void setStartFrequency(int startFrequency) {
		this.startFrequency = startFrequency;
	}

	public void setEndFrequency(int endFrequency) {
		this.endFrequency = endFrequency;
	}

	public void setDuration(int durationInS) {
		this.durationInMS = durationInS;
	}

	public void setToneGeneratorFragment(
			ToneGeneratorFragment toneGeneratorFragment) {
		this.toneGeneratorFragment = toneGeneratorFragment;
	}

}
