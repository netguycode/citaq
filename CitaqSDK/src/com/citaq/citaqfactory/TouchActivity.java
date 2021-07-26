package com.citaq.citaqfactory;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.citaq.util.AnimationUtil;
import com.citaq.util.SoundManager;
import com.citaq.view.PaintView;
import com.citaq.view.PaintViewMy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TouchActivity extends FullActivity 
		implements OnClickListener, OnTouchListener,
        OnGestureListener{
	
	private ImageView btnClear,btnSave,btnBack;
	private PaintViewMy mPaintView;
	private LayoutInflater mInfater;
	private View touchView;
	private View touchbarView;
	
	private AnimationUtil mAnimationUtil;
	
	private static Handler sHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		*/
		
		/*Intent intent = new Intent();
		intent.setAction("com.android.action.hide_navigationbar");
		sendBroadcast(intent);*/

		mInfater = LayoutInflater.from(this);
	    touchView = mInfater.inflate(R.layout.activity_touch, null);
		this.setContentView(touchView);
		initView(); //
	}
	
	private void initView(){
		btnClear=(ImageView)this.findViewById(R.id.button_clear);
		btnClear.setOnClickListener(this);
		
        btnSave=(ImageView)this.findViewById(R.id.button_save);
        btnSave.setOnClickListener(this);
        
        btnBack=(ImageView)this.findViewById(R.id.button_back);
		btnBack.setOnClickListener(this);

		mPaintView=(PaintViewMy)this.findViewById(R.id.paintview);
		
		touchbarView = this.findViewById(R.id.touchBar);
		// 为布局绑定监听
		mPaintView.setOnTouchListener(this);
		
		mAnimationUtil =new AnimationUtil();
	
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onClick(View v) {
		SoundManager.playSound(0, 1);
		switch(v.getId())
		{
			case R.id.button_clear:
				mPaintView.clear();
				break;
			case R.id.button_save:
				String name = new SimpleDateFormat("yyyyMMddHHmm", Locale.SIMPLIFIED_CHINESE).format(new Date());
				name = Environment.getExternalStorageDirectory()+"/"+name+".png";
				mPaintView.storeImageToFile(name);
				break;
			case R.id.button_back:
				this.finish();
				
				break;
			default:
				break;
		}
		
	}




	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	private GestureDetector detector = new GestureDetector(this);
    // 限制最小移动像素
    private int FLING_MIN_DISTANCE = 20;
    private int FLING_MAX_DISTANCE = 350;
    // 定义的Toast提示框显示时间
    private int TIME_OUT = 1000;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
            // 向左滑动
//            Toast.makeText(this, "向左滑动", TIME_OUT).show();
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
            // 向右滑动
//            Toast.makeText(this, "向右滑动", TIME_OUT).show();
        	if(e1.getX()< 50 && e2.getX() - e1.getX()<FLING_MAX_DISTANCE){
        		
        		mAnimationUtil.startAnimation(touchbarView, AnimationUtil.show);
        		
        	}
//        	 Toast.makeText(this, "--"+(int)(e2.getX() - e1.getX()), TIME_OUT).show();
        }
        return false;
	}


	


	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		detector.onTouchEvent(event);
        return false;
	}

}
