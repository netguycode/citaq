package com.citaq.citaqfactory;


import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class FSKCALLERIDActivity extends SerialPortActivity implements OnClickListener{
	public final int PACKET_TIMEOUT = 1;
	protected InputStream mInputStream = null;
	protected ReadThreadCaller mReadThreadCaller = null;
	
	private boolean isStop = false;
    private byte[] receivedBuffer = new byte[64];
    private TextView info;
	private Button send, clear;
	
//	private boolean foundFSK = false;
	private int bufferIndexFSK  = 0;
	private TextView tvStatus, tvTime, tvPhoneNum;
	
	ToggleButton tb_auto;

	byte[] cmd = new byte[] { 0x1D,0x49,0x43 };
	
	boolean auto = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fskcallerid);
		
		
		
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);
		
		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(this);
		
		info = (TextView) findViewById(R.id.info);
		
		tvStatus = (TextView) findViewById(R.id.tv_status);
		tvTime = (TextView) findViewById(R.id.time);
		tvPhoneNum = (TextView) findViewById(R.id.phone);
		
		tb_auto = (ToggleButton) findViewById(R.id.tb_auto);
		
		tb_auto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//auto
					
					auto = true;
					send.setEnabled(false);
					handler.sendEmptyMessageDelayed(1002,1000); 
					
				}else{
					auto = false;
					send.setEnabled(true);
					handler.sendEmptyMessageDelayed(1003,1000);//stop
//					
				}
			}
		});
		

		if( initSerial()){	
			tvStatus.setText(R.string.listening);
			startread();
		}else{
			info.setText(R.string.failed);
			tvStatus.setText(R.string.failed);
		}
		 
	}
	
	private boolean initSerial(){
		try {
			mSerialPort = mApplication.getCtmDisplaySerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			return true;
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}	
		return false;
	}
	
	@Override
	protected void onDestroy() {
		stopreadcaller();
		System.out.println("onDestroy.");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.send:
			int what = 1001;
			
			if(tb_auto.isChecked()) what =1002;
			
			handler.sendEmptyMessage(what);
			
			
			break;
		case R.id.clear:
			info.setText("");
			tvTime.setText("");
			tvPhoneNum.setText("");
			break;
		default:
			break;
		}
	}	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(mOutputStream == null) return;
			
			switch (msg.what) {
			case 1001:
				try {
					mOutputStream.write(cmd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1002:
				try {
					mOutputStream.write(cmd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessageDelayed(1002,1000 * 30);
				break;
			case 1003:
				handler.removeMessages(1002);
				break;
			default:
				break;
			}
			
			
		}
	};
   
	private class ReadThreadCaller extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isStop && !isInterrupted()) {
				System.out.println("read....");
				int size;
				byte[] buffer = new byte[64];
				if (mInputStream == null){
					System.out.println("inputStream is null.");
					return;
				}
				try {
					size = mInputStream.read(buffer);
					System.out.println("--read: " + size + " bytes..."+buffer);

					
					onDataReceived(buffer, size);
				} catch (IOException e) {
					e.printStackTrace();					
				}finally{
				}
				
			}
			System.out.println("read thread end.");
		}
		public void cancel(){
			interrupt();
		}
	}
	

    protected void onDataReceived(final byte buffer[], final int size) {
    	System.out.println("onDataReceived ---> " +size +"///" + bytes2HexString(buffer,size));	
		runOnUiThread(new Runnable() {
			public void run() {
//				info.append(encodeBytes(buffer,size));
				/*for(int i = 0; i < size; i++)
				{
					String s = Integer.toHexString((int)buffer[i]);
					//String.valueOf(((char)buffer[i]));
					info.append(s + ',');
				}*/
				System.out.println("bufferIndexFSK = " + bufferIndexFSK);	
				if(bufferIndexFSK > 0){
					for(int i = 0; i < size; i++){
						int index = bufferIndexFSK++;
						if(index>= 64) break;
						receivedBuffer[index] = buffer[i];
					}
				}
				else{
					
					
					int i;
					for(i = 0; i < size; i++){
						if((buffer[i] == 0x04) || //start byte for China
							(buffer[i] == (byte)0x80) || //start byte for UK
							(buffer[i] == 0x43)){  //start byte for Caller ID
							receivedBuffer[0] = buffer[i];//
							bufferIndexFSK = 1;
							i++;
							break;
						}
					}
					
					if(bufferIndexFSK > 0){
						for(; i < size; i++){
							receivedBuffer[bufferIndexFSK++] = buffer[i];
						}
					}
				}
				
				if(bufferIndexFSK > 1){
					if(bufferIndexFSK >= receivedBuffer[1] + 2){//received 
						refreshTel(false);
						bufferIndexFSK = 0;
						
					}else if(bufferIndexFSK >= 11){//Caller ID board
						if(receivedBuffer[0] == 0x43 &&
								   receivedBuffer[1] == 0x54 &&
								   receivedBuffer[2] == 0x45 ){
							refreshTel(true);
							bufferIndexFSK = 0;
						}
						
						
					}
					
				}
				
//				info.append(bytes2HexString(buffer,size));
				System.out.println("onDataReceived ---> " + bytes2HexString(buffer,size));	
			}
		});
    	
    }
    
    private void refreshTel(boolean isCallerID){
    	int byteIndex = 0;
		int byteCout;
				
		//show Caller ID board		
		if(isCallerID){		
			
			
			Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。 
		    t.setToNow(); // 取得系统时间。 
//		    String time = (t.month+1) + "-" + t.monthDay+" "+t.hour+":"+t.minute;  //+t.second; 
		   /* String time = String.format("%02d", t.month+1) + "-" + String.format("%02d", t.monthDay)+ " " +
		    		 String.format("%02d", t.hour) + ":" + String.format("%02d", t.minute);*/
		    String time = String.format("%02d", t.hour) + ":" + String.format("%02d", t.minute) +":" + String.format("%02d", t.second);
		    
		    
			String callerID = "";
			for(int i=0; i<receivedBuffer.length; i++){
				callerID += (char)receivedBuffer[byteIndex + i];
			}
			
//			info.append(callerID);  //
			info.append(time + "  " + callerID);
			
			for(int i=0; i<64; i++){
				receivedBuffer[i] = 0;
			}
			return;
		}
		
		//show tel
		if(receivedBuffer[0] == 0x04){//start byte for China
			byteIndex = 2;	//The 3rd byte is start byte of the Calling time
			byteCout = 8;	//Total bytes of the Calling time
		}
		else if(receivedBuffer[0] == (byte)0x80){//start byte for UK
			byteIndex = 4;					//The 5th byte is start byte of the Calling time
			
			while(receivedBuffer[byteIndex]<0x30) byteIndex++;
			
			byteCout = receivedBuffer[byteIndex-1];	//Total bytes of the Calling time
		}
		else{
			info.append("Error:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
			return;
		}
		
		String in_time =""
		        + (char)receivedBuffer[byteIndex] + (char)receivedBuffer[byteIndex + 1] + "-"
				+ (char)receivedBuffer[byteIndex + 2] + (char)receivedBuffer[byteIndex + 3] + " "
				+ (char)receivedBuffer[byteIndex + 4] + (char)receivedBuffer[byteIndex + 5] + ":"
				+ (char)receivedBuffer[byteIndex + 6] + (char)receivedBuffer[byteIndex + 7];
		
		//int k = receivedBuffer[1];
		
		if(receivedBuffer[0] == 0x04){//start byte for China
			byteIndex += byteCout;	//start byte of the Calling number
			byteCout = receivedBuffer[1] - 8;	//Total bytes of the Calling number
		}
		else if(receivedBuffer[0] == (byte)0x80){//start byte for UK
			byteIndex += byteCout + 2;	//start byte of the Calling number
			byteCout = receivedBuffer[byteIndex - 1];	//Total bytes of the Calling number
		}
		else{
			
			info.append("Error2:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
			return;
		}
		
		String in_tel="";
		for(int i=0; i<byteCout; i++){
			in_tel += (char)receivedBuffer[byteIndex + i];
		}
		System.out.println("-------------------------------------------------------------------");
		System.out.println("time="+in_time);
		System.out.println("tel="+in_tel);
		
		tvTime.setText(in_time);
		tvPhoneNum.setText(in_tel);
		
		info.append(in_time+"  " + in_tel +"\n");
		
		for(int i=0; i<64; i++){
			receivedBuffer[i] = 0;
		}
		
    }
    
    
    public  String bytes2HexString(byte[] b,int size) {
    	  String ret = "";
    	  for (int i = 0; i < size; i++) {
    	   String hex = Integer.toHexString(b[ i ] & 0xFF);
    	   if (hex.length() == 1) {
    	       hex = "0x0" + hex.toUpperCase();
    	   }else{
    		   hex = "0x" + hex.toUpperCase(); 
    	   }
    	   
    	   if(hex.equals("0x00")){
    		   ret += hex+'\n';
    	   }else{
    		   ret += hex+',';
    	   }
    	   
    	  }
    	  return ret;
    }
    	
  //开启读取线程
	private void startread(){
		if(mInputStream == null){
			try {
				mInputStream = mSerialPort.getInputStream();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		isStop = false;
//		foundFSK = false;
		for(int i=0; i<64; i++){
			receivedBuffer[i] = 0;
		}
		
		if(mReadThreadCaller == null){
			mReadThreadCaller = new ReadThreadCaller();
		}
		mReadThreadCaller.start();
		
		System.out.println("start read");
	}
	
	//停止读取线程
	private void stopreadcaller(){
		isStop = true;
		if(mReadThreadCaller != null){
			mReadThreadCaller.cancel();
			mReadThreadCaller = null;
		}
		System.out.println("stop read");
	}
	
	private String encodeBytes(byte[] bytes, int size){

		StringBuilder sb = new StringBuilder(size);
		
		char[]tChars=new char[size];
		for (int i = 0; i < size; i++) {
		      tChars[i]=(char)bytes[i];		
			System.out.println(" tChars[" +i + "] = " + tChars[i]);		
		}
		sb.append(tChars);
		return sb.toString();
	}
	   
		
}
