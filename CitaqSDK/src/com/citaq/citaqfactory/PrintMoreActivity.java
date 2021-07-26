package com.citaq.citaqfactory;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import com.citaq.citaqfactory.SerialPortActivity.ReadThread;
import com.citaq.util.Command;
import com.printer.util.CallbackUSB;
import com.printer.util.USBConnectUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class PrintMoreActivity extends SerialPortActivity{
	private static final String TAG  ="PrintActivityMore";
	private Button btn_SetCodepage;
	private Button btn_SetCharacterSet;
	private Button btn_SetResidentCharacterSet;
	private Button btn_SetPrintDensity;
	private Button btn_EnableChinese;
	private Button btn_DisableChinese;
	private Button btn_EnableBuzzer;
	private Button btn_DisableBuzzer;
	
	private Bitmap mBitmap = null;
	static private int openfileDialogId = 0;
	
	int mCurrentBt = -1;
	
	Spinner spinnerCP,spinnerCS,spinnerResidentCS,spinnerPrintDensity;
	private ArrayAdapter<?> cpAdapter,csAdapter,residentCsAdapter,printDensityAdapter;
	private int cpIndex,csIndex,residentCsIndex,printDensityIndex;
	private int Print_type = 0;
	USBConnectUtil mUSBConnectUtil = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_printmore);
		
		try {
//			mSerialPort = mApplication.getSerialPort();
			mSerialPort = mApplication.getPrintSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
//			initInputStream();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
		
		initView();
		
		// now we olny want it to run in serial.
		/*Intent intent=getIntent();
		Print_type = intent.getIntExtra("Print_type",0);*/
		
		if(Print_type == 1){
			initUSBConnect();
		}
	}
	
	private void initUSBConnect() {       // remember to destroyPrinter on
		 mUSBConnectUtil = USBConnectUtil.getInstance();
	    
			
	}
	
	private void initInputStream(){
		mInputStream = mSerialPort.getInputStream();

		/* Create a receiving thread */
		mReadThread = new ReadThread();
		mReadThread.start();
	}
	
	private void initView(){
		
		btn_SetCodepage = (Button) findViewById(R.id.btn_setCodepage);
		btn_SetCodepage.setOnClickListener(SendPrintListener);
		
		btn_SetCharacterSet = (Button) findViewById(R.id.btn_setCharacterSet);
		btn_SetCharacterSet.setOnClickListener(SendPrintListener);
		
		btn_SetResidentCharacterSet = (Button) findViewById(R.id.btn_setResidentCharacterSet);
		btn_SetResidentCharacterSet.setOnClickListener(SendPrintListener);
		
		btn_SetPrintDensity= (Button) findViewById(R.id.btn_setPrintDensity);
		btn_SetPrintDensity.setOnClickListener(SendPrintListener);
		
		btn_EnableChinese = (Button) findViewById(R.id.btn_enableChinese);
		btn_EnableChinese.setOnClickListener(SendPrintListener);
		
		btn_DisableChinese = (Button) findViewById(R.id.btn_disableChinese);
		btn_DisableChinese.setOnClickListener(SendPrintListener);
		
		btn_EnableBuzzer = (Button) findViewById(R.id.btn_enableBuzzer);
		btn_EnableBuzzer.setOnClickListener(SendPrintListener);
		
		btn_DisableBuzzer = (Button) findViewById(R.id.btn_disableBuzzer);
		btn_DisableBuzzer.setOnClickListener(SendPrintListener);

		spinnerCP = (Spinner) findViewById(R.id.spinnerCP);
		cpAdapter = ArrayAdapter.createFromResource(this, R.array.Codepage, android.R.layout.simple_spinner_item);
		cpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCP.setAdapter(cpAdapter);
		cpIndex = 0;
		spinnerCP.setSelection(cpIndex);
		spinnerCP.setOnItemSelectedListener(
        		new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						cpIndex = ((Spinner)arg0).getSelectedItemPosition();						
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
         		}
        );
		
		spinnerCS = (Spinner) findViewById(R.id.spinnerCS);
		csAdapter = ArrayAdapter.createFromResource(this, R.array.CharacterSet, android.R.layout.simple_spinner_item);
		csAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCS.setAdapter(csAdapter);
		csIndex = 0;
		spinnerCS.setSelection(csIndex);
		spinnerCS.setOnItemSelectedListener(
        		new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						csIndex = ((Spinner)arg0).getSelectedItemPosition();						
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
         		}
        );
		
		//
		spinnerResidentCS = (Spinner) findViewById(R.id.spinnerResidentCS);
		residentCsAdapter = ArrayAdapter.createFromResource(this, R.array.ResidentCharacterSet, android.R.layout.simple_spinner_item);
		residentCsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerResidentCS.setAdapter(residentCsAdapter);
		residentCsIndex = 0;
		spinnerResidentCS.setSelection(residentCsIndex);
		spinnerResidentCS.setOnItemSelectedListener(
        		new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						residentCsIndex = ((Spinner)arg0).getSelectedItemPosition();						
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
         		}
        );
		
		
		//
		spinnerPrintDensity = (Spinner) findViewById(R.id.spinnerPrintDensity);
		printDensityAdapter = ArrayAdapter.createFromResource(this, R.array.PrintDensity, android.R.layout.simple_spinner_item);
		printDensityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPrintDensity.setAdapter(printDensityAdapter);
		printDensityIndex = 0;
		spinnerPrintDensity.setSelection(printDensityIndex);
		spinnerPrintDensity.setOnItemSelectedListener(
        		new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						printDensityIndex = ((Spinner)arg0).getSelectedItemPosition();						
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
         		}
        );
	}
	
	
	OnClickListener SendPrintListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			mCurrentBt = v.getId();
			switch(mCurrentBt){
			case R.id.btn_opencash:
				printerWrite(Command.openCash);
				break;
			case R.id.btn_cutPaper:
				printerWrite(Command.cutPaper);
				break;
			case R.id.btn_getprintstatus:
				printerWrite(Command.printStatus);
				break;
			case R.id.btn_printtest:
				printerWrite(Command.printTest);
				break;
			case R.id.btn_printdemo:
				printerWrite(Command.getPrintDemo());
				break;
			
			case R.id.btn_openPicture:
				mBitmap = null;
				 if(OpenFileDialog.isDialogCreate &&
						 OpenFileDialog.FileSelectView.getCurrentPath().equals(OpenFileDialog.sRoot)){
					 
					 removeDialog(openfileDialogId);
					 
				 }
				showDialog(openfileDialogId);
				break;
			case R.id.btn_printPicture:
				if(mBitmap != null)
				{
					
					/*Thread thread=new Thread(new Runnable()  
			        {  
			            @Override  
			            public void run()  
			            {  
			            	printerWrite(Command.getPrintPictureCmd(mBitmap));
			            }  
			        });  
			        thread.start();*/
					printerWrite(Command.getPrintPictureCmd(mBitmap));
					
				}
				break;
				
				//////////
			case R.id.btn_setCodepage:
				printerWrite(Command.getCodepage(cpIndex));//扩展字符集 //参数！！！！！！修改  Spinner
				break;
				
			case R.id.btn_setCharacterSet:
				printerWrite(Command.getCharacterSet(csIndex));//国际字符集 //参数！！！！！！修改  Spinner
				break;
			case R.id.btn_setResidentCharacterSet:
				printerWrite(Command.getResidentCharacterSet(residentCsIndex));
				break;
			case R.id.btn_setPrintDensity:
				printerWrite(Command.getPrintDensity(printDensityIndex));
				break;
			case R.id.btn_enableChinese:
				printerWrite(Command.getChineseMode(1));
				break;
			case R.id.btn_disableChinese:
				printerWrite(Command.getChineseMode(0));
				break;
			case R.id.btn_enableBuzzer:
				printerWrite(Command.getBuzzer(1));
				break;
			case R.id.btn_disableBuzzer:
				printerWrite(Command.getBuzzer(0));
				break;
			}
		}
			
	};
	
	private boolean printerWrite(byte[] cmd){
		boolean returnValue = true;
		if(Print_type == 0){   //serial
			returnValue = serialWrite(cmd);
		}else{   //usb
			returnValue = usbWrite(cmd);
		}
		
		return returnValue;
	}
	
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
	
	private  boolean usbWrite(byte[] cmd){
		return mUSBConnectUtil.sendMessageToPoint(cmd);
    }

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
	}
	
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Log.v(TAG, "onDestroy");
	}


}
