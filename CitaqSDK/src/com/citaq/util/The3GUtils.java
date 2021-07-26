package com.citaq.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

public class The3GUtils {
	
    public static void reset3G(Context context, boolean enable){
    	///////////////////5.1 没有权限/////////////////////////
//    	 try {
//	            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//	            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
//
//	            if (null != setMobileDataEnabledMethod) {
//	                setMobileDataEnabledMethod.invoke(telephonyService, enable);
//	            }
    	
    	
    	
    	
          ///////////////////5.1 这个方法/////////////////////////
    	
		 ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        Class<?> cmClass = connManager.getClass();    
	        Class<?>[] argClasses = new Class[1];    
	        argClasses[0] = boolean.class;    
	        // 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络    
	        
	        try {
	        	Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);    
				method.invoke(connManager, enable);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
    
    
   private static int sumForReset3G = 0;
	public static int get3GResetSum() {
		// TODO Auto-generated method stub
		return sumForReset3G;
	}
	
	public static void reset3GCount(){
		sumForReset3G++;
	}

}
