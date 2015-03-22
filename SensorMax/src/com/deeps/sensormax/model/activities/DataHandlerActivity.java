package com.deeps.sensormax.model.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.MyFragmentManager;
import com.deeps.sensormax.controller.fragments.measurement.GroupMeasurementFragment;
import com.deeps.sensormax.controller.fragments.menu.SensorOverviewFragment;
import com.deeps.sensormax.model.FileManager;
import com.deeps.sensormax.model.LiveStreamManager;
import com.deeps.sensormax.model.ModelManager;
import com.deeps.sensormax.model.MyConfig;
import com.deeps.sensormax.view.GuiManager;

/**
 * @author Deeps
 */

public class DataHandlerActivity extends NavigationActivity {

	private FileManager fileManager;
	private GuiManager guiManager;
	private LiveStreamManager liveStreamManager;
	private ModelManager modelManager;
	private MyConfig myConfig;
	private MyFragmentManager myFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initEnvironment();
		super.onCreate(savedInstanceState);
		mNavigationDrawerFragment.setDataHandlerActivity(this);
		modelManager.onCreate(savedInstanceState);
		guiManager.setMainView(mainView);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Fragment fragment = null;
		boolean isGoingBack = false;
		switch (position) {
			case 0:
				fragment = myFragmentManager.getHomeFragment();
				isGoingBack = true;
				break;
			case 1:
				fragment = myFragmentManager.getSensorOverviewFragment();
				break;
			case 2:
				fragment = myFragmentManager.getTonGeneratorFragment();
				break;
			case 3:
				fragment = myFragmentManager.getSettingsFragment();
				break;
			case 4:
				fragment = myFragmentManager.getAboutFragment();
		}
		guiManager.changeContentFragment(fragment, isGoingBack);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			if (guiManager.getCurrentContentFragment() instanceof SensorOverviewFragment
					&& ((SensorOverviewFragment) guiManager
							.getCurrentContentFragment()).getMarked() > 0) {
				getMenuInflater().inflate(R.menu.sensor_overview, menu);
			} else if (guiManager.getCurrentContentFragment() instanceof GroupMeasurementFragment) {
				getMenuInflater().inflate(R.menu.measurement, menu);
			}

			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	public void blockScreenRotation(boolean block) {
		if (block) {
			int currentOrientation = getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
			}
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	@Override
	public void onBackPressed() {
		if (!guiManager.getCurrentContentFragment().onBackPressed())
			super.onBackPressed();
	}

	private void initEnvironment() {
		fileManager = new FileManager(this);
		myConfig = new MyConfig(this);
		modelManager = new ModelManager(this);
		modelManager.initEnvironment();
		guiManager = new GuiManager(this);
		myFragmentManager = new MyFragmentManager(this);
		liveStreamManager = new LiveStreamManager(this);
	}

	public void returnToHome() {
		mNavigationDrawerFragment.selectItem(0);
		invalidateOptionsMenu();
	}

	// Setter & Getter
	public FileManager getFileManager() {
		return fileManager;
	}

	public MyConfig getMyConfig() {
		return myConfig;
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public GuiManager getGuiManager() {
		return guiManager;
	}

	public MyFragmentManager getMyFragmentManager() {
		return myFragmentManager;
	}

	public void setActionBarTitle(String title) {
		mTitle = title;
		getSupportActionBar().setTitle(title);
		super.setTitle(title);
	}

	public LiveStreamManager getLiveStreamManager() {
		return liveStreamManager;
	}

}
