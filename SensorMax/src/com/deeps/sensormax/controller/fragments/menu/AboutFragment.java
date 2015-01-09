package com.deeps.sensormax.controller.fragments.menu;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.controller.fragments.SuperFragment;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class AboutFragment extends SuperFragment {

	public AboutFragment() {
		super();
	}

	public AboutFragment(DataHandlerActivity dataHandlerActivity) {
		super(dataHandlerActivity);
		title = dataHandlerActivity.getString(R.string.about);
	}

	@Override
	protected void initUIComponents() {
		super.initUIComponents();
		initInfoTextView();
	}

	private void initInfoTextView() {
		TextView infoTextView = (TextView) view.findViewById(R.id.infoTextView);
		Spanned spanned = Html.fromHtml(dataHandlerActivity
				.getString(R.string.about_info));
		infoTextView.setMovementMethod(LinkMovementMethod.getInstance());
		infoTextView.setText(spanned);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_about, container, false);
		initUIComponents();
		return view;
	}
}
