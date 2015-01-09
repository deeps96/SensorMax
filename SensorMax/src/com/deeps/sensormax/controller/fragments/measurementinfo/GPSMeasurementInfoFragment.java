package com.deeps.sensormax.controller.fragments.measurementinfo;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.MyFragmentManager;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class GPSMeasurementInfoFragment extends MeasurementInfoFragment {

	public GPSMeasurementInfoFragment(DataHandlerActivity dataHandlerActivity,
			MyFragmentManager myFragmentManager) {
		super(dataHandlerActivity, myFragmentManager);
		title = myFragmentManager.getGPSMeasuringFragment().getTitle();
		initAdditionalInformation();
	}

	private void initAdditionalInformation() {
		additionalInformation = dataHandlerActivity
				.getString(R.string.infotext_gps);
	}

}
