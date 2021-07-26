package com.citaq.citaqfactory;

import com.citaq.util.USBPDUtil;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**  
 * 生成该类的对象，并调用execute方法之后  
 * 首先执行的是onProExecute方法  
 * 其次执行doInBackgroup方法  
 *  
 */  
public class USBPDAsyncTask extends AsyncTask<Integer, Integer, String> {  
  
    private TextView textView;  
    
    USBPDUtil mUSBPDUtil;
    UsbManager   myUsbManager;
      
    public USBPDAsyncTask(Context context,UsbManager usbManager, TextView textView,int type) {  
        super();  
        this.textView = textView;  
        myUsbManager = usbManager;
        mUSBPDUtil = new USBPDUtil(context,type);
    }  
    
    public USBPDAsyncTask(Context context,UsbManager usbManager,int type) {  
        super();  
        this.textView = new TextView(context);  
        myUsbManager = usbManager;
        mUSBPDUtil = new USBPDUtil(context,type);
    }  
  
  
    /**  
     * 这里的Integer参数对应AsyncTask中的第一个参数   
     * 这里的String返回值对应AsyncTask的第三个参数  
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
     */  
    @Override  
    protected String doInBackground(Integer... params) { 
    	
    	while(true){
    	mUSBPDUtil.getInfo(myUsbManager);
    	
    	if(mUSBPDUtil.getIsOkSend()){
			
		}else{
		}
    	
    	
    	 try {  
             //休眠1秒  
             Thread.sleep(1000);  
         } catch (InterruptedException e) {  
             // TODO Auto-generated catch block  
             e.printStackTrace();  
         } 
    	}
       // return  0+"";  
    }  
  
  
    /**  
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
     */  
    @Override  
    protected void onPostExecute(String result) {  
//        textView.setText("onPostExecute" + result);  
    }  
  
  
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
    @Override  
    protected void onPreExecute() {  
        textView.setText("start check...");  
    }  
  
  
    /**  
     * 这里的Intege参数对应AsyncTask中的第二个参数  
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
     */  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        int vlaue = values[0];  
    }  
  
      
      
      
  
}  
