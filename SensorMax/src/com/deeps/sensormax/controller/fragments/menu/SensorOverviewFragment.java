package com.deeps.sensormax.controller.fragments.menu;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.controller.fragments.measurement.LocalMeasurementFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class SensorOverviewFragment extends SuperFragment {

	/*
	 * used some source code from:
	 * http://www.mysamplecode.com/2012/07/android-listview
	 * -checkbox-example.html
	 */

	private int marked = 0;

	public SensorOverviewFragment() {
		super();
	}

	public SensorOverviewFragment(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		title = dataHandlerActivity.getString(R.string.sensor_overview);
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initListView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(
			R.layout.fragment_sensor_overview,
			container,
			false);
		initUIComponents();
		return view;
	}

	private void initListView() {
		final LocalMeasurementFragment[] sensorFragments = dataHandlerActivity
				.getMyFragmentManager().getLocalMeasurementFragments();
		ArrayList<String> availableSensorNames = new ArrayList<>();
		for (LocalMeasurementFragment f : sensorFragments) {
			availableSensorNames.add(f.getTitle());
		}
		MyCustomAdapter dataAdapter = new MyCustomAdapter(dataHandlerActivity,
				R.layout.custom_sensor_list_view_element, availableSensorNames);
		ListView listView = (ListView) view.findViewById(R.id.sensorListView);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox isInGroupCheckBox = (CheckBox) view
						.findViewById(R.id.isInGroupCheckBox);
				isInGroupCheckBox.setChecked(!isInGroupCheckBox.isChecked());
				updateGroupID(isInGroupCheckBox.isChecked(), position);
			}
		});
	}

	public void updateGroupID(boolean isChecked, int position) {
		int groupID = 0;
		if (isChecked) {
			groupID = 1;
			marked++;
		} else {
			marked--;
		}
		dataHandlerActivity.getMyFragmentManager()
				.getLocalMeasurementFragments()[position].getMeasurement()
				.setGroupID(groupID);
		dataHandlerActivity.invalidateOptionsMenu();
	}

	// Getter & Setter
	public int getMarked() {
		return marked;
	}

	private class MyCustomAdapter extends ArrayAdapter<String> {

		private ArrayList<String> sensorList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<String> sensorList) {
			super(context, textViewResourceId, sensorList);
			this.sensorList = new ArrayList<String>();
			this.sensorList.addAll(sensorList);
		}

		private class ViewHolder {
			TextView sensorNameTextView;
			CheckBox isInGroupCheckBox;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) dataHandlerActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(
					R.layout.custom_sensor_list_view_element,
					null);

				holder = new ViewHolder();
				holder.sensorNameTextView = (TextView) convertView
						.findViewById(R.id.sensorNameTextView);
				holder.isInGroupCheckBox = (CheckBox) convertView
						.findViewById(R.id.isInGroupCheckBox);
				convertView.setTag(holder);

				holder.isInGroupCheckBox
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								CheckBox cb = (CheckBox) v;
								updateGroupID(cb.isChecked(), position);
							}

						});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.sensorNameTextView.setText(sensorList.get(position));
			holder.isInGroupCheckBox
					.setChecked(dataHandlerActivity.getMyFragmentManager()
							.getLocalMeasurementFragments()[position]
							.getMeasurement().getGroupID() == 1);
			return convertView;

		}

	}

}
