package com.printer.util;

import java.util.LinkedList;

import android.util.Log;

public class DataQueue {
	private static final String TAG = "DataQueue";
	private LinkedList<byte[]> list = new LinkedList<byte[]>();
	
	
	public void clear()//销毁队列
	  {
	      list.clear();
	  }
	  public boolean QueueEmpty()//判断队列是否为空
	  {
	      return list.isEmpty();
	  }
	  public void enQueue(byte[] cmd, boolean neddSubpackage, int len)//进队
	  {
		  synchronized(list) {
	 
			  if(neddSubpackage){
					int offset = 0;
					int pkLen = len;
					
					int total = cmd.length;
					
					
					while((offset+pkLen) <= total){
						byte[] printText = new byte[pkLen];
						System.arraycopy(cmd, offset, printText, 0, pkLen);
						list.addLast(printText);
						offset += pkLen;
					}
					
					if(offset < total){
						byte[] printText = new byte[total-offset];
						System.arraycopy(cmd, offset, printText, 0, total-offset);
						list.addLast(printText);
					}		
					
				}else{
					list.addLast(cmd);
				}
		  
		  } 
	     
	  }
	  public byte[] deQueue()//出队
	  {
		  synchronized(list){
		      if(!list.isEmpty())
		      {
		    	
			    	  Log.i(TAG, "QueueLength()=" + QueueLength());
			          return list.removeFirst();
		    	  }
		  }
	      return null;
	  }
	  public int QueueLength()//获取队列长度
	  {
	      return list.size();
	  }
	  public Object QueuePeek()//查看队首元素
	  {
	      return list.getFirst();
	  }

}
