package com.citaq.citaqfactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.citaq.citaqfactory.SerialPortActivity.ReadThread;
import com.citaq.util.CitaqBuildConfig;
import com.citaq.util.Command;
import com.citaq.util.HttpUtil;
import com.citaq.util.SharePreferencesHelper;
import com.citaq.util.SharePreferencesHelper.ContentValue;
import com.citaq.util.SoundManager;
import com.citaq.util.The3GUtils;
import com.citaq.util.ThreadPoolManager;
import com.printer.util.CallbackUSB;
import com.printer.util.USBConnectUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class AgeingActivity extends SerialPortActivity {
	protected static final String TAG = "AgeingActivity";
	TextView tv_ok, tv_fail, tv_3g_restart, tv_ageing_success_rate, tv_run_time;
	Button bt_network, bt_print,bt_video;
	CheckBox cb_black, cb_grey, cb_cut, cb_sound;

	private static final int nextCheck = 0;
	private static final int refreshNum = 1001;
	private static final int toPrint = 1002;
	private static final int reset3G = 1003;

	private int countSuccess = 0;
	private int countFailue = 0;
	
	private int recLen = 0;   //运行时间

	private byte[] blackblock;
	private byte[] cutpaper;
	private byte[] grayblock;
	
	private int failueCount = 0;
	
	private boolean isTestWeb = false;
	private boolean isPrint = false;
	
	RadioGroup rg_speed;
	RadioButton speed_1000 = null;
	RadioButton speed_3000 = null;
	RadioButton speed_5000 = null;
	
	int printSpeed = 0;
	
	RadioGroup print_type;
	RadioButton print_serial = null;
	RadioButton print_usb = null;
	
	int printType = 0;
	
	SharePreferencesHelper mSharePreferencesHelper;
	
	
	////////////////
	USBConnectUtil mUSBConnectUtil = null;
	
    private String videoName ="Video_Test";
    private String mVideoUriPath = "http://oleeed73x.bkt.clouddn.com/me.mp4";
    private String mVideoPath = "/mnt/external_sd/";
    private String mVideoPath2 = "/mnt/usb_storage/";
    private String mVideoPath3 = "/mnt/internal_sd/";
    private String[] videType ={".mp4",".rmvb",".mkv",".f4v",".flv",".avi"};

	Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ctx = this;
		setContentView(R.layout.activity_ageing);
		
		SyncThread syncThread = new SyncThread();
	    syncThread.start();

		try {
			// mSerialPort = mApplication.getSerialPort();
			mSerialPort = mApplication.getPrintSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
		//	mReadThread = new ReadThread();
		//	mReadThread.start();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}

		initView();
		
		//通过构造方法来传入上下文和文件名
        mSharePreferencesHelper = new SharePreferencesHelper(this,CitaqBuildConfig.SHAREPREFERENCESNAME);

	}

	private void initView() {	
		tv_ok = (TextView) findViewById(R.id.tv_ageing_network_ok_time);
		tv_fail = (TextView) findViewById(R.id.tv_ageing_network_fail_time);
		tv_ageing_success_rate = (TextView) findViewById(R.id.tv_ageing_success_rate);
		
		tv_3g_restart = (TextView) findViewById(R.id.tv_ageing_3g_restart_times);
		
		tv_run_time = (TextView) findViewById(R.id.tv_run_time);

		cb_black = (CheckBox) findViewById(R.id.checkBox_BlackBlock);
		cb_grey = (CheckBox) findViewById(R.id.checkBox_GreyBlock);
		cb_cut = (CheckBox) findViewById(R.id.checkBox_OpenCut);
		cb_sound = (CheckBox) findViewById(R.id.checkBox_Sound);

		bt_print = (Button) findViewById(R.id.bt_ageing_printCut);
		bt_network = (Button) findViewById(R.id.bt_ageing_network);
		
		bt_video = (Button) findViewById(R.id.bt_ageing_video);

		bt_print.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getTag().toString().equals("start")) {
					v.setTag("stop");
					isPrint = true;
					// cs
					((Button) v).setText(ctx.getString(R.string.stop_testing));
					handler.sendEmptyMessage(toPrint);
					Log.i(TAG,
							"checkout--->" + cb_black.isChecked()
									+ cb_grey.isChecked() + cb_cut.isChecked());
				} else {
					// 执行 停止
					v.setTag("start");
					isPrint = false;
					((Button) v).setText(ctx.getString(R.string.start_testing));
				}

			}
		});

		bt_network.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getTag().toString().equals("start")) {
					v.setTag("stop");
					// cs
					((Button) v).setText(ctx.getString(R.string.stop_testing));

					isTestWeb = true;
					handler.sendEmptyMessage(nextCheck);
				} else {
					// 执行 停止
					v.setTag("start");
					((Button) v).setText(ctx.getString(R.string.start_testing));
					isTestWeb = false;
				}

			}
		});
		
		bt_video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = null;
				Intent intent = new Intent(ctx,VideoAcivity.class);
				
				path =getVideo(mVideoPath,videoName,videType);
				
				if(null ==path){
					path =getVideo(mVideoPath2,videoName,videType);	
				}
				if(null ==path){
					path =getVideo(mVideoPath3,videoName,videType);
					
				}
				
				
				if(null == path){
//					Toast.makeText(ctx, R.string.no_video, Toast.LENGTH_LONG).show();
					intent.putExtra("hasExternalVideo", false);
				}else{
					intent.putExtra("hasExternalVideo", true);
					intent.putExtra("path", path);
				}
				
				ctx.startActivity(intent);
				
			}
		});

//		long a = System.currentTimeMillis();
//		blackblock = Command.transToPrintText(getString(R.string.black_block));
//		long b = System.currentTimeMillis();
//		cutpaper = Command.transToPrintText(getString(R.string.cut_paper));
//
//		grayblock = Command.transToPrintText(getString(R.string.gray_block));
//		long d = System.currentTimeMillis();
		
//		Log.i(TAG, "b-a =" + (b - a) + ",,,,,d - a =" + (d - a));
		
		 
		
		rg_speed = (RadioGroup) findViewById(R.id.rg_speed);
		speed_1000 = (RadioButton) findViewById(R.id.speed_1000);
		speed_3000 = (RadioButton) findViewById(R.id.speed_3000);
		speed_5000 = (RadioButton) findViewById(R.id.speed_5000);
		
		rg_speed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(speed_1000.getId()==checkedId){
					printSpeed =1000;
				}else if(speed_3000.getId()==checkedId){
					printSpeed =3000;
				}else if(speed_5000.getId()==checkedId){
					printSpeed =5000;
				}
				
			}
		});
		
		speed_3000.setChecked(true);
		
		
		
		print_type = (RadioGroup) findViewById(R.id.print_type);
		print_serial = (RadioButton) findViewById(R.id.print_serial);
		print_usb = (RadioButton) findViewById(R.id.print_usb);
		
		print_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(print_serial.getId()==checkedId){
					printType = 0;
				}else if(print_usb.getId()==checkedId){
					printType = 1;
					if(mUSBConnectUtil == null){
						initUSBConnect();
					}
				}
				
			}
		});
		
		
		timeHandler.postDelayed(runnable, 1000);    
	}
	
	private void initUSBConnect() {       // remember to destroyPrinter on
		if(mUSBConnectUtil == null){
			 mUSBConnectUtil = USBConnectUtil.getInstance();
		       
		        
			 mUSBConnectUtil.setCallback(new CallbackUSB() {
					
					@Override
					public void callback(final String str,boolean toShow) {
						Log.v(TAG, str.toString());
					}

					@Override
					public void hasUSB(boolean hasUSB) {
						if(!hasUSB) Toast.makeText(ctx, R.string.nousbdevice, Toast.LENGTH_SHORT).show();
						
					}
	
				});
		        
			 mUSBConnectUtil.initConnect(this,USBConnectUtil.TYPE_PRINT);
		}
			
	}
	
	Handler timeHandler = new Handler();    
    Runnable runnable = new Runnable() {    
        @Override    
        public void run() {    
            recLen++;    
            
           
    		
            tv_run_time.setText(getRunTime(recLen));    
            handler.postDelayed(this, 1000);    
        }    
    };   
    
    private String getRunTime(int second){
    	int d = recLen/3600/24;
    	int h = recLen/3600;
 		int m = recLen%3600/60;
 		int s = recLen%3600%60;
 		
 		String time = "";
 		if(d != 0){
 			time = time + d + " d ";
 		}
 		if(h != 0){
 			time = time + String.format("%02d", h) + " : ";
 		}
 		if(m != 0){
 			time = time + String.format("%02d", m) + " : ";
 		}
 		time = time + String.format("%02d", s) + " ";
    	
		return time;
    }
	
    private class SyncThread extends Thread {
        @Override
        public void run() {

    		blackblock = Command.transToPrintText(getString(R.string.black_block));
    		cutpaper = Command.transToPrintText(getString(R.string.cut_paper));
    		grayblock = Command.transToPrintText(getString(R.string.gray_block));
    		
    		long a = System.currentTimeMillis();
    		
    		Log.i(TAG, "a = " + a);

        }
    }
    private boolean fileIsExists(String path){
        try{
                File f=new File(path);
                if(!f.exists()){
                        return false;
                }
                
        }catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
    }
    
    private String getVideo(String path,String name,String[] type){
    	List<String> videoInfos=new ArrayList<String>();
    	
    	for(String ty:type){
    		String videoPath = path+name+ty;
    		if(fileIsExists(videoPath)){
    			return videoPath;
    		}
    	}
    	
    	return null;
    	
    }

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub

	}

	private boolean serialWrite(byte[] cmd) {
		boolean returnValue = true;
		try {

			mOutputStream.write(cmd);
		} catch (Exception ex) {
			returnValue = false;
		}
		return returnValue;
	}
	
	private  boolean usbWrite(byte[] cmd){
		return mUSBConnectUtil.sendMessageToPoint(cmd);
    }
	
	private boolean printerWrite(byte[] cmd) {
		boolean returnValue = true;	
 		if(printType == 0){   //serial
			returnValue = serialWrite(cmd);

 		}else{   //usb
			returnValue = usbWrite(cmd);
		
 		}
		return returnValue;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case refreshNum:
				tv_ok.setText(countSuccess+"");
				tv_fail.setText(countFailue+"");
				
				
				if(countSuccess + countFailue > 0){
					tv_ageing_success_rate.setText(String.format("%.2f", (double)countSuccess/(double)( countSuccess+countFailue) * 100) +"%");
				}
				
				tv_3g_restart.setText(The3GUtils.get3GResetSum()+"");
				handler.sendEmptyMessageDelayed(nextCheck, 1000);
				break;
			case nextCheck:
				checkNet();
				break;
			case toPrint:
				doPrint();
				break;
			case reset3G:
				reset();
				break;
			default:
				break;
			}
		}
	};

	private void reset() {
		ThreadPoolManager.getInstance().executeTask(new Runnable() {
			@Override
			public void run() {
				The3GUtils.reset3G(ctx, false);
				The3GUtils.reset3G(ctx, true);
				The3GUtils.reset3GCount();
			}
		});
	}

	private void doPrint() {
		if(!isPrint)return;
		if (cb_black.isChecked()) {
            if(blackblock == null ) {
            	blackblock = Command.transToPrintText(getString(R.string.black_block));
            }
            printerWrite(blackblock);
		}
		if (cb_grey.isChecked()) {
			if(grayblock == null ) grayblock = Command.transToPrintText(getString(R.string.gray_block));
			printerWrite(grayblock);
		}
		if (cb_cut.isChecked()) {
			if(cutpaper == null ) cutpaper = Command.transToPrintText(getString(R.string.cut_paper));;
			printerWrite(cutpaper);
		}
		
		handler.sendEmptyMessageDelayed(toPrint, printSpeed);
	}

	private void checkNet(){
		if(!isTestWeb)return;
		ThreadPoolManager.getInstance().executeTask(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url ="http://yx.ideliver.cn/checkNet.asp";
				boolean b = HttpUtil.httpString(url);
				if (b){
					countSuccess++;
					failueCount =0;
				}else{
					countFailue++;
					failueCount++;
					if(cb_sound.isChecked())SoundManager.playSound(0, 1);;
					if(failueCount>=10){
						failueCount = 0;
						handler.sendEmptyMessage(reset3G);
					}					
				}
				handler.sendEmptyMessage(refreshNum);

			}});
	}
	
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
			   isPrint = false;
			   isTestWeb = false;
			   finish();
//			   System.exit(0);
			   return true;
		   }
		   return super.onKeyDown(keyCode, event);
		}
	 
	 
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//
		
		recLen = mSharePreferencesHelper.getInt("recLen");
	}
	 
	 
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		ContentValue contentValues = new ContentValue("recLen",recLen);
		mSharePreferencesHelper.putValues(contentValues);
	} 

	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mUSBConnectUtil != null)
			mUSBConnectUtil.destroyPrinter();
	}
}
