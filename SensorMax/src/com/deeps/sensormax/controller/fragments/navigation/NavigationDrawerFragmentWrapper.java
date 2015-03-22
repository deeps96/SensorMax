package com.deeps.sensormax.controller.fragments.navigation;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.measurement.GroupMeasurementFragment;
import com.deeps.sensormax.controller.fragments.measurement.LocalMeasurementFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class NavigationDrawerFragmentWrapper extends NavigationDrawerFragment {

	private final int RQ_SHARE = 1001;

	private File lastSharedFile;

	private DataHandlerActivity dataHandlerActivity;

	public NavigationDrawerFragmentWrapper() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initNavigationMenuTitles();
	}

	private void initNavigationMenuTitles() {
		navigationMenu = new String[] { getString(R.string.home),
				getString(R.string.sensor_overview),
				getString(R.string.frequency_generator),
				getString(R.string.settings), getString(R.string.about) };
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.openMeasurementFragmentAction:
				dataHandlerActivity.getGuiManager().changeContentFragment(
					dataHandlerActivity.getMyFragmentManager()
							.getGroupMeasurementFragment(),
					false);
				dataHandlerActivity.invalidateOptionsMenu();
				return true;
			case R.id.shareMeasurementAction:
				showShareContentIntent();
				return true;
			case R.id.infoAction:
				openInfoFragment();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void openInfoFragment() {
		dataHandlerActivity.getGuiManager().changeContentFragment(
			((GroupMeasurementFragment) dataHandlerActivity.getGuiManager()
					.getCurrentContentFragment()).getCurrentFragment()
					.getMeasurementInfoFragment(),
			false);
	}

	public void showShareContentIntent() {
		LocalMeasurementFragment localMeasurementFragment = dataHandlerActivity
				.getMyFragmentManager().getGroupMeasurementFragment()
				.getCurrentFragment();
		lastSharedFile = dataHandlerActivity.getFileManager().saveAppFile(
			System.currentTimeMillis() + ".csv",
			localMeasurementFragment.getMeasurement().getCSV(),
			false,
			false);

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("*/*");
		Uri uri = Uri.fromFile(lastSharedFile);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

		startActivityForResult(
			Intent.createChooser(
				sharingIntent,
				dataHandlerActivity.getString(R.string.share_measurement)),
			RQ_SHARE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RQ_SHARE && lastSharedFile != null) {
			lastSharedFile.delete();
		}
	}

	// Setter & Getter
	public void setDataHandlerActivity(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
	}

}
