package com.deeps.sensormax.controller.fragments.measurementinfo;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.MyFragmentManager;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class ThinkGearMeasurementInfoFragment extends MeasurementInfoFragment {

	public ThinkGearMeasurementInfoFragment(
			DataHandlerActivity dataHandlerActivity,
			MyFragmentManager myFragmentManager) {
		super(dataHandlerActivity, myFragmentManager);
		title = myFragmentManager.getThinkGearFragment().getTitle();
		initAdditionalInformation();
	}

	private void initAdditionalInformation() {
		additionalInformation = dataHandlerActivity
				.getString(R.string.infotext_think_gear);
	}

}
