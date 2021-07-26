package com.citaq.citaqfactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.citaq.util.CitaqBuildConfig;
import com.citaq.util.SharePreferencesHelper;
import com.citaq.view.*;

public class MainActivity2 extends Activity {
	protected static final String Tag = "MainActivity2";
	ImageAdapter adapter;
	Context mContext;

	private static final int TITLE_LED = 0;
	private static final int TITLE_PRINT = 1;
	private static final int TITLE_TOUCH = 2;
	private static final int TITLE_DISPLAY = 3;
	private static final int TITLE_MUSIC = 4;
	private static final int TITLE_PD = 5;
	private static final int TITLE_MSR = 6;
	private static final int TITLE_MICROPHONE = 7;
	private static final int TITLE_NETWORK = 8;
	private static final int TITLE_FSKCALLERID = 9;
	private static final int TITLE_AGEING = 10;
	private static final int TITLE_INFO = 11;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.activity_main_gride);  

		GridView gridView = (GridView) findViewById(R.id.gridview);

		adapter = new ImageAdapter(this); 

		gridView.setAdapter(adapter);
		// 单击GridView元素的响应
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//
				switch (position) {
				case TITLE_LED:
					mContext.startActivity(new Intent(mContext, LedActivity.class));
					break;
				case TITLE_PRINT:
					mContext.startActivity(new Intent(mContext, PrintActivity.class));
					break;
				case TITLE_TOUCH:
					mContext.startActivity(new Intent(mContext, TouchActivity.class));
					break;
				case TITLE_DISPLAY:
					mContext.startActivity(new Intent(mContext, DisplayActivity.class));
					break;
				case TITLE_MUSIC:
					mContext.startActivity(new Intent(mContext,
							MusicPlayerActivity.class));
					break;
				case TITLE_PD:
					mContext.startActivity(new Intent(mContext,
							PDActivity.class));
					break;
				case TITLE_MSR:
					mContext.startActivity(new Intent(mContext,
							MSRActivity.class));
					break;
				case TITLE_MICROPHONE:
					mContext.startActivity(new Intent(mContext,
							MicrophoneActivity.class));
					break;
				case TITLE_NETWORK:
					mContext.startActivity(new Intent(mContext,
							NetWorkActivity.class));
					break;
				case TITLE_AGEING:
					mContext.startActivity(new Intent(mContext,
							AgeingActivity.class));
					break;
				case TITLE_FSKCALLERID:
					mContext.startActivity(new Intent(mContext,
							FSKCALLERIDActivity.class));
					break;					
				case TITLE_INFO:
					mContext.startActivity(new Intent(mContext,
							SysInfoActivity.class));
					break;
					

				}

			}

		});
		
		getScreenDensity_ByWindowManager();

	}
	
	 public void getScreenDensity_ByWindowManager(){  
		    DisplayMetrics mDisplayMetrics = new DisplayMetrics();//屏幕分辨率容器  
		    getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);  
		    int width = mDisplayMetrics.widthPixels;  
		    int height = mDisplayMetrics.heightPixels;  
		    float density = mDisplayMetrics.density;  
		    int densityDpi = mDisplayMetrics.densityDpi;  
		    Log.d(Tag,"Screen Ratio: ["+width+"x"+height+"],density="+density+",densityDpi="+densityDpi);  
		    Log.d(Tag,"Screen mDisplayMetrics: "+mDisplayMetrics);  
		}
	 
	 @Override
	protected void onDestroy() {
		if(BuildConfig.DEBUG) Log.i(Tag, "onDestroy----");
		
		SharePreferencesHelper mSharePreferencesHelper = new SharePreferencesHelper(this,CitaqBuildConfig.SHAREPREFERENCESNAME);
		mSharePreferencesHelper.clear();
		
		super.onDestroy();
		
	}

}
