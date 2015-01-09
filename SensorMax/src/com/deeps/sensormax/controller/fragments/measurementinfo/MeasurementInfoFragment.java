package com.deeps.sensormax.controller.fragments.measurementinfo;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.MyFragmentManager;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class MeasurementInfoFragment extends SuperFragment {

	protected boolean showSensorInfoLayout;
	protected float power, resolution, minRange, maxRange;
	protected int version;
	protected String type, vendor, name, unit, additionalInformation;

	protected MyFragmentManager myFragmentManager;

	public MeasurementInfoFragment(DataHandlerActivity dataHandlerActivity,
			MyFragmentManager myFragmentManager) {
		super(dataHandlerActivity);
		this.myFragmentManager = myFragmentManager;
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		if (showSensorInfoLayout) {
			initSensorTypeTextView();
			initVendorTextView();
			initNameTextView();
			initVersionTextView();
			initPowerTextView();
			initResolutionTextView();
			initRangeTextView();
		} else {
			hideSensorInfoLayout();
		}
		initAdditionalSensorInformationTextView();
	}

	@Override
	public boolean onBackPressed() {
		dataHandlerActivity.getGuiManager().changeContentFragment(
			dataHandlerActivity.getMyFragmentManager()
					.getGroupMeasurementFragment());
		return true;
	}

	private void hideSensorInfoLayout() {
		LinearLayout sensorInfoLayout = (LinearLayout) view
				.findViewById(R.id.sensorInfoLayout);
		sensorInfoLayout.setVisibility(View.GONE);
	}

	private void initAdditionalSensorInformationTextView() {
		TextView additionalSensorInformationTextView = (TextView) view
				.findViewById(R.id.additionalSensorInformationTextView);
		Spanned spanned = Html.fromHtml(additionalInformation);
		additionalSensorInformationTextView
				.setMovementMethod(LinkMovementMethod.getInstance());
		additionalSensorInformationTextView.setText(spanned);
	}

	private void initRangeTextView() {
		TextView rangeTextView = (TextView) view
				.findViewById(R.id.rangeTextView);
		rangeTextView.setText(minRange + " " + unit + "  bis  " + maxRange
				+ " " + unit);
	}

	private void initResolutionTextView() {
		TextView resolutionTextView = (TextView) view
				.findViewById(R.id.resolutionTextView);
		resolutionTextView.setText(Float.toString(resolution) + " " + unit);
	}

	private void initPowerTextView() {
		TextView powerTextView = (TextView) view
				.findViewById(R.id.powerTextView);
		powerTextView.setText(Float.toString(power) + " mA");
	}

	private void initVersionTextView() {
		TextView versionTextView = (TextView) view
				.findViewById(R.id.versionTextView);
		versionTextView.setText(Integer.toString(version));
	}

	private void initNameTextView() {
		TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
		nameTextView.setText(name);
	}

	private void initVendorTextView() {
		TextView vendorTextView = (TextView) view
				.findViewById(R.id.vendorTextView);
		vendorTextView.setText(vendor);
	}

	private void initSensorTypeTextView() {
		TextView sensorTypeTextView = (TextView) view
				.findViewById(R.id.sensorTypeTextView);
		sensorTypeTextView.setText(type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.fragment_sensor_info, container, false);
		initUIComponents();
		return view;
	}
}
