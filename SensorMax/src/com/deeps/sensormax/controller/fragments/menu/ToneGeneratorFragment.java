package com.deeps.sensormax.controller.fragments.menu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.MyToneGenerator;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class ToneGeneratorFragment extends SuperFragment {

	private final int initProgress = 13000, initDuration = 5;

	private Button startStopButton;
	private EditText startFrequencyEditText, endFrequencyEditText;
	private SeekBar startFrequencySeekBar, endFrequencySeekBar;

	private MyToneGenerator myToneGenerator;

	public ToneGeneratorFragment() {
	}

	public ToneGeneratorFragment(DataHandlerActivity dataHandlerActivity,
			MyToneGenerator toneGenerator) {
		super(dataHandlerActivity);
		this.myToneGenerator = toneGenerator;
		title = dataHandlerActivity.getString(R.string.frequency_generator);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
			R.layout.fragment_tone_generator,
			container,
			false);
		initUIComponents();
		return view;
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initStartStopButton();
		initIntervalCheckBox();
		initSeekBars();
		initEditTexts();
		initDurationEditText();
	}

	private void initEditTexts() {
		startFrequencyEditText = (EditText) view
				.findViewById(R.id.startFrequencyEditText);
		endFrequencyEditText = (EditText) view
				.findViewById(R.id.endFrequencyEditText);
		startFrequencyEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (startFrequencyEditText.getText().toString().length() > 0) {
					try {
						int frequency = Integer.parseInt(startFrequencyEditText
								.getText().toString());
						if (frequency >= 1
								&& frequency <= startFrequencySeekBar.getMax()) {
							myToneGenerator.setStartFrequency(frequency);
							startFrequencySeekBar.setProgress(frequency);
						} else {
							startFrequencyEditText.setText(Integer
									.toString(initProgress));
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		});
		startFrequencyEditText.setText(Integer.toString(initProgress));
		endFrequencyEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (endFrequencyEditText.getText().toString().length() > 0) {
					try {
						int frequency = Integer.parseInt(endFrequencyEditText
								.getText().toString());
						if (frequency >= 1
								&& frequency <= endFrequencySeekBar.getMax()) {
							myToneGenerator.setEndFrequency(frequency);
							endFrequencySeekBar.setProgress(frequency);
						} else {
							endFrequencyEditText.setText(Integer
									.toString(initProgress));
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	private void initDurationEditText() {
		final EditText durationEditText = (EditText) view
				.findViewById(R.id.durationEditText);
		durationEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (durationEditText.getText().toString().length() > 0) {
					try {
						myToneGenerator.setDuration(Integer
								.parseInt(durationEditText.getText().toString()));
					} catch (NumberFormatException e) {
					}
				}
			}
		});
		durationEditText.setText(Integer.toString(initDuration));
	}

	private void initSeekBars() {
		endFrequencySeekBar = (SeekBar) view
				.findViewById(R.id.endFrequencySeekBar);
		startFrequencySeekBar = (SeekBar) view
				.findViewById(R.id.startFrequencySeekBar);
		startFrequencySeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (progress == 0) {
							progress = 1;
							startFrequencySeekBar.setProgress(progress);
						}
						if (fromUser) {
							startFrequencyEditText.setText(Integer
									.toString(progress));
						}
					}
				});
		endFrequencySeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (progress == 0) {
							progress = 1;
							endFrequencySeekBar.setProgress(progress);
						}
						if (fromUser) {
							endFrequencyEditText.setText(Integer
									.toString(progress));
						}
					}
				});
	}

	private void initIntervalCheckBox() {
		CheckBox intervalCheckBox = (CheckBox) view
				.findViewById(R.id.isFrequencyIntervalCheckBox);
		final LinearLayout endFrequencyLayout = (LinearLayout) view
				.findViewById(R.id.endFrequencyLayout);
		final SeekBar endFrequencySeekBar = (SeekBar) view
				.findViewById(R.id.endFrequencySeekBar);
		final SeekBar startFrequencySeekBar = (SeekBar) view
				.findViewById(R.id.startFrequencySeekBar);
		intervalCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myToneGenerator.setIsInterval(isChecked);
						if (isChecked) {
							endFrequencySeekBar
									.setProgress(startFrequencySeekBar
											.getProgress());
							AlphaAnimation alphaAnimation = new AlphaAnimation(
									0.0f, 1.0f);
							alphaAnimation.setDuration(500);
							alphaAnimation
									.setAnimationListener(new AnimationListener() {
										@Override
										public void onAnimationStart(
												Animation animation) {
											endFrequencyLayout
													.setVisibility(View.VISIBLE);
										}

										@Override
										public void onAnimationRepeat(
												Animation animation) {
										}

										@Override
										public void onAnimationEnd(
												Animation animation) {
										}
									});
							endFrequencyLayout.startAnimation(alphaAnimation);
						} else {
							AlphaAnimation alphaAnimation = new AlphaAnimation(
									1.0f, 0.0f);
							alphaAnimation.setDuration(400);
							alphaAnimation
									.setAnimationListener(new AnimationListener() {
										@Override
										public void onAnimationStart(
												Animation animation) {
										}

										@Override
										public void onAnimationRepeat(
												Animation animation) {
										}

										@Override
										public void onAnimationEnd(
												Animation animation) {
											endFrequencyLayout
													.setVisibility(View.GONE);
										}
									});
							endFrequencyLayout.startAnimation(alphaAnimation);
						}
					}
				});
	}

	private void initStartStopButton() {
		startStopButton = (Button) view.findViewById(R.id.startStopButton);
		startStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (startStopButton.getText().toString()
						.equals(dataHandlerActivity.getString(R.string.start))) {
					myToneGenerator.startGenerating();
					startStopButton.setText(dataHandlerActivity
							.getString(R.string.stop));
				} else {
					myToneGenerator.stopGenerating();
				}
			}
		});
	}

	public void generatingStopped() {
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				startStopButton.setText(dataHandlerActivity
						.getString(R.string.start));
			}
		});

	}

}
