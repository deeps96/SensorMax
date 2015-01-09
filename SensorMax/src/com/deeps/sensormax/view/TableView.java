package com.deeps.sensormax.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class TableView extends LinearLayout {

	private final int MAX_CHILD_COUNT = 100;

	private int maxHeight;
	private MyScrollView tableScrollView;
	private String time;
	private String[] header;
	private TableLayout tableLayout;
	private TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();
	private TableRow.LayoutParams colParams = new TableRow.LayoutParams();

	private DataHandlerActivity dataHandlerActivity;

	public TableView(Context context) {
		super(context);
		this.dataHandlerActivity = (DataHandlerActivity) context;
		init();
	}

	private void init() {
		tableLayout = new TableLayout(dataHandlerActivity);
		tableLayout.setStretchAllColumns(true);
		time = dataHandlerActivity.getString(R.string.time_in_ms);
		tableScrollView = new MyScrollView(dataHandlerActivity);
		tableScrollView.setFillViewport(true);
		tableScrollView.addView(tableLayout);
		addView(tableScrollView);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setMinimumHeight(w);
		maxHeight = h;
	}

	private void addHeaderToLayout() {
		if (header == null) {
			return;
		}

		TableRow tableRow = new TableRow(dataHandlerActivity);
		tableRow.setLayoutParams(rowParams);

		for (int i = 0; i < header.length + 1; i++) { // +1 for time
			TextView textView = new TextView(dataHandlerActivity);
			if (i == 0) { // time
				textView.setText(time);
			} else {
				textView.setText(header[i - 1]);
			}
			textView.setGravity(Gravity.CENTER | Gravity.CENTER);
			textView.setLayoutParams(colParams);
			tableRow.addView(textView);
		}

		tableLayout.addView(tableRow);
	}

	public void updateData(float[] data, long time) {
		if (tableLayout.getChildCount() == MAX_CHILD_COUNT) {
			tableLayout.removeView(tableLayout.getChildAt(0));
		}
		TableRow tableRow = new TableRow(dataHandlerActivity);
		tableRow.setLayoutParams(rowParams);

		for (int i = 0; i < data.length + 1; i++) { // +1 for time
			TextView textView = new TextView(dataHandlerActivity);
			if (i == 0) { // time
				textView.setText(Float.toString(time));
			} else {
				textView.setText(Float.toString(data[i - 1]));
			}
			textView.setGravity(Gravity.CENTER | Gravity.CENTER);
			textView.setLayoutParams(colParams);
			tableRow.addView(textView);
		}

		tableLayout.addView(tableRow);
		tableScrollView.scrollTo(0, tableLayout.getHeight());
	}

	public void reset() {
		tableLayout.removeAllViews();
		addHeaderToLayout();
	}

	// Getter & Setter
	public void setHeader(String[] header) {
		this.header = header;
	}

	private class MyScrollView extends ScrollView {

		public MyScrollView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				maxHeight,
				MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

}
