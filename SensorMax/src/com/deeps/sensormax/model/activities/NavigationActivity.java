package com.deeps.sensormax.model.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.navigation.NavigationDrawerFragment;
import com.deeps.sensormax.controller.fragments.navigation.NavigationDrawerFragmentWrapper;

public abstract class NavigationActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	protected NavigationDrawerFragmentWrapper mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	protected CharSequence mTitle;

	protected View mainView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = getLayoutInflater().inflate(
			R.layout.activity_navigation,
			null);
		setContentView(mainView);

		mNavigationDrawerFragment = (NavigationDrawerFragmentWrapper) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
			R.id.navigation_drawer,
			(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

}
