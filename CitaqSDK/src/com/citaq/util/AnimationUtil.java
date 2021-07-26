package com.citaq.util;

import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

	private  TranslateAnimation mShowAnimation;
	private  TranslateAnimation mHideAnimation;
	
	public final static int hide = 1001;
	public final static int show = 1002;
	
	public AnimationUtil()
	{
		initAnimation();
	}
 

	public  void initAnimation(){
//	    TranslateAnimation(float fromXDelta, float toXDelta,
//                float fromYDelta, float toYDelta);
			//第一个参数fromXDelta为动画起始时 X坐标上的移动位置   
			//第二个参数toXDelta为动画结束时 X坐标上的移动位置      
			//第三个参数fromYDelta为动画起始时Y坐标上的移动位置     
			//第四个参数toYDelta为动画结束时Y坐标上的移动位置 
                
	    // 从自已-1倍的位置移到自己原来的位置  
        mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, 
        		Animation.RELATIVE_TO_SELF, 0.0f,  
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);  
        
        
        mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,  0.0f, 
        		Animation.RELATIVE_TO_SELF, -1.0f,  
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);  
        mShowAnimation.setDuration(500);  
        mHideAnimation.setDuration(500);  
			
	}
	
	public  void startAnimation(View view,int type){
		switch (type) {
		case hide:
			view.startAnimation(mHideAnimation);  
			view.setVisibility(View.INVISIBLE);  
			break;
		case show:
			view.startAnimation(mShowAnimation);  
			view.setVisibility(View.VISIBLE); 
			postHide(view);
			break;

		default:
			break;
		}
		
	}
	
	  Handler handler = new Handler();
	 Runnable runnable;
	 View rView;
	int timeOut = 3 * 1000;
	private  void postHide(View view){
		rView =view;
		runnable = new Runnable() {
			@Override
			public void run() {
				startAnimation(rView, hide);
				handler.removeCallbacks(this);
			}
	
		};
		handler.postDelayed(runnable, timeOut);// 打开定时器，执行操作
	}
}
