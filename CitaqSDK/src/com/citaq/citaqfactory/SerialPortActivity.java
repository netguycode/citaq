/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.citaq.citaqfactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android_serialport_api.SerialPort;

public abstract class SerialPortActivity extends Activity {

	protected CitaqApplication mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;
	protected ReadThread mReadThread;
	protected boolean isStop = false;

	protected class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isStop && !isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
						//stopread();
						
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		
		public void cancel(){
			interrupt();
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			super.start();
			isStop = false;
		}
		
		
	}
	
	
	//停止读取线程
	public void stopread(){
		isStop = true;
		if(mReadThread != null){
			mReadThread.cancel();
			mReadThread = null;
		}
	}

	protected void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (CitaqApplication) getApplication();
		
	}

	protected abstract void onDataReceived(final byte[] buffer, final int size);

	@Override
	protected void onDestroy() {
		stopread();
		mApplication.closeSerialPort();
		mSerialPort = null;
		mOutputStream = null;
		mInputStream = null;
		super.onDestroy();
	}
}
