package com.ehelp.sound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ehelp.R;
import com.ehelp.sound.RecordButton.OnFinishedRecordListener;

public class RecordingActivity extends Activity {

	private RecordButton mRecordButton = null;

	@Override
	public void onCreate(Bundle icicle) {
		Intent intent = getIntent();
		int event_id = getIntent().getIntExtra("EVENT_ID",-1);
		super.onCreate(icicle);
		setContentView(R.layout.activity_sound);
		mRecordButton = (RecordButton) findViewById(R.id.record_button);

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		path = "/sdcard/" + event_id + ".mp3";
		mRecordButton.setSavePath(path);
		mRecordButton
				.setOnFinishedRecordListener(new OnFinishedRecordListener() {

					@Override
					public void onFinishedRecord(String audioPath) {
						Log.i("RECORD!!!", "finished!!!!!!!!!! save to "
								+ audioPath);

					}
				});

	}
}
