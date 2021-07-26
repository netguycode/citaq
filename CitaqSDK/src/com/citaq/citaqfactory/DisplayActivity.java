package com.citaq.citaqfactory;

import com.citaq.util.SoundManager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public  class DisplayActivity extends FullActivity implements OnClickListener {

	private int i = 1;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//      requestWindowFeature(Window.FEATURE_NO_TITLE);
		View temp=new View(this);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		this.setContentView(temp);
		temp.setOnClickListener(this);
		temp.setBackgroundColor(colors[0]);
	}
	
	boolean exit = false;
	@Override
		public void onClick(View v) {
			SoundManager.playSound(0, 1);
			
			if(i>colors.length-1)
			{
				
				if(exit){
					finish();
				}else{
					 GradientDrawable grad = new GradientDrawable(//渐变色  
					            Orientation.BL_TR,  
					            new int[]{Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE}  
					        );  
					        v.setBackgroundDrawable(grad);
					        exit = true;
				}
				
				/*i=0;
				v.setBackgroundColor(colors[i++]);*/
			}
			else
			{
				v.setBackgroundColor(colors[i++]);
				
				
			}
		}
	
	private static int[] colors={
//		0x00000000,
		0xff000000,//黑
		0xffffffff,//白
		0xff0000ff,//蓝
		0xff00ff00,//绿
		0xffff0000,//红
//		0xff00ffff,
//		0xffff00ff,
		0xffffff00,		
		};
}
