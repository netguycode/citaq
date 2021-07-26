package com.citaq.citaqfactory;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.citaq.util.Command;
import com.printer.util.CallbackUSB;
import com.printer.util.USBConnectUtil;

public class PDActivity extends SerialPortActivity {
	protected static final String TAG = "PDActivity";

	Context mContext;
	
	private TextView tv_title;
	private EditText et_QR, et_Title, et_Cmd;
	
	private Spinner spinner_cmd;
	private ArrayAdapter<?> adapter_type, adapter_cmd;
	private String cmdString;
	
	private TextView tv_recevice;
	private Button btn_send = null;
	private Button btn_setTime = null;
	private Button btn_setQR  = null;
	private Button btn_setTitle  = null;
	
	private Button btn_White = null;
	private Button btn_Red = null;
	private Button btn_Blue = null;
	private Button btn_Green = null;
	private Button btn_Black = null;
	
	RadioGroup pd_type;
	RadioButton pd_serial = null;
	RadioButton pd_usb = null;
	int pdType = 0;
	
	LinearLayout layout_send1,layout_send2,layout_send3;
	
	USBConnectUtil mUSBConnectUtil = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pd);
		mContext =this;
		initView();
		initSerial();
		
	}
	
	private void initSerial(){
		try {
			mSerialPort = mApplication.getCtmDisplaySerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
	}
	
	private void initUSBConnect() {       // remember to destroyPrinter on
		if(mUSBConnectUtil == null){
			 mUSBConnectUtil = USBConnectUtil.getInstance();
		       
		        
			 mUSBConnectUtil.setCallback(new CallbackUSB() {
					
					@Override
					public void callback(final String str,boolean toShow) {
						//Log.v(TAG, str.toString());
						
					}

					@Override
					public void hasUSB(boolean hasUSB) {
						if(!hasUSB) Toast.makeText(mContext, R.string.nousbdevice, Toast.LENGTH_SHORT).show();
						
					}
	
				});
		        
			 mUSBConnectUtil.initConnect(this,USBConnectUtil.TYPE_PD);
		}
			
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mUSBConnectUtil != null)
			mUSBConnectUtil.destroyPrinter();
		Log.v(TAG, "onDestroy");
	}
	
	
	private void initView(){
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.requestFocus();
		
		et_QR = (EditText) findViewById(R.id.et_QR);
		et_QR.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				et_QR.setFocusable(true);
				et_QR.requestFocus();
				
			}
		});
		
		et_Title= (EditText) findViewById(R.id.et_Title);
		et_Title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				et_Title.setFocusable(true);
				et_Title.requestFocus();
				
			}
		});
		
		et_Cmd = (EditText) findViewById(R.id.et_cmd);
		et_Cmd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				et_Cmd.setFocusable(true);
				et_Cmd.requestFocus();
				
			}
		});
		

		adapter_type= ArrayAdapter.createFromResource(this, R.array.PD_type, android.R.layout.simple_spinner_item);
		adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		
		pd_type = (RadioGroup) findViewById(R.id.pd_type);
		pd_serial = (RadioButton) findViewById(R.id.pd_serial);
		pd_usb = (RadioButton) findViewById(R.id.pd_usb);
		
		pd_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(pd_serial.getId()==checkedId){
					pdType = 0;
				}else if(pd_usb.getId()==checkedId){
					pdType = 1;
					if(mUSBConnectUtil == null){
						initUSBConnect();
					}
				}
				
			}
		});
		
		//
		spinner_cmd = (Spinner) findViewById(R.id.spinner_cmd);
		adapter_cmd=ArrayAdapter.createFromResource(this, R.array.PD_cmd, android.R.layout.simple_spinner_item);
		adapter_cmd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_cmd.setAdapter(adapter_cmd);
		spinner_cmd.setSelection(0);
        cmdString = spinner_cmd.getSelectedItem().toString();
        spinner_cmd.setOnItemSelectedListener(
        		new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						cmdString=((Spinner)arg0).getSelectedItem().toString();						
						//txtPrint.setText((tmp.split("\n"))[0]);//以\n分割字符串，并只使用第一段
						et_Cmd.setText((cmdString.split("\n"))[0]);
						
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
         		}
        );
        
		tv_recevice = (TextView) findViewById(R.id.tv_recevice);
		
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_send.setOnClickListener(pdListener);
		
		btn_setTime = (Button) findViewById(R.id.btn_setTime);
		btn_setTime.setOnClickListener(pdListener);
		
		btn_setQR = (Button) findViewById(R.id.btn_setQR);
		btn_setQR.setOnClickListener(pdListener);
		
		btn_setTitle= (Button) findViewById(R.id.btn_setTitle);
		btn_setTitle.setOnClickListener(pdListener);
		
		btn_White = (Button) findViewById(R.id.btn_White);
		btn_White.setOnClickListener(pdListener);
		
		btn_Red = (Button) findViewById(R.id.btn_Red);
		btn_Red.setOnClickListener(pdListener);
		
		btn_Blue = (Button) findViewById(R.id.btn_Blue);
		btn_Blue.setOnClickListener(pdListener);
		
		btn_Green = (Button) findViewById(R.id.btn_Green);
		btn_Green.setOnClickListener(pdListener);
		
		btn_Black = (Button) findViewById(R.id.btn_Black);
		btn_Black.setOnClickListener(pdListener);
		
		layout_send1 = (LinearLayout) findViewById(R.id.layout_send1);
		layout_send2 = (LinearLayout) findViewById(R.id.layout_send2);
		layout_send3 = (LinearLayout) findViewById(R.id.layout_send3);
	}
	
	private void setVisibility(int show){
		if(show == View.INVISIBLE){
			layout_send1.setVisibility(View.INVISIBLE);
			layout_send2.setVisibility(View.INVISIBLE);
			layout_send3.setVisibility(View.INVISIBLE);
		}else{
			layout_send1.setVisibility(View.VISIBLE);
			layout_send2.setVisibility(View.VISIBLE);
			layout_send3.setVisibility(View.VISIBLE);
		}
	}
	
	OnClickListener pdListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(pdType == 0){   //serial
				switch(v.getId()){
				case R.id.btn_send:
					
//					byte[] a = Command.getCodepage(15);
//					byte[] b = Command.getChineseMode(0);
					
					String cmdStr =et_Cmd.getText().toString();
					
//					byte[] c = Command.transToPrintText(cmdStr);

//					serialWrite(Command.transToPrintText((cmdString.split("\n"))[0]));
					serialWrite(Command.transToPrintText(cmdStr));
					break;
				case R.id.btn_setTitle:
					String title = et_Title.getText().toString();
					if(title.getBytes().length>30){
						Toast.makeText(mContext, "Too Long(must < 30 bytes", Toast.LENGTH_SHORT).show();
						break;
					
					}
//					title ="STX N " + title + "CR";
					byte[] data1 = {2,78};
					byte[] data2 = Command.transCommandBytes(title);
					byte[] data3 = {13};
					byte[] data4 = new byte[title.getBytes().length + 3];
					System.arraycopy(data1,0,data4,0,2);
					System.arraycopy(data2,0,data4,data1.length,data2.length);
					System.arraycopy(data3,0,data4,data1.length + data2.length,data3.length);
					
					serialWrite( data4);
					
					break;
				case R.id.btn_setTime:
					serialWrite(Command.getSetTimeCmd());
					break;
				case R.id.btn_setQR:
					byte[] cmd = Command.getSendQRCmd(et_QR.getText().toString());
					if(cmd != null){
						serialWrite(cmd);
					}
					break;
					
				case R.id.btn_Red:
					serialWrite(Command.getColorCmd(Command.RED));//1001
					break;
				case R.id.btn_Blue:
					serialWrite(Command.getColorCmd(Command.BLUE));
					break;
				case R.id.btn_Green:
					serialWrite(Command.getColorCmd(Command.GREEN));
					break;
				case R.id.btn_Black:
					serialWrite(Command.getColorCmd(Command.BLACK));
					break;
				case R.id.btn_White:
					serialWrite(Command.getColorCmd(Command.WHITE));
					break;
			
				}
			}else{
				//usb
				String cmd;
				boolean isSend =false;
				StringBuffer sb = new StringBuffer();
				switch(v.getId()){
				case R.id.btn_send:
//					if(cmdString.equals("Chinese CP866")){
//						
//						//Command.getCodepage(18);//cp866
//						//Command.getChineseMode(0); //GBK
//						sb = mUSBPDUtil.sendMessageToPointByte(Command.getCodepage(15));
//						sb = mUSBPDUtil.sendMessageToPointByte(Command.getChineseMode(0));
//						break;
//					}
					cmd =et_Cmd.getText().toString();
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.transToPrintText((cmd.split("\n"))[0]));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					
					break;
					
				case R.id.btn_setTitle:
					String title = et_Title.getText().toString();
					if(title.getBytes().length>30){
						Toast.makeText(mContext, "Too Long(must < 30 bytes", Toast.LENGTH_SHORT).show();
						break;
					
					}
//					title ="STX N " + title + "CR";
					byte[] data1 = {2,78};
					byte[] data2 = Command.transCommandBytes(title);
					byte[] data3 = {13};
					byte[] data4 = new byte[title.getBytes().length + 3];
					System.arraycopy(data1,0,data4,0,2);
					System.arraycopy(data2,0,data4,data1.length,data2.length);
					System.arraycopy(data3,0,data4,data1.length + data2.length,data3.length);
					
					isSend = mUSBConnectUtil.sendMessageToPoint(data4);
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_setTime:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getSetTimeCmd());
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_setQR:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.transToPrintText((et_QR.getText().toString().split("\n"))[0]));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
					
				case R.id.btn_Red:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getColorCmd(Command.RED));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_Blue:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getColorCmd(Command.BLUE));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_Green:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getColorCmd(Command.GREEN));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_Black:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getColorCmd(Command.BLACK));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
				case R.id.btn_White:
					isSend = mUSBConnectUtil.sendMessageToPoint(Command.getColorCmd(Command.WHITE));
					if(isSend){
						tv_recevice.setText("Data send!");
					}else{
						tv_recevice.setText("Data can not sent!");
					}
					break;
			
				}
				
				
			}
		}
			
	};
	
	private  boolean serialWrite(byte[] cmd){
    	boolean returnValue=true;
    	try{
		
			mOutputStream.write(cmd);
    	}
    	catch(Exception ex)
    	{
    		returnValue=false;
    	}
    	return returnValue;
    }

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (tv_recevice != null) {
					tv_recevice.append(new String(buffer, 0, size));
				}
			}
		});

	}

}
