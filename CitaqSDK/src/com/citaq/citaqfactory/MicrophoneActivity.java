package com.citaq.citaqfactory;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MicrophoneActivity extends Activity {

	MediaRecorder mMediaRecorder;
	private String outputFile = null;
	private Button bt_start, bt_stop, bt_play;
	ImageView img_volume;
	public static final int MAX_LENGTH = 1000 * 60 * 5;// 最大录音时长1000*60*10;
	private static final String TAG = "MicrophoneActivity";
	
	MediaPlayer mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_microphone);

		bt_start = (Button) findViewById(R.id.bt_audio_start);
		bt_stop = (Button) findViewById(R.id.bt_audio_stop);
		bt_play = (Button) findViewById(R.id.bt_audio_play);
		img_volume = (ImageView) findViewById(R.id.img_audio_volume);
		img_volume.setBackgroundResource(R.drawable.audio_volume_high);

		bt_stop.setEnabled(false);
		bt_play.setEnabled(false);
		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/myrecording.3gp";

		

	}

	public void start(View view) {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		mMediaRecorder.setOutputFile(outputFile);
		mMediaRecorder.setMaxDuration(MAX_LENGTH);

		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();

			updateMicStatus(); //
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bt_start.setEnabled(false);
		bt_stop.setEnabled(true);
		bt_play.setEnabled(false);
		// Toast.makeText(getApplicationContext(), "Recording started",
		// Toast.LENGTH_LONG).show();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		deleteFile(new File(outputFile));
		this.finish();
	}

	public void stop(View view) {
		try {
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		bt_start.setEnabled(true);
		bt_stop.setEnabled(false);
		bt_play.setEnabled(true);
//		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
//				Toast.LENGTH_LONG).show();
		
		

	}

	public void play(View view) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				bt_play.setEnabled(true);

			}
		});
		mMediaPlayer.setDataSource(outputFile);
		mMediaPlayer.prepare();
		mMediaPlayer.start();

		// Toast.makeText(getApplicationContext(), "Playing audio",
		// Toast.LENGTH_LONG).show();

		bt_play.setEnabled(false);

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what < 33) {
				img_volume.setBackgroundResource(R.drawable.audio_volume_low);
				img_volume.invalidate();
			} else if (msg.what < 66) {
				img_volume
						.setBackgroundResource(R.drawable.audio_volume_medium);
				img_volume.invalidate();
			} else {
				img_volume.setBackgroundResource(R.drawable.audio_volume_high);
				img_volume.invalidate();
			}

		}
	};

	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	/**
	 * 更新话筒状态
	 * 
	 */
	private int BASE = 1;
	private int SPACE = 1000;// 间隔取样时间

	private void updateMicStatus() {
		if (mMediaRecorder != null) {
			double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
			double db = 0;// 分贝
			if (ratio > 1)
				db = 20 * Math.log10(ratio);
			Log.d(TAG, "分贝值：" + db);
			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
			mHandler.sendEmptyMessage((int) db);
		}
	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			Log.d(TAG, "file not exists");
		}
	}

}
