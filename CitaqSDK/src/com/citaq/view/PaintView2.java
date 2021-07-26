package com.citaq.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.citaq.citaqfactory.R;
import com.citaq.util.MessageUtil;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class PaintView2 extends View {

	 private float[] mov_x = new float[2];//声明起点坐标
	 private float[] mov_y = new float[2];
	 private float mov_x2=0;//声明起点坐标
	 private float mov_y2=0;
	 private Paint paint,paint2,bgPaint;//声明画笔
	 private Canvas canvas;//画布
	 private Bitmap bitmap;//位图
	 private int mScreenWidth, mScreenHeight, mBarHeight;
	 
	 private int count;
	 private int historySize;
	 private float[] historicalX = new float[2];
	 private float[] historicalY = new float[2];
	 
	public PaintView2(Context context) {
		super(context);
		init();
	}

	public PaintView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	 public PaintView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	 
	 public void postInvalidate(){
		 this.postInvalidate();
	 }
	
	 //触摸事件
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
	
	  float x1,y1,x2=mov_x2,y2=mov_y2;
	  int point = 0;
	  point = event.getPointerCount();
	  if(point==2){
		  x2=event.getX(1);
		  y2=event.getY(1);
	  }
	  x1=event.getX(0);
	  y1=event.getY(0);
	  
	  if (event.getAction()==MotionEvent.ACTION_MOVE) {//如果拖动
		  count++;
		  Log.d("ronfull", "count=" + count);
		  historySize = event.getHistorySize();  
		  Log.d("ronfull", "historySize=" + historySize);
          for (int i = 0; i < historySize; i++) {  
            //float historicalX = event.getHistoricalX(i);  
            //float historicalY = event.getHistoricalY(i);  
        	for(int j = 0; j < point; j++)
        	{
            historicalX[j] = event.getHistoricalX(j, i);
            historicalY[j] = event.getHistoricalY(j, i); 
            //float historicalX2= event.getHistoricalX(j, i);
            //float historicalY2= event.getHistoricalY(j, i);
            canvas.drawLine(mov_x[j],mov_y[j], historicalX[j], historicalY[j],paint); 
            //canvas.drawLine(mov_x2,mov_y2, historicalX2, historicalY2,paint2); 
            mov_x[j]= historicalX[j];
     	    mov_y[j]= historicalY[j];
     	    //mov_x2= historicalX2;
    	    //mov_y2= historicalY2;
        	}
          }  
//	   canvas.drawLine(mov_x, mov_y, x1, y1, paint);//画线
//	   canvas.drawLine(mov_x2, mov_y2, x2, y2, paint2);//画第二条线
	   invalidate();
	  }
	  if (event.getAction()==MotionEvent.ACTION_DOWN) {//如果点击
		  count = 0;
		  Log.d("ronfull", "count=" + count);
	   mov_x[0]= x1;
	   mov_y[0]= y1;
	   canvas.drawPoint(mov_x[0], mov_y[0], paint);//画点
	   mov_x[1]= x2;
	   mov_y[1]= y2;
	   canvas.drawPoint(mov_x[1], mov_y[1], paint2);//画第二点
	   invalidate();
	  }
	   mov_x[0]= x1;
	   mov_y[0]= y1;
	   
	   mov_x[1]= x2;
	   mov_y[1]= y2;
	  return true;
	 }
	 
	 //清空画布
	 public void clear()  
	 {  	     
		 bitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888); //设置位图的宽高
		 canvas.setBitmap(bitmap);
		 
		 DrawBackGround();  
		    
	     
	     invalidate();  
	 }  
	 
	 //保存位图
    public boolean storeImageToFile(String name){
    	if(bitmap == null){
    		return false;         
    	}         
    	File file = null;         
    	RandomAccessFile accessFile = null;         
    	ByteArrayOutputStream steam = new ByteArrayOutputStream();
    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam); 
    	byte[] buffer = steam.toByteArray();          
    	try 
    	{             
    		file = new File(name);             
    		accessFile = new RandomAccessFile(file, "rw");             
    		accessFile.write(buffer);         
    	} 
    	catch (Exception e) 
    	{             
    		MessageUtil.toast(getContext(), e.toString());
    		return false;         
    	}         
    	try 
    	{             
    		steam.close();             
    		accessFile.close();         
    	} 
    	catch (IOException e) 
    	{             
    		MessageUtil.toast(getContext(), e.toString());
    		return false;         
    	}
    	MessageUtil.toast(getContext(),getContext().getString(R.string.save_info));
    	return true;     
    	} 
    
    //画位图
 		 @Override
	protected void onDraw(Canvas canvas) {
		//  super.onDraw(canvas);
		canvas.drawBitmap(bitmap,0,0,null);
	}
	
	private void init() {
		WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
		

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
	    mScreenWidth = dm.widthPixels;   //获得屏幕的宽
	    mScreenHeight = dm.heightPixels+ 80;  //获得屏幕的高
	    mBarHeight=0;
	    
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);//创建一个画笔
		paint.setStyle(Style.FILL_AND_STROKE);//设置填充非填充
		paint.setStrokeWidth(4);//笔宽4像素
		paint.setColor(Color.RED);//设置为红笔
		paint.setAntiAlias(false);//锯齿显示
		
		paint2=new Paint(Paint.ANTI_ALIAS_FLAG);//创建一个画笔
		paint2.setStyle(Style.FILL_AND_STROKE);//设置填充非填充
		paint2.setStrokeWidth(4);//笔宽4像素
		paint2.setColor(Color.BLUE);//设置为蓝笔
		paint2.setAntiAlias(false);//锯齿显示
		
		bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(Color.GRAY);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(2);
		
	    canvas=new Canvas();
	    clear();
	}
	 
	private void DrawBackGround(){
		int i;
		for(i=60;i<mScreenWidth;i+=60){
			canvas.drawLine(i, 0, i, mScreenHeight, bgPaint);
		}
		for(i=0;i<mScreenHeight;i+=60){
			canvas.drawLine(0, i, mScreenWidth, i, bgPaint);
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.toolbar_v);
	    //canvas.drawBitmap(bmp, 0, 0,null);

	    RectF rectF = new RectF(0, 0, bmp.getWidth(), mScreenHeight);
//	    canvas.drawBitmap(bmp, null, rectF, null);  
	}

}
