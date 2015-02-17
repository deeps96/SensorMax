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
		numSamples = Math.round(sampleRate);
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
			byte generatedSnd[] = new byte[2 * numSamples];

			for (int i = 0; i < numSamples; ++i) { // Fill the sample array
				sample[i] = Math.sin(currentFrequency * 2 * Math.PI * i
						/ (sampleRate));
			}

			// convert to 16 bit pcm sound array
			// assumes the sample buffer is normalized.
			// convert to 16 bit pcm sound array
			// assumes the sample buffer is normalised.
			int idx = 0;
			int i = 0;

			int ramp = numSamples / 20; // Amplitude ramp as a percent of
										// sample
										// count

			for (i = 0; i < ramp; ++i) { // Ramp amplitude up (to avoid clicks)
				double dVal = sample[i];
				// Ramp up to maximum
				final short val = (short) ((dVal * 32767 * i / ramp));
				// in 16 bit wav PCM, first byte is the low order byte
				generatedSnd[idx++] = (byte) (val & 0x00ff);
				generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			}

			for (; i < numSamples - ramp; ++i) { // Max amplitude for most
												 // of the samples
				double dVal = sample[i];
				// scale to maximum amplitude
				final short val = (short) ((dVal * 32767));
				// in 16 bit wav PCM, first byte is the low order byte
				generatedSnd[idx++] = (byte) (val & 0x00ff);
				generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			}

			for (; i < numSamples; ++i) { // Ramp amplitude down
				double dVal = sample[i];
				// Ramp down to zero
				final short val = (short) ((dVal * 32767 * (numSamples - i) / ramp));
				// in 16 bit wav PCM, first byte is the low order byte
				generatedSnd[idx++] = (byte) (val & 0x00ff);
				generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			}
			try {
				audioTrack.write(generatedSnd, 0, generatedSnd.length);
				audioTrack.play();
			} catch (IllegalStateException e) {
			}
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
		audioTrack.flush();
		audioTrack.stop();
		audioTrack.release();
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
		this.durationInMS = durationInS * 1000;
	}

	public void setToneGeneratorFragment(
			ToneGeneratorFragment toneGeneratorFragment) {
		this.toneGeneratorFragment = toneGeneratorFragment;
	}

}
