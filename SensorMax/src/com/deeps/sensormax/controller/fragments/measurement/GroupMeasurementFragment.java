package com.deeps.sensormax.controller.fragments.measurement;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;
import com.viewpagerindicator.LinePageIndicator;

/**
 * @author Deeps
 */

public class GroupMeasurementFragment extends SuperFragment {

	private ViewPager viewPager;

	private SectionsPagerAdapter sectionsPagerAdapter;

	public GroupMeasurementFragment() {
		super();
	}

	public GroupMeasurementFragment(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		title = dataHandlerActivity.getString(R.string.sensor_overview);// place
																		// holder
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		updateGroupMembers();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
			R.layout.fragment_group_measurement,
			container,
			false);
		initUIComponents();
		return view;
	}

	@Override
	public boolean onBackPressed() {
		return ((LocalMeasurementFragment) sectionsPagerAdapter
				.getItem(viewPager.getCurrentItem())).onBackPressed();
	}

	public void updateGroupMembers() {
		ArrayList<LocalMeasurementFragment> sensorFragments = new ArrayList<>();
		for (LocalMeasurementFragment s : dataHandlerActivity
				.getMyFragmentManager().getLocalMeasurementFragments()) {
			if (s.getMeasurement().getGroupID() == 1) {
				sensorFragments.add(s);
			}
		}
		initPager(sensorFragments);
		view.invalidate();
	}

	private void initPager(
			final ArrayList<LocalMeasurementFragment> sensorFragments) {
		sectionsPagerAdapter = new SectionsPagerAdapter(sensorFragments);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(null);
		viewPager.setAdapter(sectionsPagerAdapter);
		viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount());

		final float density = dataHandlerActivity.getResources()
				.getDisplayMetrics().density;
		LinePageIndicator linePageIndicator = (LinePageIndicator) view
				.findViewById(R.id.indicator);
		linePageIndicator.setStrokeWidth(4 * density);
		linePageIndicator.setLineWidth(20 * density);
		linePageIndicator.setViewPager(viewPager);
		linePageIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				dataHandlerActivity.setActionBarTitle(sensorFragments.get(
					position).getTitle());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		dataHandlerActivity
				.setActionBarTitle(sensorFragments.get(0).getTitle());

		if (sensorFragments.size() == 1) {
			linePageIndicator.setVisibility(View.GONE);
		}
	}

	@Override
	public void onMinimize() {
		for (LocalMeasurementFragment l : sectionsPagerAdapter.sensorFragments) {
			l.getMeasurement().stopMeasuring(false, false);
		}
	}

	// Getter & Setter
	public LocalMeasurementFragment getCurrentFragment() {
		return sectionsPagerAdapter.sensorFragments.get(viewPager
				.getCurrentItem());
	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<LocalMeasurementFragment> sensorFragments;

		public SectionsPagerAdapter(
				ArrayList<LocalMeasurementFragment> sensorFragments) {
			super(getChildFragmentManager());
			this.sensorFragments = sensorFragments;
		}

		@Override
		public Fragment getItem(int position) {
			if (sensorFragments != null && position < sensorFragments.size()) {
				return sensorFragments.get(position);
			}
			return null;
		}

		@Override
		public int getCount() {
			return sensorFragments.size();
		}

	}
}
