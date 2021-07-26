package com.citaq.citaqfactory;

import java.io.IOException;
import java.security.InvalidParameterException;

import com.citaq.citaqfactory.SerialPortActivity.ReadThread;
import com.citaq.util.MainBoardUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MSRActivity extends SerialPortActivity {
	protected static final String TAG = "MSRActivity";
	
	Context mContext;
	TextView  tv_received;
	Button bt_delete;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_msr);
		mContext =this;
		initView();
		try {
			if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK3368)){
				mSerialPort = mApplication.getMSRSerialPort_S4();
			}else{
				mSerialPort = mApplication.getMSRSerialPort();
			}
			mSerialPort = mApplication.getMSRSerialPort_S4();
//			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
//			mReadThread.setTag(0);
			mReadThread.start();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
	}
	
	
	private void initView() {
		tv_received = (TextView) findViewById(R.id.TVTTYS2Reception);
		bt_delete = (Button) findViewById(R.id.BtnReceptionClear);
		bt_delete.setOnClickListener(ClearDataListener);
	}


	OnClickListener ClearDataListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			tv_received.setText("");
		}
			
	};

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		//Log.i(TAG, "size = " + size);
		runOnUiThread(new Runnable() {
			public void run() {
				if (tv_received != null) {
					tv_received.append(new String(buffer, 0, size));
				}
			}
		});
		
	}

}
