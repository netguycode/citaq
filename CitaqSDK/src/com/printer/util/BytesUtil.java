package com.printer.util;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.util.Base64;

public class BytesUtil {

	
	/**
     * 文件转化为字节数组
     * @param f
     * @return
     */
    public static byte[] getBytesFromFile(File f){
        if (f == null){
            return null;
        }
        try{
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 把字节数组保存为一个文件
     * @param b
     * @param outputFile
     * @return
     */
    public static File getFileFromBytes(byte[] b, String outputFile){
        BufferedOutputStream stream = null;
        File file = null;
        try{
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            if (stream != null){
                try{
                    stream.close();
                } catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
    
    /**
     * 从字节数组获取对象
     * @param objBytes
     * @return
     * @throws Exception
     */
    public static Object getObjectFromBytes(byte[] objBytes) throws Exception{
        if (objBytes == null || objBytes.length == 0){
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }
    
    /**
     * 从对象获取一个字节数组
     * @param obj
     * @return
     * @throws Exception
     */
    public static byte[] getBytesFromObject(Serializable obj) throws Exception{
        if (obj == null){
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }    
    
    /**
     * 字节数组转Base64字符串
     * @param data
     * @return
     */
    public static String getBase64StringFromBytes(byte[] data){
    	if(data == null || data.length <= 0){
    		return null;
    	}
    	return Base64.encodeToString(data, Base64.DEFAULT);
    }
    
    /**
     * Base64字符串转字节数组
     * @param base64string
     * @return
     */
    public static byte[] getBytesFromBase64String(String base64string){
    	if(base64string == null || base64string.equals("")){
    		return null;
    	}
    	return Base64.decode(base64string, Base64.DEFAULT);
    }
    
    /**
     * 字节数组转16进制字符串
     * @param data
     * @return
     */
    public static String getHexStringFromBytes(byte[] data){
    	if(data == null || data.length <= 0){
    		return null;
    	}    	
		String hexString = "0123456789ABCDEF";
		int size = data.length * 2;
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			sb.append(hexString.charAt((data[i] & 0xF0) >> 4));
			sb.append(hexString.charAt((data[i] & 0x0F) >> 0));
		}
		return sb.toString();    	
    }
    
    /**
     * 16进制字符串转字节数组
     * @param hexstring
     * @return
     */
    @SuppressLint("DefaultLocale") 
    public static byte[] getBytesFromHexString(String hexstring){
    	if(hexstring == null || hexstring.equals("")){
    		return null;
    	}
    	hexstring = hexstring.toUpperCase();
    	int size = hexstring.length()/2;
    	char[] hexarray = hexstring.toCharArray();
    	byte[] rv = new byte[size];
    	for(int i=0; i<size; i++){
            int pos = i * 2;  
            rv[i] = (byte) (charToByte(hexarray[pos]) << 4 | charToByte(hexarray[pos + 1]));     		
    	}
    	return rv;
    }

    public static byte[] getGbk(String paramString)
    {
		byte[] arrayOfByte = null;
		try 
		{
			arrayOfByte = paramString.getBytes("GBK");  //必须放在try内才可以
		}
		catch (Exception   ex) {
			ex.printStackTrace();
		}
		return arrayOfByte;
    }
    
    public static byte[] transToPrintText(String s){
		
		byte[] printText = new byte[4096];
		int iNum = 0;
		byte[] cmdData;
		String[] tmp=s.split(" ");
		
		for(int i=0;i<tmp.length ;i++){
			if(tmp[i].length()>0){
				cmdData=transCommandBytes(tmp[i]);
				System.arraycopy(cmdData, 0,  printText,  iNum,  cmdData.length);
				iNum += cmdData.length;
			}
		}
		
		cmdData = new byte[iNum];
		System.arraycopy(printText,0,cmdData,0,iNum);
		
		return cmdData;
	}
    
    public static byte[] transCommandBytes(String s){
		for(byte i=0;i<cmdBytes.length;i++){
			if(cmdBytes[i].equals(s)){
				return new byte[]{i};
			}
		}
		
		Pattern p1 = Pattern.compile("(\\d{1,3})([Dd]$)");  
		Pattern p2 = Pattern.compile("([0-9a-fA-F]{1,2})([Hh]$)");  
		Pattern p3=Pattern.compile("^0x([0-9a-fA-F]{1,2})");
		Matcher m;
		
		m=p1.matcher(s);
		if(m.matches()){
			int i=Integer.parseInt(m.group(1));
			if(i>255){
				return getGbk(s);
			}else{
				return new byte[]{(byte)i};				
			}
		}
		
		m=p2.matcher(s);
		if(m.matches()){
			return getBytesFromHexString(m.group(1));
		}
		
		m=p3.matcher(s);
		if(m.matches()){
			return getBytesFromHexString(m.group(1));
		}
		
		return getGbk(s);
	}
    
    private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    } 
    
    private static String[] cmdBytes=new String[]{
		"NUL","SOH","STX","ETX","EOT","ENQ","ACK","BEL",
	    "BS","HT","LF","VT","CLR","CR","SO","SI",
	    "DLE","DC1","DC2","DC3","DC4","NAK","SYN","ETB",
	    "CAN","EM","SUB","ESC","FS","GS","RS","US",
	    "SP"
	};
    
}
