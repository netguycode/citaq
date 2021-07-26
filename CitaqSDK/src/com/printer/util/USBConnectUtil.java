package com.printer.util;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

@SuppressLint("NewApi")
public class USBConnectUtil{
	private static final String ACTION_USB_PERMISSION = "com.citaq.usbprinter.USB_PERMISSION";
	
	public static final int TYPE_PD = 10001;
	public static final int TYPE_PRINT = 10002;
	

	private static final String TAG = "USBConnectUtil";
	private static USBConnectUtil mInstance;

	private UsbManager mUsbManager =null;
	private UsbDevice mUsbDevice;
	private UsbInterface mInterface;
	private UsbDeviceConnection mUsbDeviceConnection;

	private final int VendorID_Print = 0x6868;
	private final int ProductID_print = 0x0600;
	
	private final int VendorID_PD = 0x0483;
	private final int ProductID_PD = 0x5750;
	
	int mUSBType = 0;
	
	private USBReadThread mUSBReadThread;
	
	private UsbEndpoint epBulkOut = null;
	private UsbEndpoint epBulkIn = null;
	
	Context mContext;
	
	static CallbackUSB mCallbackUSB;
	
	private USBConnectUtil() {}
	
    public static USBConnectUtil getInstance() {
        if (mInstance == null) {
            mInstance = new USBConnectUtil();
        }
        return mInstance;
    }
    
    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public void initConnect(Context context, int type) {
        getInstance().init(context,type);
    }
    
    public void setCallback(CallbackUSB mCallback){
    	mCallbackUSB = mCallback;
    }
    
    /**
     * 销毁打印机持有的对象
     */
    public void destroyPrinter() {
        getInstance().destroy();
    }
    
    private void init(Context context, int type) {
        mContext = context;
        mUSBType = type;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        
        
        IntentFilter usbFilter = new IntentFilter(ACTION_USB_PERMISSION);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, usbFilter);
        
        openUSBPrinter();
    }

    private void destroy() {
    	
        mContext.unregisterReceiver(mUsbDeviceReceiver);
        
        if(mUSBReadThread != null){
        	mUSBReadThread.cancel();
        	mUSBReadThread = null;
		}

        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }

        mContext = null;
        mUsbManager = null;
        mInterface = null;
        mInstance = null;
        
    }
	

    /**
     * Open device
     */
    private boolean openDevice() {
        if (mInterface != null) {
            UsbDeviceConnection conn = null;

            if(mUsbDevice == null) return false;
            if (mUsbManager.hasPermission(mUsbDevice)) {//Check if USB device have permission to open
                conn = mUsbManager.openDevice(mUsbDevice);
            }else{//no permission, try to get permission
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);//get USB device access permission
                
                //callback
                callback("USB device have not the permission to access, try to get the permission and open again！\n",false);
            }

            if (conn == null) {
                return false;
            }

            if (conn.claimInterface(mInterface, true)) {
            	mUsbDeviceConnection = conn;
				//Log.d(TAG, getString(R.string.OpenFinishText));
            	callback("Open USB device success！\n",false);
                return true;
            } else {
                conn.close();
            }
        }

        return false;
    }
    
    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    	if(mUsbDevice != null){
                            //call method to set up device communication
                            if(openDevice()) {
                                assignEndpoint();
                            }
                        }
                    } else {
                    	Log.d(TAG, "permission denied for device " + usbDevice);
                    }              
                   
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                	 Log.v(TAG,"Device closed");
                	 callback("Device closed\n",false);
                     mUsbDeviceConnection = null;
                     
                }
            }else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){ //android.hardware.usb.action.USB_DEVICE_ATTACHED
	    		Log.v(TAG,"Device access"); 
	    		callback("Device access\n",false);
	    		
	    		openUSBPrinter();
	    	}
	    
        }
    };
    
    public boolean openUSBPrinter(){

        if(mUsbManager == null) {
        	return false;
        }

        if(mUsbManager != null) {
        	callback("Start to enumerate USB device：\n",false);
            if(enumerateDevice()) {//Find USB device
                if(openDevice()) {//Open USB device
                    assignEndpoint();
                }
            }
        }

        return false;
    }
    
    /**
     * Assign Endpoint
     */
    private boolean assignEndpoint() {

        boolean ret = true;
        if ((mInterface != null) && (mUsbDeviceConnection != null)) {
			int EPCount = mInterface.getEndpointCount();
			callback("Total have " + EPCount + "endpoint.\n",false);

			epBulkOut = null;

			for(int n = 0; n < EPCount; n++){
                if (mInterface.getEndpoint(n) != null) {
                    UsbEndpoint epTemp = mInterface.getEndpoint(n);
                    if(epTemp.getDirection() == UsbConstants.USB_DIR_OUT){//OUT endpoint: from Host to USB device
                    	epBulkOut = epTemp;
                    }else if(epTemp.getDirection() == UsbConstants.USB_DIR_IN){//IN endpoint: from USB device to Host
                    	epBulkIn = epTemp;
                         mUSBReadThread = new USBReadThread();
                         mUSBReadThread.start();
                    }
                }else {
                    ret = false;
                }
            }

            if(epBulkOut == null){
            	callback("The USB device have not OUT endpoint!\n",false);
			    ret = false;
            }

        }else{
            ret = false;
        }
        return ret;
    }
    
    /**
     * Enumerate Device
     */
    public boolean enumerateDevice() {
        if (mUsbManager == null) {
            return false;
        }

        mUsbDevice = null;
        mInterface = null;

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();//Get all USB device list
        if (!deviceList.isEmpty()) { // deviceList is not empty
        	if(mUSBType == TYPE_PRINT){
	            //info.append("USB device list:\n");
	            for (UsbDevice device : deviceList.values()) {//Check every device
	                //info.append(device.toString());
	                //info.append("\n");
	                for (int i = 0; i < device.getInterfaceCount(); i++) {
	                    UsbInterface intfTemp = device.getInterface(i);
	
	                    if (intfTemp.getInterfaceClass() == 7) {	//USB printer class is 7
	                    	callback("\nHave USB printer:\n",false);
	                    	callback(device.toString(),false);
	                    	callback("\n",false);
	
	                        mUsbDevice = device;
	                        mInterface = intfTemp;
	                        
	                        callback(true);
	                        return true;
	                    }
	                }
        		
        		   
	            }
        		/*String s="";
        		 for (UsbDevice device : deviceList.values()) {
        			 for (int i = 0; i < device.getInterfaceCount(); i++) {
 	    				UsbInterface intfTemp = device.getInterface(i);
 	    				if(intfTemp == null){
 	    					s=s+ device.getDeviceId();
 	    				}
        			 }
        		 }
        		*/
        	}else if (mUSBType == TYPE_PD){
        		 for (UsbDevice device : deviceList.values()) {
        			if (device.getVendorId() == VendorID_PD && device.getProductId() == ProductID_PD) {
        				for (int i = 0; i < device.getInterfaceCount(); i++) {
    	    				UsbInterface intfTemp = device.getInterface(i);
    		        		// 根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
    						if (intfTemp.getInterfaceClass() == 3	//HID设备
    								&& intfTemp.getInterfaceSubclass() == 0
    								&& intfTemp.getInterfaceProtocol() == 0) {
    							
    							callback("\nHave USB PD:\n",false);
    	                    	callback(device.toString(),false);
    	                    	callback("\n",false);
    							
    							 mUsbDevice = device;
    							 mInterface = intfTemp;
    							 
    							 callback(true);
    							return true;
    						}
    	        		}

 					}
	        		
        		 }
        	}else{
        		
        	}
        }

        callback("Can not find USB device...\n",false);
        callback(false);
        return false;
    }
    
    private class USBReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            //isReadThreadRunning = true;
            while (!isInterrupted()) {
                int size;
                byte[] buffer = new byte[64];
                if (epBulkIn == null || mUsbDeviceConnection == null){
                    return;
                }
               
                size = mUsbDeviceConnection.bulkTransfer(epBulkIn, buffer, buffer.length, 3000);
                //info.append("USB rec:");
                //info.append(new String(buffer, 0, size));
                //info.append("\n");
                if(size > 0){
                    String debstr;
                    debstr = "Rec " + size + " bytes(USB):   ";
                    for (int i = 0; i < size; i++) {
                        String s;
                        if(buffer[i] < 0){
                            s = Integer.toHexString(256 + buffer[i]);//String.valueOf(((char)buffer[i]));
                        }
                        else {
                            s = Integer.toHexString(buffer[i]);//String.valueOf(((char)buffer[i]));
                        }

                        if(s.length() < 2){
                            s = "0x0" + s + ',';
                        }else{
                            s = "0x" + s + ',';
                        }
                        debstr += s;
                    }
                    debstr += "\r\n";
                    System.out.println(debstr);
                    callback(debstr,true);
                }
            }
        }

        public void cancel(){
            interrupt();
        }
    }
    
    
    /**
     * 发送数据
     * @param buffer
     */
    public boolean sendMessageToPoint(byte[] data) {
    	 if(epBulkOut != null){ 
	        if(mUsbDeviceConnection.bulkTransfer(epBulkOut,data,data.length,0) >= 0){
	            //0 或者正数表示成功
	        	callback("Data send OK！\n",false);
	        	return true;
	        }else{
	        	callback("bulkOut error！",false);
	        }
    	 }else{
    		 callback("Data can not sent!\n",false);
    	 }
    	 return false;
    }
    
    
    public boolean sendMessageToPoint(String data) {
    	return sendMessageToPoint(data.getBytes());
    }
   
       
    
    private void callback(String str ,boolean toShow){
    	if(mCallbackUSB != null)
    		mCallbackUSB.callback(str, toShow);
    }
    
    private void callback(boolean hasUSB){
    	if(mCallbackUSB != null)
    		mCallbackUSB.hasUSB(hasUSB);
    }
		
}
