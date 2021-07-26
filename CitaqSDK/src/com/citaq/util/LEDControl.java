package com.citaq.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.example.gpioled.PosCtrl;

public class LEDControl extends Thread {
	
	//////8625Q/////////////////////
	private static PosCtrl mPosCtrlOp=null;	
	private static boolean RedLightOn=false;
	private static boolean BlueLightOn=false;
	
	public static PosCtrl getPosCtrl()
	{
		if(mPosCtrlOp==null)
			mPosCtrlOp=new PosCtrl();
		return mPosCtrlOp;
	}

	public static boolean isRedLightOn(){
		RedLightOn=getPosCtrl().isRedlightOn();
		return RedLightOn;
	}
	
	public static boolean isBlueLightOn(){
		BlueLightOn=getPosCtrl().isYellowlightOn();
		return BlueLightOn;
	}
	
//	public static void trunOnRedRight(boolean isOn){
//		if(isOn !=isRedLightOn()){
//			getPosCtrl().trunOnoffRedlight(isOn);
//		}
//	}
	
//	public static void trunOnBlueRight(boolean isOn){
//		if(isOn !=isBlueLightOn()){
//			getPosCtrl().trunOnoffYellowlight(isOn);
//		}
//	}
	//////////////////////////////////////////////
		
	private  boolean trunoff=false;
	
	public void run() {
		try {
			while(!trunoff && !this.isInterrupted())
			{
				if(trunoff)return;
				trunOnBlueRight(true);
				Thread.sleep(150);
				trunOnRedRight(true);
				Thread.sleep(150);
				trunOnBlueRight(false);
				Thread.sleep(150);
				trunOnRedRight(false);
				Thread.sleep(150);	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    }
	
	public void StartFresh(){
		start();
    }
    
    public void StopFresh(){
    	trunoff=true;
    }
    
    ///////////////RK3188///////////////////////////
    private static String RedLightFileName_3188 = "/sys/class/gpio/gpio190/value";
    private static String BlueLightFileName_3188 = "/sys/class/gpio/gpio172/value";

    ///////////////RK3368///////////////////////////
    private static String RedLightFileName_3368 = "/sys/class/gpio/gpio124/value";
	private static String BlueLightFileName_3368 = "/sys/class/gpio/gpio106/value";
	

	public static int TurnRedLedOnoff(String onff, String fileName)
    {
    	File f_red_led = new File(fileName);//red led

    	OutputStream outRed = null;

    	byte[] b = onff.getBytes();

    	int ret = 0;
    	try 
    	{
    		outRed = new FileOutputStream(f_red_led);

        	outRed.write(b);
        	outRed.flush();
        	
        	ret = 1;
    	} 
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	finally
    	{
    		try
    		{
    			outRed.close();
    		} 
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return ret;
    }
    
    public static int TurnBlueLedOnoff(String onff, String fileName)
    {
    	File f_blue_led = new File(fileName);//blue led

    	OutputStream outBlue = null;
    	byte[] b = onff.getBytes();

    	int ret = 0;
    	try 
    	{
        	outBlue = new FileOutputStream(f_blue_led);

        	outBlue.write(b);
        	outBlue.flush();
        	
        	ret = 1;
    	} 
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	finally
    	{
    		try
    		{
    			outBlue.close();
    		} 
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return ret;
    }

	public static String isLightOn(String fileName)// 
	{
		//LightUtil temp = new LightUtil();
		String str = "";
		File f_led = new File(fileName);
		//FileInputStream istream = this.context.openFileInput("/sys/class/gpio/gpio172/value");
		try {
			FileInputStream inputStream = new FileInputStream(f_led);//temp.openFileInput(fileName);  
            byte[] bytes = new byte[1024];  
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
            while (inputStream.read(bytes) != -1) {  
                arrayOutputStream.write(bytes, 0, bytes.length);  
            }  
            inputStream.close();  
            arrayOutputStream.close();  
            str = new String(arrayOutputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return str;
	}
	
	
	public static void trunOnRedRight(boolean isOn)
	{
		if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK3188)
				|| MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK30BOARD)){
			
			
			if(isOn == true)
			{
				if(isLightOn(RedLightFileName_3188).contains("0"))
				{
					TurnRedLedOnoff("1",RedLightFileName_3188);
				}
			}
			else
			{
				if(isLightOn(RedLightFileName_3188).contains("1"))
				{
					TurnRedLedOnoff("0",RedLightFileName_3188);
				}
			}
		}else if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.MSM8625Q)){
			if(isOn !=isRedLightOn()){
				getPosCtrl().trunOnoffRedlight(isOn);
			}
		}else if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK3368)){
			if(isOn == true)
			{
				if(isLightOn(RedLightFileName_3368).contains("0"))
				{
					TurnRedLedOnoff("1",RedLightFileName_3368);
				}
			}
			else
			{
				if(isLightOn(RedLightFileName_3368).contains("1"))
				{
					TurnRedLedOnoff("0",RedLightFileName_3368);
				}
			}
		}
	}
	
	public static void trunOnBlueRight(boolean isOn)
	{
		if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK3188)
				|| MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK30BOARD)){
			if(isOn == true)
			{
				if(isLightOn(BlueLightFileName_3188).contains("0"))
				{
					TurnBlueLedOnoff("1",BlueLightFileName_3188);
				}
			}
			else
			{
				if(isLightOn(BlueLightFileName_3188).contains("1"))
				{
					TurnBlueLedOnoff("0",BlueLightFileName_3188);
				}
			}
		}else if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.MSM8625Q)){
			if(isOn !=isBlueLightOn()){
				getPosCtrl().trunOnoffYellowlight(isOn);
			}
		}else if(MainBoardUtil.getCpuHardware().contains(MainBoardUtil.RK3368)){
			if(isOn == true)
			{
				if(isLightOn(BlueLightFileName_3368).contains("0"))
				{
					TurnBlueLedOnoff("1",BlueLightFileName_3368);
				}
			}
			else
			{
				if(isLightOn(BlueLightFileName_3368).contains("1"))
				{
					TurnBlueLedOnoff("0",BlueLightFileName_3368);
				}
			}
		}
	}
	////////////////////////////////////////
}
