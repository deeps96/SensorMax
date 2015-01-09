package com.deeps.sensormax.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class SatelliteCounterView extends LinearLayout {

	private Drawable graySatelliteDrawable, greenSatelliteDrawable;
	private ImageView[] imageViews;

	private DataHandlerActivity dataHandlerActivity;

	public SatelliteCounterView(Context context) {
		super(context);
		this.dataHandlerActivity = (DataHandlerActivity) context;
		graySatelliteDrawable = dataHandlerActivity.getResources().getDrawable(
			R.drawable.ic_gps_gray);
		greenSatelliteDrawable = dataHandlerActivity.getResources()
				.getDrawable(R.drawable.ic_gps_green);
		initImages();
	}

	private void initImages() {
		LinearLayout rootLayout = new LinearLayout(dataHandlerActivity);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		LinearLayout[] linearLayouts = new LinearLayout[4];
		imageViews = new ImageView[12];

		for (int layoutID = 0; layoutID < linearLayouts.length; layoutID++) {
			linearLayouts[layoutID] = new LinearLayout(dataHandlerActivity);
			linearLayouts[layoutID].setOrientation(LinearLayout.HORIZONTAL);
			linearLayouts[layoutID]
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, 1.0f));
			for (int imageID = 0; imageID < 3; imageID++) {
				imageViews[layoutID * 3 + imageID] = new ImageView(
						dataHandlerActivity);
				imageViews[layoutID * 3 + imageID]
						.setImageDrawable(graySatelliteDrawable);
				imageViews[layoutID * 3 + imageID]
						.setLayoutParams(new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 1.0f));
				linearLayouts[layoutID].addView(imageViews[layoutID * 3
						+ imageID]);
			}
			rootLayout.addView(linearLayouts[layoutID]);
		}
		removeAllViews();
		addView(rootLayout);
		postInvalidate();
	}

	public void reset() {
		updateData(0);
	}

	public void updateData(int satellitesAvailable) {
		for (int i = 0; i < imageViews.length; i++) {
			if (i < satellitesAvailable) {
				imageViews[i].setImageDrawable(greenSatelliteDrawable);
			} else {
				imageViews[i].setImageDrawable(graySatelliteDrawable);
			}
		}
		postInvalidate();
	}
}
