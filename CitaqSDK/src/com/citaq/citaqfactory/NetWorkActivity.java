package com.citaq.citaqfactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import com.citaq.util.PingLooperThread;
import com.citaq.util.PingLooperThread.Callbak;
import com.citaq.util.WifiAdmin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NetWorkActivity extends Activity {
	protected static final String TAG = "NetWorkActivity";

	protected static final int wifi_state = 1001;
	protected static final int ping_state = 1002;

	private int networkType = 0; // NETWORK_TYPE_UNKNOWN
	

	ConnectivityManager connectivityManager;
	private WifiInfo wifiInfo = null; // 获得的Wifi信息
	private WifiManager wifiManager = null; // Wifi管理器

	TelephonyManager telephoneManager;
	int type;

	private int wifi_level;

	TextView tv_info;
	TextView tv_signal_strength;
	TextView tv_signal_strength_3G;
	TextView tv_ping_result;
	TextView tv_success_percentage;
	Button bt_start;
	Button bt_baidu;
	EditText et_network_addr;
	
	Spinner spinner_time_count;
	private ArrayAdapter<?> adapter_time;
	int pingTime =10;

	Resources mResources;

	boolean isRun = true;
//	int timeOut = 10 * 1000;

	Handler uiHandler;

//	Runnable runnable;

	Timer timer;

	Context mContext;
	
	private ToggleButton tb_wifi;
	
	WifiAdmin wifiAdmin;
	String[] defaultWifiInfo ={ "wifitest", "CitaqServerAp", "3"};
	
	ProgressBar mProgressBar;
	
	PingLooperThread mPingLooperThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_network);

		wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
		telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		wifiAdmin = new WifiAdmin(mContext);
	

		initView();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(myNetReceiver, mFilter);

		telephoneManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		type = telephoneManager.getNetworkType();
	}

	private void initView() {
		tv_ping_result = (TextView) findViewById(R.id.result);
		tv_success_percentage = (TextView) findViewById(R.id.success_percentage);
		bt_start = (Button) findViewById(R.id.begin);
		bt_baidu= (Button) findViewById(R.id.baidu);

		et_network_addr = (EditText) findViewById(R.id.web);
		et_network_addr.setSelection(et_network_addr.getText().length());
		et_network_addr.clearFocus();
		
		tb_wifi = (ToggleButton) findViewById(R.id.tb_wifi);
		mProgressBar = (ProgressBar) findViewById(R.id.myprobar);
		
		
		tv_info = (TextView) findViewById(R.id.tv_show_status);
		tv_signal_strength = (TextView) findViewById(R.id.tv_signal_strength);
		tv_signal_strength_3G = (TextView) findViewById(R.id.tv_signal_strength_3G);
		spinner_time_count = (Spinner) findViewById(R.id.spinner_time_count);
		
		adapter_time= ArrayAdapter.createFromResource(this, R.array.ping_time, android.R.layout.simple_spinner_item);
		adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_time_count.setAdapter(adapter_time);
		spinner_time_count.setSelection(0);
		spinner_time_count.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					pingTime = 10;
					break;
				case 1:
					pingTime = 12;
					break;
				case 2:
					pingTime = 14;
					break;
				case 3:
					pingTime = 16;
					break;
				}
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mResources = mContext.getResources();

		bt_start.setEnabled(false);
		et_network_addr.setEnabled(false);
		tv_signal_strength.setText("");

		uiHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				
				switch (msg.what) {
				case wifi_state:
					if (networkType == ConnectivityManager.TYPE_WIFI) {
						tv_signal_strength
								.setText(
								 mResources.getString(R.string.network_wifi)+","+
								mResources
										.getString(R.string.network_signal_strength)
										+ ":"
										+ msg.arg1
										+ "dBm");
//						if(Integer.valueOf(msg.what)!= -200)
						{
							tb_wifi.setEnabled(true);
				    		mProgressBar.setVisibility(View.INVISIBLE);
//				    		 System.out.println("1111111111111111111111");
						}
					}
					break;
				case ping_state:
					tv_ping_result.setText(mResources
					.getString(R.string.total_times)
					+ ":"
					+ (msg.arg1+msg.arg2)
					+ ";"
					+ mResources.getString(R.string.success_times)
					+ ":"
					+ msg.arg1
					+ ";"
					+ mResources.getString(R.string.fail_times)
					+ ":"
					+ msg.arg2);
					
					if(msg.arg1+msg.arg2 >0){
						tv_success_percentage.setText(mResources
								.getString(R.string.success_rate)
								+ ":"
								+ String.format("%.2f", (double)msg.arg1/(double)( msg.arg1+msg.arg2) * 100) +"%"
								);
					}
					break;

				default:
					break;
				}
				
				
				super.handleMessage(msg);
			}

		};

		bt_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bt_start.getTag().toString().equals(
						mResources.getString(R.string.start))) {

//					bt_start.setText(mResources.getString(R.string.stop));
					bt_start.setTag(mResources.getString(R.string.stop));
					bt_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pingstop_selector));
					et_network_addr.setEnabled(false);

					
					//ping
					mPingLooperThread.start();
					
					uiHandler.postDelayed(new Runnable() {  
					    @Override  
					    public void run() {  
					    	Message msg= new Message();
							mPingLooperThread.sendMessage(msg);
					        //要做的事情  
					        uiHandler.postDelayed(this, pingTime * 1000);  
					    }  
					}, 1000);
					//
					

				} else {

					// bt_start.setText(mResources.getString(R.string.start));
					bt_start.setTag(mResources.getString(R.string.start));
					bt_start.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.btn_ping_selector));
					et_network_addr.setEnabled(true);

					// stop ping
					mPingLooperThread.stop();
				}

			}
		});
		
		if(wifiManager.isWifiEnabled()){
			
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if(wifiInfo.getSSID().equals("\""+defaultWifiInfo[0]+"\"")){
				tb_wifi.setChecked(true);
			}
		}
		
		
		tb_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					
					bt_start.setTag(mResources.getString(R.string.start));
					bt_start.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.btn_ping_selector));
					et_network_addr.setEnabled(true);
					mPingLooperThread.stop();
					
					  new Thread(){
						  public void run(){
							  
							  
							  wifiAdmin.openWifi(); 
							  wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(defaultWifiInfo[0], defaultWifiInfo[1], Integer.valueOf(defaultWifiInfo[2]))); 
							 
						  }
						
					  }.start();
					  tb_wifi.setEnabled(false);
					  mProgressBar.setVisibility(View.VISIBLE);
					  
					  /*new Handler().postDelayed(new Runnable(){   

						    public void run() {   

						    	if(wifiManager.isWifiEnabled() ){
						    		tb_wifi.setEnabled(true);
						    		mProgressBar.setVisibility(View.INVISIBLE);
						    	}else{
						    		mProgressBar.setVisibility(View.INVISIBLE);
						    		tb_wifi.setChecked(false);
;						    		tb_wifi.setEnabled(true);
						    	}

						    }   

						 }, 20000);*/
					  
				    
				}else{
					tb_wifi.setEnabled(false);
					mProgressBar.setVisibility(View.VISIBLE);
					wifiAdmin.closeWifi();
				}

			}
		});
		
		bt_baidu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openBrowser();
				
			}
		});
		
		mPingLooperThread = new PingLooperThread(new Callbak() {

			@Override
			public boolean handleMessage(int allcount, int success, int faild) {

				Log.i(TAG, allcount + "   "+ success + "         "+faild);

				Message msg = new Message();
				msg.what = ping_state;
				msg.arg1 = success;
				msg.arg2 = faild;
				uiHandler.sendMessage(msg);
				
				return false;
			}
		}, pingTime,et_network_addr.getText().toString());
		
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		handler.removeCallbacks(runnable);
		if (myNetReceiver != null) {
			unregisterReceiver(myNetReceiver);
		}
	}



	private void networkRssi(int which) {
		bt_start.setEnabled(true);
		switch (which) {
		case ConnectivityManager.TYPE_WIFI:
			// 使用定时器,每隔5秒获得一次信号强度值
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					wifiInfo = wifiManager.getConnectionInfo();
					// 获得信号强度值
					wifi_level = wifiInfo.getRssi();
					// 根据获得的信号强度发送信息

					Message msg = new Message();
					msg.what = wifi_state;
					msg.arg1 = wifi_level;
					uiHandler.sendMessage(msg);

				}

			}, 1000, 5000);

			break;
		case ConnectivityManager.TYPE_MOBILE:
			tv_signal_strength.setText("");
			break;
		case ConnectivityManager.TYPE_ETHERNET:
			tv_signal_strength.setText("");
			break;

		default:
			break;
		}
	}

	private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = connectivityManager
						.getActiveNetworkInfo();

				if (netInfo != null && netInfo.isAvailable()) {

					// ///////////网络连接
					// String name = netInfo.getTypeName();
					networkType = netInfo.getType();

					if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						// ///WiFi网络
						networkRssi(ConnectivityManager.TYPE_WIFI);
//						tv_info.setText(netInfo.toString());
						tv_info.setText(mResources.getString(R.string.network_type)+":"+mResources.getString(R.string.network_wifi));
						et_network_addr.setEnabled(true);
						bt_start.setEnabled(true);
						
						
					} else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
						// ///有线网络
						networkRssi(ConnectivityManager.TYPE_ETHERNET);
//						tv_info.setText(netInfo.toString());
						tv_info.setText(mResources.getString(R.string.network_type)+":"+mResources.getString(R.string.network_ethernet));
						et_network_addr.setEnabled(true);
						bt_start.setEnabled(true);
						
					} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
						// ///////3g网络
						networkRssi(ConnectivityManager.TYPE_MOBILE);
//						tv_info.setText(netInfo.toString());
						tv_info.setText(mResources.getString(R.string.network_type)+":"+mResources.getString(R.string.network_mobile));
						et_network_addr.setEnabled(true);
						bt_start.setEnabled(true);
					}
				} else {
					// //////网络断开
					tv_info.setText(mResources
							.getString(R.string.network_not_connect));
//					bt_start.setText(mResources.getString(R.string.start));
					bt_start.setTag(mResources.getString(R.string.start));
					bt_start.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_ping_selector));
					bt_start.setEnabled(false);
					et_network_addr.setEnabled(false);
					tv_signal_strength.setText("");
					
					
					System.out.println("2222222222222222222222");
					tb_wifi.setChecked(false);
					tb_wifi.setEnabled(true);
					mProgressBar.setVisibility(View.INVISIBLE);
					
				}
			}

		}
	};

	/*
	　　signalStrength.isGsm() 是否GSM信号 2G or 3G
	　　signalStrength.getCdmaDbm(); 联通3G 信号强度
	　　signalStrength.getCdmaEcio(); 联通3G 载干比
	　　signalStrength.getEvdoDbm(); 电信3G 信号强度
	　　signalStrength.getEvdoEcio(); 电信3G 载干比
	　　signalStrength.getEvdoSnr(); 电信3G 信噪比
	　　signalStrength.getGsmSignalStrength(); 2G 信号强度
	　　signalStrength.getGsmBitErrorRate(); 2G 误码率
	　　载干比 ，它是指空中模拟电波中的信号与噪声的比值
	　　*/
	// 3G
	PhoneStateListener phoneStateListener = new PhoneStateListener() {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			StringBuffer sb = new StringBuffer();
			int asu = signalStrength
					.getGsmSignalStrength();
			String strength = String.valueOf(asu);
			String strengthdmb = String.valueOf(signalStrength.getCdmaDbm());
			if (type == TelephonyManager.NETWORK_TYPE_UMTS
					|| type == TelephonyManager.NETWORK_TYPE_HSDPA) {
				strengthdmb =String.valueOf(-113+(2*asu));
				// 联通3G
				sb.append(mResources.getString(R.string.network_Unicom_3G))
						.append(",").append(mResources.getString(R.string.network_signal_strength))
						.append(":").append(strengthdmb).append("dBm")
						.append("  ").append(strength).append("asu");

				tv_signal_strength_3G.setText(sb);

			} else if (type == TelephonyManager.NETWORK_TYPE_GPRS
					|| type == TelephonyManager.NETWORK_TYPE_EDGE) {
				// 移动或者联通2g
				sb.append(
						mResources
								.getString(R.string.network_ChinaMobile_Unicom_2G))
						.append(",").append(mResources.getString(R.string.network_signal_strength))
						.append(":").append(strengthdmb).append("dBm")
						.append("  ").append(strength).append("asu");
				tv_signal_strength_3G.setText(sb);
			} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
				// 电信3g
				strengthdmb = String.valueOf(signalStrength.getEvdoDbm());
				strengthdmb =String.valueOf(-113+(2*asu));
				sb.append(mResources.getString(R.string.network_CMCC_3G))
						.append(",").append(mResources.getString(R.string.network_signal_strength))
						.append(":").append(strengthdmb).append("dBm")
						.append("  ").append(strength).append("asu");
				tv_signal_strength_3G.setText(sb);
			} else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
				// Reflection code starts from here
				try {
					Method[] methods = android.telephony.SignalStrength.class
							.getMethods();
					for (Method mthd : methods) {
						if (mthd.getName().equals("getLteSignalStrength")
								|| mthd.getName().equals("getLteRsrp")
								|| mthd.getName().equals("getLteRsrq")
								|| mthd.getName().equals("getLteRssnr")
								|| mthd.getName().equals("getLteCqi")) {
							Log.i(TAG,"onSignalStrengthsChanged: "
											+ mthd.getName() + " "
											+ mthd.invoke(signalStrength));
							
							
							if(mthd.getName().equals("getLteRsrp")){
								int strengthdmb_int = (Integer) mthd.invoke(signalStrength);
								strengthdmb = String.valueOf(mthd.invoke(signalStrength));
								
								
								sb.append(mResources.getString(R.string.network_LTE))
								.append(",")
								.append(mResources
										.getString(R.string.network_signal_strength))
								.append(":").append(strengthdmb).append("dBm")
								.append("  ").append(strengthdmb_int+140).append("asu");
								tv_signal_strength_3G.setText(sb);
								
							}
							//LTE和2G算法不一样，LTE算法是dbm-aus=-140
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				// Reflection code ends here


			} else {
				sb.append(mResources.getString(R.string.network_Other))
						.append(",").append(mResources.getString(R.string.network_signal_strength))
						.append(":").append(strengthdmb).append("dBm")
						.append("  ").append(strength).append("asu");
//				tv_signal_strength_3G.setText(sb);
			}

		}

	};
	
	private void openBrowser(){
		Intent intent = new Intent();        
		intent.setAction("android.intent.action.VIEW");    
		Uri content_url = Uri.parse("http://www.baidu.com");   
		intent.setData(content_url);           
		intent.setClassName("com.android.browser","com.android.browser.BrowserActivity"); 
		if(getPackageManager().resolveActivity(intent, 0) == null) {
			//com.android.chrome/com.google.android.apps.chrome.Main
			intent.setClassName("com.android.chrome","com.google.android.apps.chrome.Main"); 
			if(getPackageManager().resolveActivity(intent, 0) == null) {
				Toast.makeText(this, "browser is not exit!!!", Toast.LENGTH_SHORT).show();
			}else{
				startActivity(intent);
			}
		}else{
			startActivity(intent);
		}
	}

}
