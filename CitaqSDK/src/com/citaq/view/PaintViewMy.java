package com.citaq.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.citaq.citaqfactory.R;
import com.citaq.util.MessageUtil;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class PaintViewMy extends View {

	private static final int MAX_TOUCHPOINTS = 10;
	private static final int LINE_SPACE = 60;
	private float scale = 1.0f;
	
	private Canvas canvas;// 画布
	private Bitmap bitmap;// 位图
	private int mScreenWidth, mScreenHeight;

	private float mStartX[] = new float[MAX_TOUCHPOINTS];// 声明起点坐标
	private float mStartY[] = new float[MAX_TOUCHPOINTS];

	private float mPosX[] = new float[MAX_TOUCHPOINTS];// 声明终点坐标
	private float mPosY[] = new float[MAX_TOUCHPOINTS];

	private Paint bmgPaint = new Paint();
	private Paint textPaint = new Paint();
	private Paint touchPaints[] = new Paint[MAX_TOUCHPOINTS];
	private int colors[] = new int[MAX_TOUCHPOINTS];
	
	String title;

	public PaintViewMy(Context context) {
		super(context);
		title = context.getResources().getString(R.string.multi_touch_test);
		init();
	}

	public PaintViewMy(Context context, AttributeSet attrs) {
		super(context, attrs);
		title = context.getResources().getString(R.string.multi_touch_test);
		init();
	}

	public PaintViewMy(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		title = context.getResources().getString(R.string.multi_touch_test);
		init();
	}

	public void postInvalidate() {
		this.postInvalidate();
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 获得屏幕触点数量
		int pointerCount = event.getPointerCount();
		if (pointerCount > MAX_TOUCHPOINTS) {
			pointerCount = MAX_TOUCHPOINTS;
		}

		for (int i = 0; i < pointerCount; i++) {
			int id = event.getPointerId(i);
			mStartX[id] = event.getX(i);
			mStartY[id] = event.getY(i);
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {// 如果拖动
			for (int i = 0; i < pointerCount; i++) {
				int id = event.getPointerId(i);
				canvas.drawLine(mPosX[id], mPosY[id], mStartX[id], mStartY[id],
						touchPaints[id]);// 画线
			}
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {// 如果点击
			for (int i = 0; i < pointerCount; i++) {
				int id = event.getPointerId(i);
				mPosX[id] = mStartX[id];
				mPosY[id] = mStartY[id];
				canvas.drawPoint(mPosX[id], mPosY[id], touchPaints[id]);// 画点
			}
			invalidate();
		}
		for (int i = 0; i < pointerCount; i++) {
			int id = event.getPointerId(i);
			mPosX[id] = mStartX[id];
			mPosY[id] = mStartY[id];
		}
		return true;
	}

	// 清空画布
	public void clear() {
		bitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight,
				Bitmap.Config.ARGB_8888); // 设置位图的宽高
		canvas.setBitmap(bitmap);

		DrawBackGround();

		invalidate();
	}

	// 保存位图
	public boolean storeImageToFile(String name) {
		if (bitmap == null) {
			return false;
		}
		File file = null;
		RandomAccessFile accessFile = null;
		ByteArrayOutputStream steam = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam);
		byte[] buffer = steam.toByteArray();
		try {
			file = new File(name);
			accessFile = new RandomAccessFile(file, "rw");
			accessFile.write(buffer);
		} catch (Exception e) {
			MessageUtil.toast(getContext(), e.toString());
			return false;
		}
		try {
			steam.close();
			accessFile.close();
		} catch (IOException e) {
			MessageUtil.toast(getContext(), e.toString());
			return false;
		}
		MessageUtil.toast(getContext(),
				getContext().getString(R.string.save_info));
		return true;
	}

	// 画位图
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	private void init() {

		DisplayMetrics dm = getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels; // 获得屏幕的宽
		mScreenHeight = dm.heightPixels + 80; // 获得屏幕的高

		bmgPaint.setColor(Color.GRAY);
		bmgPaint.setStyle(Style.STROKE);
		bmgPaint.setStrokeWidth(2);
		
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(30 * scale);

		colors[0] = Color.BLUE;
		colors[1] = Color.RED;
		colors[2] = Color.GREEN;
		colors[3] = Color.YELLOW;
		colors[4] = Color.CYAN;
		colors[5] = Color.MAGENTA;
		colors[6] = Color.DKGRAY;
		colors[7] = Color.BLACK;
		colors[8] = Color.LTGRAY;
		colors[9] = Color.GRAY;
		for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
			touchPaints[i] = new Paint();
			touchPaints[i].setColor(colors[i]);
			touchPaints[i].setStrokeWidth(4);// 笔宽4像素
			touchPaints[i].setAntiAlias(false);// 锯齿显示
		}

		canvas = new Canvas();
		clear();
	}

	private void DrawBackGround() {
		canvas.drawColor(Color.WHITE);
		int i;
		for (i = LINE_SPACE; i < mScreenWidth; i += LINE_SPACE) {
			canvas.drawLine(i, 0, i, mScreenHeight, bmgPaint);
		}
		for (i = 0; i < mScreenHeight; i += LINE_SPACE) {
			canvas.drawLine(0, i, mScreenWidth, i, bmgPaint);
		}
		
//		float tWidth = textPaint.measureText(title);
//		canvas.drawText(title, mScreenWidth / 2 - tWidth / 2, mScreenHeight / 2, textPaint);
	}

}

