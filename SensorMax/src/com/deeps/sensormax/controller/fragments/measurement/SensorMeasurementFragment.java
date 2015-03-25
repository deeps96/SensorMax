package com.deeps.sensormax.controller.fragments.measurement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Point;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.interfaces.UICodeWrapper;
import com.deeps.sensormax.model.measurement.SensorMeasurement;
import com.deeps.sensormax.model.sensors.MySensor;
import com.deeps.sensormax.view.MyLineGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.GraphViewStyle.GridStyle;

/**
 * @author Deeps
 */

public class SensorMeasurementFragment extends LocalMeasurementFragment {

	private final String TAG = getClass().getName();

	private boolean[] showGraphViewSeries;
	private float maxValue, minValue;
	private int maxGraphViewShowTime;
	private int[] seriesColors;
	private long lastTimeStamp;
	private TextView textView1, textView2, textView3;

	private MyLineGraphView graphView;
	private GraphViewSeries[] graphViewSeries;
	private SensorMeasurement sensorMeasurement;
	private MySensor sensor;

	public SensorMeasurementFragment() {
		super();
	}

	public SensorMeasurementFragment(DataHandlerActivity dataHandlerActivity,
			SensorMeasurement sensorMeasurement) {
		super(dataHandlerActivity, sensorMeasurement);
		this.sensorMeasurement = sensorMeasurement;
		this.sensor = sensorMeasurement.getSensor();
		this.title = sensor.getName();
		maxGraphViewShowTime = dataHandlerActivity.getMyConfig()
				.getMaxGraphViewShowTime();
	}

	@Override
	public void onMeasuringStopped() {
		waitTillUITasksAreDoneAndExecuteUICode(new UICodeWrapper() {
			@Override
			public void runUICode() {
				if (dataHandlerActivity.getMyConfig().isShowWholeGraphAtEnd()) {
					graphView.setViewPort(0, lastTimeStamp);
					graphView.redrawAll();
				} else {
					resetView();
				}
				if (dataHandlerActivity.getMyConfig().isShowSummaryAtEnd()) {
					showSummaryDialog();
				}
			}
		});
	}

	private void showSummaryDialog() {
		if (sensorMeasurement.getData() != null
				&& sensorMeasurement.getDataCounter() > 0) {
			sensorMeasurement.calculateAverage();
			final float[] average = sensorMeasurement.getAverage();
			final float[] min = sensorMeasurement.getMin();
			final float[] max = sensorMeasurement.getMax();
			final int[] time = sensorMeasurement.getTime();

			String averageText = "<b>Mittelwert in ["
					+ sensor.getMeasuringUnit() + "]:</b><br /><br />";
			for (int i = 0; i < sensor.getAxisLabels().length; i++)
				averageText += "<i>" + sensor.getAxisLabels()[i]
						+ ":</i>&emsp;&emsp;" + Float.toString(average[i])
						+ "<br />";

			averageText += "<br /><b>Minimum in [" + sensor.getMeasuringUnit()
					+ "]:</b><br /><br />";
			for (int i = 0; i < sensor.getAxisLabels().length; i++)
				averageText += "<i>" + sensor.getAxisLabels()[i]
						+ ":</i>&emsp;&emsp;" + Float.toString(min[i])
						+ "<br />";

			averageText += "<br /><b>Maximum in [" + sensor.getMeasuringUnit()
					+ "]:</b><br /><br />";
			for (int i = 0; i < sensor.getAxisLabels().length; i++)
				averageText += "<i>" + sensor.getAxisLabels()[i]
						+ ":</i>&emsp;&emsp;" + Float.toString(max[i])
						+ "<br />";

			averageText += "<br /></br ><b>Messzeit in [ms]:</b>&emsp;&emsp;&emsp;"
					+ time[sensorMeasurement.getDataCounter() - 1]
					+ " ms<br />";

			AlertDialog dialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					dataHandlerActivity);
			builder.setMessage(Html.fromHtml(averageText));
			builder.setTitle(dataHandlerActivity.getString(R.string.overview));
			builder.setPositiveButton(
				dataHandlerActivity.getString(R.string.close),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
			if (dataHandlerActivity.getMyConfig()
					.isLiveStreamWellConfigurated()) {
				builder.setNeutralButton(
					dataHandlerActivity.getString(R.string.send),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dataHandlerActivity
									.getLiveStreamManager()
									.sendSummaryData(
										measurement.getTitle(),
										min,
										max,
										average,
										time[sensorMeasurement.getDataCounter() - 1],
										System.currentTimeMillis(),
										((ToggleButton) view
												.findViewById(R.id.enableLiveStreamToggleButton))
												.isChecked());
						}
					});
			}
			dialog = builder.create();
			dialog.show();
		}
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		updateMeasuredValuesTextViews(new float[sensor.getAxisLabels().length]);
	}

	@Override
	protected void initMeasuredValuesTextViews() {
		textView1 = (TextView) view.findViewById(R.id.liveStreamTextView);
		textView1.setTextColor(dataHandlerActivity.getResources().getColor(
			R.color.series1));
		textView2 = (TextView) view.findViewById(R.id.addressTextView);
		textView2.setTextColor(dataHandlerActivity.getResources().getColor(
			R.color.series2));
		textView3 = (TextView) view.findViewById(R.id.textView3);
		textView3.setTextColor(dataHandlerActivity.getResources().getColor(
			R.color.series3));
	}

	@Override
	protected void initTriggerEditTexts() {
		final EditText[] triggerEditTexts = new EditText[3];
		triggerEditTexts[0] = (EditText) view
				.findViewById(R.id.triggerEditText1);
		triggerEditTexts[1] = (EditText) view
				.findViewById(R.id.triggerEditText2);
		triggerEditTexts[2] = (EditText) view
				.findViewById(R.id.triggerEditText3);

		if (sensor.getAxisLabels().length == 1) {
			triggerEditTexts[1].setVisibility(View.GONE);
			triggerEditTexts[2].setVisibility(View.GONE);
		}
		for (int i = 0; i < sensor.getAxisLabels().length; i++) {
			final int currentID = i;
			triggerEditTexts[currentID]
					.setHint(sensor.getAxisLabels()[currentID]);
			triggerEditTexts[currentID]
					.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
						}

						@Override
						public void afterTextChanged(Editable s) {
							try {
								if (triggerEditTexts[currentID].getText()
										.toString().length() > 0) {
									sensorMeasurement.setTriggerValue(
										currentID,
										Integer.parseInt(triggerEditTexts[currentID]
												.getText().toString()));
								}
							} catch (NumberFormatException e) {
								Log.e(TAG, e.getMessage());
							}
						}
					});
		}
	}

	@Override
	public void resetView() {
		resetGraphViewSeries();
	}

	@Override
	public void update(final float[] data, final long time) {
		super.update(data, time);
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateMeasuredValuesTextViews(data);
				updateGraphView(data, time);
				uiTasks--;
			}
		});
	}

	private void updateGraphView(float[] data, long time) {
		for (int i = 0; i < data.length; i++) {
			graphViewSeries[i].appendData(
				new GraphViewData(time, data[i]),
				false,
				sensorMeasurement.getMaxDataCounter());
		}
		lastTimeStamp = time;
		adjustYAxisBounds();
		graphView.setViewPort(
			Math.max(0, time - maxGraphViewShowTime),
			Math.min(time, maxGraphViewShowTime));
		graphView.redrawAll();
	}

	private void adjustYAxisBounds() {
		maxValue = -Float.MAX_VALUE;
		minValue = Float.MAX_VALUE;
		if (dataHandlerActivity.getMyConfig().isUseAutoScaling()) {
			for (int i = 0; i < showGraphViewSeries.length; i++) {
				if (showGraphViewSeries[i]) {
					maxValue = Math
							.max(maxValue, sensorMeasurement.getMax()[i]);
					minValue = Math
							.min(minValue, sensorMeasurement.getMin()[i]);
				}
			}
			graphView.setManualYAxisBounds(maxValue, minValue);
		}
	}

	private void updateMeasuredValuesTextViews(float[] data) {
		if (data.length == 1) {
			textView3.setText(data[0] + " " + sensor.getAxisLabels()[0]);
		} else {
			textView1
					.setText(Double.toString(Math.round(data[0] * 100.0) / 100.0));
			textView2
					.setText(Double.toString(Math.round(data[1] * 100.0) / 100.0));
			textView3
					.setText(Double.toString(Math.round(data[2] * 100.0) / 100.0));
		}

	}

	@Override
	protected void initCheckBoxGroup() {
		CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.CheckBox1);
		CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.CheckBox2);
		CheckBox checkBox3 = (CheckBox) view.findViewById(R.id.CheckBox3);
		if (sensor.getAxisLabels().length == 1) {
			checkBox1.setVisibility(View.INVISIBLE);
			checkBox2.setVisibility(View.INVISIBLE);
			checkBox3.setText(sensor.getAxisLabels()[0]);
			checkBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					setShowGraphViewSerie(0, isChecked);
				}
			});
		} else {
			checkBox1.setText(sensor.getAxisLabels()[0]);
			checkBox2.setText(sensor.getAxisLabels()[1]);
			checkBox3.setText(sensor.getAxisLabels()[2]);

			checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					setShowGraphViewSerie(0, isChecked);
				}
			});
			checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					setShowGraphViewSerie(1, isChecked);
				}
			});
			checkBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					setShowGraphViewSerie(2, isChecked);
				}
			});
		}
	}

	@Override
	protected void initCustomView() {
		graphView = new MyLineGraphView(dataHandlerActivity, "");
		customView = graphView;
		GraphViewStyle graphViewStyle = graphView.getGraphViewStyle();
		graphViewStyle.setGridStyle(GridStyle.HORIZONTAL);
		graphViewStyle.setVerticalLabelsWidth(100);

		if (!dataHandlerActivity.getMyConfig().isUseAutoScaling()) {
			if (!Float.isNaN(sensor.getMaxValue())) {
				graphView.setManualYAxisBounds(
					sensor.getMaxValue(),
					sensor.getMinValue());
			}
		}
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isXValue) {
				if (isXValue) {
					if (value == lastTimeStamp) {
						return dataHandlerActivity
								.getString(R.string.time_in_ms);
					} else {
						value = Math.round(value);
					}
				} else {
					if ((dataHandlerActivity.getMyConfig().isUseAutoScaling() && value == maxValue)
							|| (value == sensor.getMaxValue())) {
						return sensor.getMeasuringUnit();
					}
				}
				return Double.toString(Math.round(value * 100.0) / 100.0);
			}
		});

		showGraphViewSeries = new boolean[sensor.getAxisLabels().length];
		for (int i = 0; i < showGraphViewSeries.length; i++) {
			showGraphViewSeries[i] = true;
		}
		seriesColors = new int[showGraphViewSeries.length];
		seriesColors[0] = dataHandlerActivity.getResources().getColor(
			R.color.series1);
		if (seriesColors.length == 3) {
			seriesColors[1] = dataHandlerActivity.getResources().getColor(
				R.color.series2);
			seriesColors[2] = dataHandlerActivity.getResources().getColor(
				R.color.series3);
		}

		graphViewSeries = new GraphViewSeries[sensor.getAxisLabels().length];
		for (int i = 0; i < graphViewSeries.length; i++) {
			graphViewSeries[i] = new GraphViewSeries(sensor.getAxisLabels()[i],
					new GraphViewSeriesStyle(seriesColors[i], 1),
					new GraphViewData[] {});
			if (showGraphViewSeries[i]) {
				graphView.addSeries(graphViewSeries[i]);
			}
		}

		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.graphLayout);
		layout.addView(graphView);
	}

	@Override
	protected void goFullscreen() {
		super.goFullscreen();
		graphView.setScalable(true);
		graphView.setScrollable(true);
		graphView.redrawAll();
	}

	@Override
	protected void goNormal() {
		super.goNormal();
		graphView.setScalable(false);
		graphView.setScrollable(false);
		graphView.redrawAll();
	}

	private void resetGraphViewSeries() {
		for (int i = 0; i < graphViewSeries.length; i++) {
			graphViewSeries[i].resetData(new GraphViewData[] {});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		switch (getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				Display display = ((WindowManager) dataHandlerActivity
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int screenHeight = size.y;
				graphView.setMinimumHeight(Math.round(0.8f * screenHeight));
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				graphView.setMinimumHeight(0);
				break;
		}
	}

	// Setter & Getter
	public GraphView getGraphView() {
		return graphView;
	}

	public void setShowGraphViewSerie(int serie, boolean show) {
		showGraphViewSeries[serie] = show;
		if (show) {
			graphView.addSeries(graphViewSeries[serie]);
		} else {
			graphView.removeSeries(graphViewSeries[serie]);
		}
		adjustYAxisBounds();
	}

	public boolean[] getShowGraphViewSeries() {
		return showGraphViewSeries;
	}

}