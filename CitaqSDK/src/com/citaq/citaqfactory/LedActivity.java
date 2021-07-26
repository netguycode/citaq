package com.citaq.citaqfactory;


import com.citaq.util.LEDControl;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class LedActivity extends Activity {

	private ToggleButton bt_red;
	private ToggleButton bt_blue;
	private ToggleButton bt_fresh;
	
	private LEDControl freshThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_led);

		initView();
	}

	private void initView() {
		bt_red = (ToggleButton) findViewById(R.id.tb_red);
		bt_blue = (ToggleButton) findViewById(R.id.tb_blue);
		bt_fresh = (ToggleButton) findViewById(R.id.tb_fresh);

		bt_red.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					LEDControl.trunOnRedRight(true);
				}else{
					LEDControl.trunOnRedRight(false);
				}
			}
		});

		bt_blue.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					LEDControl.trunOnBlueRight(true);
				}else{
					LEDControl.trunOnBlueRight(false);
				}

			}
		});

		bt_fresh.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					if(freshThread == null)
					{
						freshThread = new LEDControl();
						freshThread.StartFresh();
					}
				}else{
					if(freshThread != null)
					{
						freshThread.StopFresh();
						freshThread = null;
					}
				}
			}
		});

	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LEDControl.trunOnRedRight(false);
		LEDControl.trunOnBlueRight(false);
		if(freshThread != null)
		{
			freshThread.StopFresh();
			freshThread = null;
		}
	}

}
