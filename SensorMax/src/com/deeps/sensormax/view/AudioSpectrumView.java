package com.deeps.sensormax.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class AudioSpectrumView extends ImageView {

	private int spectrumScale, startIndex = -1, endIndex = -1;
	private float barWidth;
	private float[][] currentTransform = null;
	private Paint paint;

	public AudioSpectrumView(Context context, int spectrumScale) {
		super(context);
		this.spectrumScale = spectrumScale;
		paint = new Paint();
		paint.setColor(((DataHandlerActivity) getContext()).getResources()
				.getColor(R.color.series3));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setMinimumHeight(w);
		barWidth = (float) w / (float) spectrumScale;
		if (oldw == 0 && oldh == 0) {
			reset();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (startIndex != -1 && endIndex != -1 && currentTransform != null) {
			for (int i = startIndex; i < endIndex; i++) {
				int x = i;
				int downy = (int) (getHeight() / 2 - (currentTransform[0][i] * 10));
				int upy = getHeight() / 2;
				canvas.drawLine(
					x * barWidth,
					downy,
					(x + 1) * barWidth,
					upy,
					paint);
			}
		}
	}

	public void updateData(int startIndex, int endIndex, float[]... transform) {
		reset();
		currentTransform = transform;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		postInvalidate();
	}

	public void reset() {
		startIndex = -1;
		endIndex = -1;
		currentTransform = null;
		postInvalidate();
	}

}
