package com.citaq.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressLint("NewApi")
public class RAMandROMInfo {
	private static final String TAG = "RAMandROMInfo";
	
	Context context;
	
	
	public RAMandROMInfo(Context ctx) {
		context = ctx;
//		 showRAMInfo();
//		 
//		 showROMInfo();
//		 
//		 showSDInfo();
	}
	
	
	/*显示RAM的可用和总容量，RAM相当于电脑的内存条*/  
    public String showRAMInfo(){  
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo mi=new MemoryInfo();  
        am.getMemoryInfo(mi);  
        String[] available=fileSize(mi.availMem);  
        String[] total=fileSize(mi.totalMem);  
  //      Log.i(TAG, "RAM "+available[0]+available[1]+"/"+total[0]+total[1]) ;
        
        return total[0]+total[1];
    }  
    
    public String showRAMInfo2(){//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";//返回1GB/2GB/3GB/4GB
    }
    
    
    public String showROMInfo2(){//GB
        String path = "/proc/partitions";
        String line = null;
        Float totalRom = (float) 0;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            
            while ((line = br.readLine()) != null) {//如果之前文件为空，则不执 行输出
            	if(line.endsWith("mmcblk0")|| line.endsWith("zram0")){
            		String  blk = line.split("\\s+")[3];
            		if(isNumeric(blk)){
            			
            			totalRom =totalRom+ Float.valueOf(blk);
            		}
            		
            	}

            }
            
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
       
          int   total = (int)Math.ceil((new Float(totalRom/ (1024 * 1024)).doubleValue()));
        

        return total + "GB";//返回1GB/2GB/3GB/4GB
    }
    
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
 }
    
/*	public static String getTotalRam(){//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";//返回1GB/2GB/3GB/4GB
    }*/
    
	/*显示ROM的可用和总容量，ROM相当于电脑的C盘*/  
    public String showROMInfo(){  
        File file=Environment.getDataDirectory();   
        StatFs statFs=new StatFs(file.getPath());    
        long blockSize=statFs.getBlockSize();    
        long totalBlocks=statFs.getBlockCount();    
        long availableBlocks=statFs.getAvailableBlocks();    
            
        String[] total=fileSize(totalBlocks*blockSize);    
        String[] available=fileSize(availableBlocks*blockSize);   
        
        Log.i(TAG, "ROM "+available[0]+available[1]+"/"+total[0]+total[1]) ;  
        
        return "ROM "+available[0]+available[1]+"/"+total[0]+total[1];
    }  
    
    
    
    /*显示SD卡的可用和总容量，SD卡就相当于电脑C盘以外的硬盘*/  
    private void showSDInfo(){  
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){    
            File file=Environment.getExternalStorageDirectory();  //  /mnt/internal_sd
            StatFs statFs=new StatFs(file.getPath());    
            long blockSize=statFs.getBlockSize();    
            long totalBlocks=statFs.getBlockCount();    
            long availableBlocks=statFs.getAvailableBlocks();    
                
            String[] total=fileSize(totalBlocks*blockSize);    
            String[] available=fileSize(availableBlocks*blockSize);    
                
            Log.i(TAG, "SD "+available[0]+available[1]+"/"+total[0]+total[1]) ;   
        }else {    
            
            Log.i(TAG, "SD card removed") ;   
        }    
    }  
    /*返回为字符串数组[0]为大小[1]为单位KB或者MB*/    
    private String[] fileSize(long size){    
        String str="";    
        if(size>=1024){    
            str="KB";    
            size/=1000;    
            if(size>=1024){    
                str="MB";    
                size/=1024;    
            }    
            if(size>=1024){    
                str="GB";    
                size/=1024;    
            } 
        }    
        /*将每3个数字用,分隔如:1,000*/    
        DecimalFormat formatter=new DecimalFormat();    
        formatter.setGroupingSize(3);    
        String result[]=new String[2];    
        result[0]=formatter.format(size);    
        result[1]=str;    
        return result;    
    }    
}
