package com.citaq.citaqfactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

@SuppressLint("NewApi")
public abstract class FullActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		hideNavigationBar();
		
		super.onCreate(savedInstanceState);
	}
	
	public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if( android.os.Build.VERSION.SDK_INT >= 19 ){ 
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

}
