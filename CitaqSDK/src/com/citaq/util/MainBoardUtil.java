package com.citaq.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;


public class MainBoardUtil {

	public static final String SMDKV210 = "SMDKV210";
	public static final String RK3188 = "RK30BOARD";
	public static final String RK30BOARD = "SUN50IW1P1";
	public static final String MSM8625Q = "QRD MSM8625Q SKUD";
	public static final String RK3368 = "RK3368";
	
	
    public static String getCpuHardware() {
    	
        String hardware = "";
        String str = "";
        try {
                Process pp = Runtime.getRuntime().exec(
                                "cat /proc/cpuinfo");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                
                while((str = input.readLine()) != null){
                	if (str.startsWith("Hardware")){
                		int i = str.indexOf(":");
                		hardware = str.substring(i+1).trim().toUpperCase();
                		return hardware;
                	}
                }
                
        } catch (IOException ex) {
                //
                ex.printStackTrace();
        }
        return hardware;
    }
	
	public static String getModel(){
		String rs = "unknow board";
		
		String hw = getCpuHardware();
		
		if(hw.contains(SMDKV210)){
			rs = "smdkv210";
		}else if(hw.contains(RK3188)){
			rs = "rk30sdk";
		}else if(hw.contains(MSM8625Q)){
			rs = "c500";
		}
		
		return rs;
	}

}
