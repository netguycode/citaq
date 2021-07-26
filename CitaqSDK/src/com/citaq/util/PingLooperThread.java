package com.citaq.util;

/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2014, JUN LU <lujun.hust@gmail.com>
 *  All rights reserved.  
 *   
 *  @file    LooperThread.java 
 *  @brief   带Looper的线程封装
 *  
 *  演示带Looper的线程原理
 *  
 *  @version 1.0     
 *  @author  卢俊
 *  @date    2014/10/15  
 * 
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Message;

public class PingLooperThread {
    private volatile boolean mIsLooperQuit = false;
		
    private Thread mThread;
    private Callbak mCallbak;
    
    private Lock mLock = new ReentrantLock();
    private Condition mCondition = mLock.newCondition();
    private Queue<Message> mMessageQueue = new LinkedList<Message>();
    
    int delayTime =0;
    String pingWeb;
    
    public int allcount = 0;
	public int success = 0;
	public int faild = 0;
    
//    public static class Message {
//
//     int what;
//    	
//    }
    
    public static interface Callbak {
    	public boolean handleMessage(int allcount, int success, int faild);
    }
    
    public PingLooperThread( Callbak callback, int time , String web) {
    	mCallbak = callback;
    	delayTime = time;
    	pingWeb = web;
    }

	public void start() {		
		if( mThread != null ) {
			return;
		}		
		mIsLooperQuit = false;
		mThread = new Thread(mLooperRunnable);
		mThread.start();		
	}
	
	public void stop() {		
		if( mThread == null ) {
			return;
		}		
		mIsLooperQuit = true;	
		
		mLock.lock();    	
    	mCondition.signal();
    	mLock.unlock();
    	
		mThread.interrupt();
		try {
			mThread.join(1000);
		} 
		catch (InterruptedException e) {		
			e.printStackTrace();
		}
		mMessageQueue.clear();
		mThread = null;		
	}
	
	//发送消息，由外部其他模块调用，发送消息给线程
	public void sendMessage( Message message ) {
		if( mThread == null ) {
			return;
		}		
		mLock.lock();
    	mMessageQueue.add(message);//添加消息到消息队列
    	mCondition.signal(); //通知线程循环，有消息来了，请立即处理
    	mLock.unlock();
	}

	protected Runnable mLooperRunnable = new Runnable() {		
		
		@Override
		public void run() {
			
			while( !mIsLooperQuit ) {
				
				mLock.lock();
				
				Message message = null;
				try {			
					while( !mIsLooperQuit && mMessageQueue.isEmpty() ) {				
						mCondition.await();
					} 
					message = mMessageQueue.poll();					
				}
				catch (InterruptedException e) {
					e.printStackTrace();			
				}
				finally {
					mLock.unlock();
				}									
				
				if( mCallbak != null && message != null ) {
					
					Ping(pingWeb);
//					mCallbak.handleMessage(message);
				}									
			}
		}
	};	
	
	public void Ping(String web) {

		Process p;
		// 10 packets transmitted, 10 received, 0% packet loss, time 9030ms

		try {
			p = Runtime.getRuntime().exec("ping -c 1 " + web);
			int status = p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = buf.readLine()) != null) {
				buffer.append(line);
			}
			allcount++;
//			allcount++;
			// System.out.println("Return ============" +
			// buffer.toString());
			if (status == 0) {
			success++;
			} else {
			faild++;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		mCallbak.handleMessage(allcount,success, faild);

	}
}
