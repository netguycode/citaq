package com.citaq.citaqfactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citaq.util.InterAddressUtil;
import com.citaq.util.RAMandROMInfo;
import com.citaq.util.ZXingUtil;

public class SysInfoActivity extends Activity {
	Context ctx;
	protected static final int MSG_REFRESH_UI = 1000;
	TextView tv_serialNo, tv_ICCID, tv_buildNum, tv_IMEI, tv_productName, tv_platform, tv_android_ver, tv_sd, tv_battery_info_voltage, tv_battery_info_status, tv_sys_uptime, tv_version_name;
	String info="";
	ImageView iv_mac;
	
	String sdcard ="/mnt/external_sd";  // sdcard ="/storage/23E5-4C27";
	
	private Handler mHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sys_info);
		ctx = this;
		tv_serialNo = (TextView) findViewById(R.id.tv_serialNo);
		tv_ICCID = (TextView) findViewById(R.id.tv_ICCID);
		tv_buildNum = (TextView) findViewById(R.id.tv_buildID);
		tv_IMEI = (TextView) findViewById(R.id.tv_IMEI);
		tv_productName = (TextView) findViewById(R.id.tv_productName);
		tv_platform = (TextView) findViewById(R.id.tv_platform);
		tv_android_ver = (TextView) findViewById(R.id.tv_android_ver);
		tv_sd = (TextView) findViewById(R.id.tv_sd);
		//升级判断
//		private static String getProductName() { 
//	    	return SystemProperties.get("ro.product.model");        
//	    }
		
		tv_battery_info_voltage = (TextView) findViewById(R.id.battery_info_voltage);
		tv_battery_info_status = (TextView) findViewById(R.id.battery_info_status);
		
		tv_sys_uptime = (TextView) findViewById(R.id.uptime);
		
		tv_version_name = (TextView) findViewById(R.id.version_name);
		
		iv_mac = (ImageView) findViewById(R.id.iv_mac);
		
		String mac = InterAddressUtil.getMacAddress();
		if (mac !=null ){
			Bitmap mBitmap = ZXingUtil.createQRImage( mac, 250, 250);
			iv_mac.setImageBitmap(mBitmap);
		}
		

	    String serial = getSerial();
	    if(serial!=null && !"".equals(serial)){
	    	tv_serialNo.setText(serial);
	    }
	    
	    String iccid = getICCID();
	    if(iccid!=null){
	    	String newStr = iccid.substring(iccid.indexOf(":")+1,iccid.length());
	    	tv_ICCID.setText(newStr);
	    	
	    }
	    
	    String sys_buildID = getBuildDisplayID();
	    if(sys_buildID!=null){
	    	tv_buildNum.setText(sys_buildID);
	    }
	    
	    String sys_IMEI = getIMEI();
	    if(sys_IMEI!=null){
	    	tv_IMEI.setText(sys_IMEI);
	    }
	    
	    String sys_productName = getProductName();
	    if(sys_productName!=null){
	    	tv_productName.setText(sys_productName);
	    }
	    
	    String sys_platform = getPlatform();
	    if(sys_platform!=null){
	    	RAMandROMInfo mRAMandROMInfo = new RAMandROMInfo(ctx);
	 	    String RAMInfo = mRAMandROMInfo.showRAMInfo2();
	 	    
	 	    String ROMinfo =mRAMandROMInfo.showROMInfo2();
	 	    
	 	    tv_platform.setText(sys_platform+"(" + getCPUNumCores() +")  RAM+ROM("+RAMInfo+ "+" + ROMinfo + ")"      );
	 	    
	 	  
	    }
	    
	    
	    
	    String sys_android_ver = getSystemVersion();
	    tv_android_ver.setText(sys_android_ver);
	    
	    
	    if(sys_android_ver.contains("6.0")){
	    	sdcard ="/storage/23E5-4C27";
	    }
	    
	    
	    String sys_sd = getSDTotalSize();
	    tv_sd.setText(sys_sd); 
	    
	    String sys_version_name = getVersion();
	    if(sys_version_name!=null){
	    	tv_version_name.setText(sys_version_name);
	    }
		

	    mHandler = new Handler();
	    mHandler.post(new Runnable() {
	        @Override
	        public void run()
	        {
	            // TODO Auto-generated method stub
	        	tv_sys_uptime.setText(getUptime());  
	            mHandler.postDelayed(this, 1000);
	        }
	    });
	}
	
	
	private String getUptime(){
		//（1）统计系统从启动到现在的时间elapsedRealTime(); （2）统计系统从启动到当前处于非休眠的时间uptimeMillis() 以毫秒为单位
		long uptimeMillis = android.os.SystemClock.uptimeMillis();
		int uptime  = (int) (uptimeMillis/1000);
		 
    	int d = uptime/3600/24;
    	int h = uptime/3600;
 		int m = uptime%3600/60;
 		int s = uptime%3600%60;
 		
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
	
	
	private String getSerial() {
		String serial = null;

		try {

			Class<?> c = Class.forName("android.os.SystemProperties");

			Method get = c.getMethod("get", String.class);

			serial = (String) get.invoke(c, "ro.serialno");

		} catch (Exception e) {

			e.printStackTrace();

		}
		
		return serial;
	}
	
	private String getICCID(){
		 TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	     String iccid =tm.getSimSerialNumber();  //取出ICCID
	     return iccid;
	}
	
	public String getIMEI() {

	    return ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

	}
	
	private String getProductName() {
		String productName = null;

		try {

			Class<?> c = Class.forName("android.os.SystemProperties");

			Method get = c.getMethod("get", String.class);

			productName = (String) get.invoke(c, "ro.product.model");

		} catch (Exception e) {

			e.printStackTrace();

		}
		
		return productName;
	}
	
	private String getBuildDisplayID() {
		String serial = null;

		try {

			Class<?> c = Class.forName("android.os.SystemProperties");

			Method get = c.getMethod("get", String.class);

			serial = (String) get.invoke(c, "ro.build.display.id");

			RAMandROMInfo ra = new RAMandROMInfo(ctx);

		} catch (Exception e) {

			e.printStackTrace();

		}
		
		return serial;
	}
	

	

	private String getPlatform() {
		String platform = null;

		try {

			Class<?> c = Class.forName("android.os.SystemProperties");

			Method get = c.getMethod("get", String.class);

			platform = (String) get.invoke(c, "ro.board.platform");

		} catch (Exception e) {

			e.printStackTrace();

		}
		
		return platform;
	}
	

	private int getCPUNumCoresInt() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Default to return 1 core
			return 1;
		}
	}
	
	 /** 
     * 获得SD卡总大小 
     *  
     * @return 
     */  
    private String getSDTotalSize() {  
    	 String android =getSystemVersion();
    	 if (Environment.getExternalStorageState().equals(
                 Environment.MEDIA_MOUNTED)) {
		        File path = new File(sdcard);  
		        
		        if(path.exists()){
		          try{
			        StatFs stat = new StatFs(path.getPath());  
			        long blockSize = stat.getBlockSize();  
			        long totalBlocks = stat.getBlockCount();  
			        if(totalBlocks ==0){
			        	return "No SD.";
			        }
			        
			        return Formatter.formatFileSize(ctx, blockSize * totalBlocks);
		          }catch(Exception e) {
		        	  return "No SD.";
		  		  }
		        }
    	 }
		 return "No SD.";
		  
    }  
    
    public  String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

	
	
	private String getCPUNumCores(){
		int core = getCPUNumCoresInt();
		String coreNum = "";
		  if(core == 8){
		    	coreNum="Octa-core[8]";
		    }else if(core == 4){
		    	coreNum="Quad-core[4]";
		    }else if(core == 2){
		    	coreNum="Dual-core[2]";
		    }else{
		    	coreNum = core+"-core";
		    }
		 return coreNum;
	}
	
	Handler mhHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_REFRESH_UI:
//            	tv_battery_info_voltage.setText(msg.obj.toString());
//            	tv_battery_info_status.setText(msg.)
            	
            	Bundle bundle = msg.getData();
                String voltage = bundle.getString("voltage");
                String status = bundle.getString("status");
                String level = bundle.getString("level");
                
                tv_battery_info_voltage.setText(voltage+" mV");
//                tv_battery_info_status.setText(status);
                
                tv_battery_info_status.setText(status+"("+ level +"%)");
            }
        }
    };
	
    @Override  
    protected void onPause() {  
    super.onPause();  
      
    unregisterReceiver(mBroadcastReceiver);  
    } 
	
	 protected void onResume() {
	        super.onResume();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
	        registerReceiver(mBroadcastReceiver, filter);
	    }
	 /*
	    “status”（int类型）…状态，定义值是BatteryManager.BATTERY_STATUS_XXX。
	    “health”（int类型）…健康，定义值是BatteryManager.BATTERY_HEALTH_XXX。
	    “present”（boolean类型）
	    “level”（int类型）…电池剩余容量
	    “scale”（int类型）…电池最大值。通常为100。
	    “icon-small”（int类型）…图标ID。
	    “plugged”（int类型）…连接的电源插座，定义值是BatteryManager.BATTERY_PLUGGED_XXX。
	    “voltage”（int类型）…mV。
	    “temperature”（int类型）…温度，0.1度单位。例如 表示197的时候，意思为19.7度。
	    “technology”（String类型）…电池类型，例如，Li-ion等等
	  */
	 
	 private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
	                int status = intent.getIntExtra("status", 0);
	                int health = intent.getIntExtra("health", 0);
	                boolean present = intent.getBooleanExtra("present", false);
	                int level = intent.getIntExtra("level", 0);
	                int scale = intent.getIntExtra("scale", 0);   // =100
	                int icon_small = intent.getIntExtra("icon-small", 0);
	                int plugged = intent.getIntExtra("plugged", 0);
	                int voltage = intent.getIntExtra("voltage", 0);
	                int temperature = intent.getIntExtra("temperature", 0);
	                String technology = intent.getStringExtra("technology");

	                String statusString = "";
	                switch (status) {
	                case BatteryManager.BATTERY_STATUS_UNKNOWN:
	                    statusString = "unknown";
	                    break;
	                case BatteryManager.BATTERY_STATUS_CHARGING:
	                    statusString = "charging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_DISCHARGING:
	                    statusString = "discharging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
	                    statusString = "not charging";
	                    break;
	                case BatteryManager.BATTERY_STATUS_FULL:
	                    statusString = "full";
	                    break;
	                }

	                String healthString = "";
	                switch (health) {
	                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
	                    healthString = "unknown";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_GOOD:
	                    healthString = "good";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
	                    healthString = "overheat";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_DEAD:
	                    healthString = "dead";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
	                    healthString = "voltage";
	                    break;
	                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
	                    healthString = "unspecified failure";
	                    break;
	                }

	                String acString = "";

	                switch (plugged) {
	                case BatteryManager.BATTERY_PLUGGED_AC:
	                    acString = "plugged ac";
	                    break;
	                case BatteryManager.BATTERY_PLUGGED_USB:
	                    acString = "plugged usb";
	                    break;
	                }
	                String s="^Battery Info: \n";
	                s = s
//	                + "status:"+statusString+"\n"
//	                +"health:"+healthString+"\n"
//	                +"present:"+String.valueOf(present)+"\n"
//	                +"\t\t\t\t\tlevel:"+String.valueOf(level)+"%"+"\n"
//	                +"scale:"+String.valueOf(scale)+"\n"
//	                +"icon_small:"+ String.valueOf(icon_small)+"\n"
//	                +"plugged:"+acString+"\n"
	                +"\t\t\t\t\tvoltage:"+String.valueOf(voltage)+" mV"+"\n"
//	                +"temperature:"+String.valueOf(temperature)+"\n"
//	                +"technology:"+technology+"\n"
	                ;
	                Message msg = new Message();
	                msg.what=MSG_REFRESH_UI;
	                
	                Bundle bundle = new Bundle();
	                bundle.putString("voltage", String.valueOf(voltage));
	                bundle.putString("status", statusString);
	                bundle.putString("level", String.valueOf(level));
	                msg.setData(bundle);
	                mhHandler.sendMessage(msg);
	                
//	                msg.obj = String.valueOf(voltage)+" mV";
//	                mhHandler.sendMessage(msg);
	            }
	        }
	    };
	    
	public String getVersion() {
		String version = null;

		try {

			PackageManager manager = this.getPackageManager();

			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

			version = info.versionName;

			return version;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return version;
	}

}
