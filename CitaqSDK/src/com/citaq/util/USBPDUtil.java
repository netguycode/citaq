package com.citaq.util;

import java.util.HashMap;

import com.citaq.citaqfactory.R;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.View;

@SuppressLint("NewApi")
public class USBPDUtil {
	private static final String ACTION_USB_PERMISSION = "com.CITAQ.peripheraltest.USB_PERMISSION";
	
	
	public static final int TYPE_PD = 10001;
	public static final int TYPE_PRINT = 10002;
	
	private UsbManager myUsbManager =null;
	private UsbDevice myUsbDevice;
	private UsbInterface myInterface;
	private static UsbDeviceConnection myDeviceConnection;

	private final int VendorID = 1155;
	private final int ProductID = 22352;
	
	
	private static UsbEndpoint epOut = null;
	
	Context mContext;
	
	int printType;
	
	public USBPDUtil(Context ctx,int type){
		mContext = ctx;
		printType =type;
	}
	
/*	private UsbManager getUsbManager(){
		myUsbManager = (UsbManager) getSystemService(USB_SERVICE);
		if(myUsbManager != null) {
			enumerateDevice();//枚举
			
			if(myUsbDevice != null)
			{
				findInterface();
			}
			else
			{
				info.append("Device no fined!\n");
			}
			openDevice();
			assignEndpoint();
		}
	}*/
	
	public boolean getIsOkSend(){
		return isokSend;
	}
	
	public StringBuffer getInfo(UsbManager usbManager){
		isokSend = false;
		myUsbManager = usbManager;
		StringBuffer sb = new StringBuffer();
		sb.append(enumerateDevice());//枚举
		if(myUsbDevice != null)
		{
			sb.append(findInterface());
		}
		else
		{
			sb.append("Device no fined!\n");
		}
		sb.append(openDevice());
		sb.append(assignEndpoint());
		return sb;
	}

	/**
	 * 枚举设备
	 */
	@SuppressLint("NewApi")
	public StringBuffer enumerateDevice() {
		StringBuffer sb = new StringBuffer();
		if (myUsbManager == null)
			return sb;
		
		myUsbDevice = null;
		
		HashMap<String, UsbDevice> deviceList = myUsbManager.getDeviceList();
		if (!deviceList.isEmpty()) { // deviceList不为空
			
			for (UsbDevice device : deviceList.values()) {
				if(printType == TYPE_PD){
					sb.append(device.toString());
					sb.append("\n");
					// 输出设备信息
	//				Log.d(TAG, "DeviceInfo: " + device.getVendorId() + " , "
	//						+ device.getProductId());
	
					// 枚举到设备
					if (device.getVendorId() == VendorID
							&& device.getProductId() == ProductID) {
						myUsbDevice = device;
	//					Log.d(TAG, getString(R.string.USBEnumFinishText));
					}
				}else if(printType == TYPE_PRINT){
					for (int i = 0; i < device.getInterfaceCount(); i++) {
	                    UsbInterface intfTemp = device.getInterface(i);

	                    if (intfTemp.getInterfaceClass() == 7) {	//USB printer class is 7

	                        myUsbDevice = device;
	                      
	                    }
	                }
				}
			}
		}
		
		if(myUsbDevice != null)
		{
			sb.append(mContext.getString(R.string.USBEnumFinishText));
		}
		else//USBEnumFailText
		{
			sb.append(mContext.getString(R.string.USBEnumFailText));
		}
		return sb;
	}
	
	/**
	 * 找设备接口
	 */
	private StringBuffer findInterface() {
		StringBuffer sb = new StringBuffer();
		if (myUsbDevice != null) {
			
			myInterface = null;
//			Log.d(TAG, "interfaceCounts : " + myUsbDevice.getInterfaceCount());
			for (int i = 0; i < myUsbDevice.getInterfaceCount(); i++) {
				UsbInterface intf = myUsbDevice.getInterface(i);
				if(printType == TYPE_PD){
					// 根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
					if (intf.getInterfaceClass() == 3	//HID设备
							&& intf.getInterfaceSubclass() == 0
							&& intf.getInterfaceProtocol() == 0) {
						myInterface = intf;
						sb.append(mContext.getString(R.string.InterfaceFinishText));//
	//					Log.d(TAG, getString(R.string.InterfaceFinishText));
						
						break;
					}
				}else if(printType == TYPE_PRINT){
					if (intf.getInterfaceClass() == 7) {
						myInterface = intf;
						sb.append(mContext.getString(R.string.InterfaceFinishText));//
	//					Log.d(TAG, getString(R.string.InterfaceFinishText));
						
						break;
					}
				}
			}
			
			if(myInterface == null)
			{
				sb.append(mContext.getString(R.string.InterfaceFailText));
			}
		}
		
		return sb;
	}

	/**
	 * 打开设备
	 */
	private StringBuffer openDevice() {
		StringBuffer sb = new StringBuffer();
		if (myInterface != null) {
			UsbDeviceConnection conn = null;
			// 在open前判断是否有连接权限；对于连接权限可以静态分配，也可以动态分配权限，可以查阅相关资料
			if(myUsbDevice == null) return sb;
			if (myUsbManager.hasPermission(myUsbDevice)) {
				conn = myUsbManager.openDevice(myUsbDevice);
			}
			else
			{
				PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
				myUsbManager.requestPermission(myUsbDevice, mPermissionIntent);
				conn = myUsbManager.openDevice(myUsbDevice);
			}

			if (conn == null) {
				sb.append(mContext.getString(R.string.OpenFailText));
				return sb;
			}

			if (conn.claimInterface(myInterface, true)) {
				myDeviceConnection = conn; // 到此你的android设备已经连上HID设备
				sb.append(mContext.getString(R.string.OpenFinishText));//
//				Log.d(TAG, getString(R.string.OpenFinishText));
			} else {
				sb.append(mContext.getString(R.string.OpenFailText));
				conn.close();
			}
		}
		return sb;
	}

	
	private boolean isokSend = false;
	
	/**
	 * 分配端点，IN | OUT，即输入输出；此处我直接用1为OUT端点，0为IN，当然你也可以通过判断
	 */
	private StringBuffer assignEndpoint() {
		StringBuffer sb = new StringBuffer();
		if ((myInterface != null) && (myDeviceConnection != null)) {
/*			int EPCount = myInterface.getEndpointCount();
			info.append("设备总共有" + EPCount + "个端点\n");*/
			if (myInterface.getEndpoint(1) != null) {
				epOut = myInterface.getEndpoint(1);
			}
			if (myInterface.getEndpoint(0) != null) {
				//epIn = myInterface.getEndpoint(0);
			}
			
			sb.append(mContext.getString(R.string.USBPDFinishText));
//			Log.d(TAG, getString(R.string.USBPDFinishText));
			
			isokSend =true;
			
		}
		else
		{
			sb.append(mContext.getString(R.string.USBPDFailText));
		}
		
		return sb;
	}
	//发送数据byte[]
	  public  StringBuffer sendMessageToPointByte(byte[] cmd){
		  StringBuffer sb = new StringBuffer();
		  // bulkOut传输  
	    	if(epOut != null)
	    	{
		        if (myDeviceConnection.bulkTransfer(epOut, cmd, cmd.length, 0) < 0)  
		        	sb.append("bulkOut返回输出为  负数");  
		        else {  
		        	sb.append("Data send！\n");  
		        }
	    	}
	    	else
	    	{
	    		sb.append("Data can not sent!\n");
	    	}
	    	return sb;
	  }
	
	 // 发送数据  最好在另外一个线程中执行本步骤的操作，以便防止阻塞主UI线程
    public StringBuffer sendMessageToPoint(String cmdString) {//byte[] buffer
    	
    	//byte[] arrayOfByte = new byte[64]; //ESC Q A 12345678 CR
		
/*		arrayOfByte[0] = 0x1D;
		arrayOfByte[1] = 0x49;
		arrayOfByte[2] = 0x42;
		
		arrayOfByte[3] = 0x1B;
		arrayOfByte[4] = 0x51;
		arrayOfByte[5] = 0x41;
		
		arrayOfByte[6] = 0x31;
		arrayOfByte[7] = 0x32;
		arrayOfByte[8] = 0x33;
		arrayOfByte[9] = 0x34;
		arrayOfByte[10] = 0x35;
		arrayOfByte[11] = '.';
		arrayOfByte[12] = 0x30;
		arrayOfByte[13] = 0x36;
		arrayOfByte[14] = 0x38;
		arrayOfByte[15] = 0x0D;*/
		
    	byte[] arrayOfByte = Command.transToPrintText((cmdString.split("\n"))[0]);
		
       return sendMessageToPointByte(arrayOfByte);
    } 

}
