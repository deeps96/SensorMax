package com.deeps.sensormax.controller.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.deeps.sensormax.model.interfaces.UICodeWrapper;

/**
 * @author Deeps
 */

public class SuperFragment extends Fragment {

	protected int uiTasks;
	protected String title;
	protected View view;

	protected DataHandlerActivity dataHandlerActivity;

	public SuperFragment() {

	}

	public SuperFragment(DataHandlerActivity dataHandlerActivity) {
		this.dataHandlerActivity = dataHandlerActivity;
	}

	protected void initUIComponents() {
	}

	public void update(float[] data, long time) {
		uiTasks++; // needs to get decrement, if ui task is done
	}

	public boolean onBackPressed() {
		dataHandlerActivity.returnToHome();
		return true;
	}

	public void waitTillUITasksAreDoneAndExecuteUICode(
			final UICodeWrapper uiCodeWrapper) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (uiTasks > 0) {
					try {
						Thread.sleep(2L);
					} catch (InterruptedException e) {
					}
				}
				dataHandlerActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						uiCodeWrapper.runUICode();
					}
				});
			}
		}).start();
	}

	public void onMinimize() {

	}

	public String getTitle() {
		return title;
	}

}
