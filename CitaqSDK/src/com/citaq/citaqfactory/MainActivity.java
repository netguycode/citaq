package com.citaq.citaqfactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Context mContext;
	Button bt_led;
	Button bt_print;
	Button bt_touch;
	Button bt_display;
	Button bt_music;
	Button bt_pd;
	Button bt_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initView();
	}

	private void initView() {
		bt_led = (Button) findViewById(R.id.bt_led);
		bt_print = (Button) findViewById(R.id.bt_print);
		bt_touch = (Button) findViewById(R.id.bt_touch);
		bt_display = (Button) findViewById(R.id.bt_display);
		bt_music = (Button) findViewById(R.id.bt_music);
		bt_pd = (Button) findViewById(R.id.bt_pd);
		bt_info = (Button) findViewById(R.id.bt_info);

		bt_led.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext, LedActivity.class));

			}
		});
		
		bt_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext, PrintActivity.class));
				
			}
		});

		bt_touch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext, TouchActivity.class));
			}
		});

		bt_display.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext, DisplayActivity.class));

			}
		});

		bt_music.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext,
						MusicPlayerActivity.class));

			}
		});
		
		bt_pd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext,
						PDActivity.class));
			}
		});
		
		bt_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mContext.startActivity(new Intent(mContext,
						SysInfoActivity.class));
				
			}
		});

	}

}
