package com.deeps.sensormax.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deeps.sensormax.R;
import com.deeps.sensormax.model.FileManager;
import com.deeps.sensormax.model.activities.DataHandlerActivity;

/**
 * @author Deeps
 */

public class DialogManager {

	public static final int STATE_SAVING = 0, STATE_FINISHED = 1,
			STATE_ABORTED = 2;

	private static AlertDialog dialog;
	private static boolean dismiss;
	private static int currentDataset, currentState;
	private static ProgressBar fileProgessBar;
	private static TextView stateTextView;

	public static void showConfirmDialog(
			final DataHandlerActivity dataHandlerActivity, final String text) {
		if (text == null) {
			return;
		}
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				View dialogView = dataHandlerActivity.getLayoutInflater()
						.inflate(R.layout.dialog_message, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						dataHandlerActivity)
						.setView(dialogView)
						.setCancelable(false)
						.setPositiveButton(
							dataHandlerActivity.getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
				AlertDialog dialog = builder.create();
				TextView messageTextView = (TextView) dialogView
						.findViewById(R.id.messageTextView);
				messageTextView.setText(text);
				dialog.show();
			}
		});
	}

	public static void showProgressDialog(
			final DataHandlerActivity dataHandlerActivity, String title,
			String progressText, final int maxDataSet,
			final FileManager fileManager) {
		currentState = STATE_SAVING;
		dataHandlerActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				View dialogView = dataHandlerActivity.getLayoutInflater()
						.inflate(R.layout.dialog_file_progress, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						dataHandlerActivity).setView(dialogView).setCancelable(
					false);
				dialog = builder.create();
				fileProgessBar = (ProgressBar) dialogView
						.findViewById(R.id.progressBar);
				fileProgessBar.setMax(maxDataSet);
				stateTextView = (TextView) dialogView
						.findViewById(R.id.stateTextView);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface arg0, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							fileManager.abortSaveProgress();
							return true;
						}
						return false;

					}
				});
				dialog.show();
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				dismiss = false;
				while (true) {
					dataHandlerActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							switch (currentState) {
								case STATE_SAVING:
									fileProgessBar.setProgress(currentDataset);
									stateTextView.setText(Integer
											.toString(currentDataset)
											+ " / "
											+ Integer.toString(maxDataSet)
											+ " "
											+ dataHandlerActivity
													.getString(R.string.dataset_saved));
									break;
								case STATE_ABORTED:
									fileProgessBar.setProgress(0);
									stateTextView.setText(" "
											+ dataHandlerActivity
													.getString(R.string.save_aborted));
									dismiss = true;
									break;
								case STATE_FINISHED:
									fileProgessBar.setProgress(0);
									stateTextView.setText(" "
											+ dataHandlerActivity
													.getString(R.string.save_finished));
									dismiss = true;
									break;
							}
						}
					});
					if (dismiss) {
						break;
					}
					try {
						Thread.sleep(5L);
					} catch (InterruptedException e) {
					}
				}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
				dialog.dismiss();
			}
		}).start();
	}

	// Setter & Getter
	public static void setCurrentDataset(int currentDataset) {
		DialogManager.currentDataset = currentDataset;
	}

	public static void setCurrentState(int currentState) {
		DialogManager.currentState = currentState;
	}

}
