package com.deeps.sensormax.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class GuiManager {

	private View mainView;

	private DataHandlerActivity dataHandlerActivity;
	private SuperFragment currentContentFragment;

	public GuiManager(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
	}

	public void changeContentFragment(Fragment nextFragment) {
		if (nextFragment == currentContentFragment) {
			return;
		}
		if (currentContentFragment != null) {
			currentContentFragment.onMinimize();
		}
		currentContentFragment = (SuperFragment) nextFragment;
		FragmentManager fragmentManager = dataHandlerActivity
				.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, nextFragment).commit();
		dataHandlerActivity.setActionBarTitle(((SuperFragment) nextFragment)
				.getTitle());
		dataHandlerActivity.invalidateOptionsMenu();
	}

	// Getter & Setter
	public View getMainView() {
		return mainView;
	}

	public void setMainView(View mainView) {
		this.mainView = mainView;
	}

	public SuperFragment getCurrentContentFragment() {
		return currentContentFragment;
	}
}
