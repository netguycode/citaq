package com.citaq.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InterAddressUtil {
	public static String loadFileAsString(String filePath) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath)); 
		char[] buf = new char[1024]; int numRead=0; 
		while((numRead=reader.read(buf)) != -1){ 
			String readData = String.valueOf(buf, 0, numRead); 
			fileData.append(readData); 
		} 
		reader.close(); 
		return fileData.toString();
	}/** Get the STB MacAddress*/
	
	public static String getMacAddress(){
		try { 
			return loadFileAsString("/sys/class/net/eth0/address") .toUpperCase().substring(0, 17);
			} catch (IOException e) { 
				e.printStackTrace(); 
				return null;
			}
	}
	
	public static String getLocalIpAddress() {
        try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                                .getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement(); 
                        for (Enumeration<InetAddress> enumIpAddr = intf
                                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress()) { 
                                        return inetAddress.getHostAddress().toString();
                                }
                        }
                }
        } catch (SocketException ex) {
                System.out.println("WifiPreference IpAddress"+ex.toString());
        }
        return null;
}
}
