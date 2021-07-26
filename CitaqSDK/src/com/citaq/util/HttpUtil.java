package com.citaq.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	private static final String TAG = "HttpUtil";
	private static HttpClient httpClient;
	
	static
	{
		openHttp(); 
    }
	
	public static void closeHttp(){
		if(httpClient !=null && httpClient.getConnectionManager()!=null){  
            httpClient.getConnectionManager().shutdown();  
            httpClient = null;
        }
	}
	
	private static void openHttp(){
		if(httpClient == null){
			Log.i(TAG, "Create HttpClient...");  
	        HttpParams params = new BasicHttpParams();  
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
	        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);  
	        HttpProtocolParams.setUseExpectContinue(params, true);  
	        SchemeRegistry schReg = new SchemeRegistry();  
	        schReg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),80));  
	        schReg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(),443));  
	        ThreadSafeClientConnManager  conMgr = new ThreadSafeClientConnManager(params, schReg);  
	        httpClient = new DefaultHttpClient(conMgr,params);  
		}
	}
	
	public static boolean httpString(String request) {
		if(TextUtils.isEmpty(request)){
			return false;
		}
		try{
			if(httpClient == null){
				return false;
			}

			HttpGet httpGet = new HttpGet(request); 
			HttpParams httpParameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParameters, 10000 );
		    HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		    httpGet.setParams(httpParameters);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200){
				return "0".equals(EntityUtils.toString(httpResponse.getEntity()));
				//return true;
			}
		}catch(Exception e){
		}
		return false;
	}

	public static boolean httpStringPost(String request, List<NameValuePair> paramList) {
		if(TextUtils.isEmpty(request)){
			return false;
		}
		try{
			if(httpClient == null){
				return false;
			}
			HttpPost post = new HttpPost(request); 
			
			post.setEntity(new UrlEncodedFormEntity(paramList,HTTP.UTF_8)); 
			
			//发送HttpPost请求，并返回HttpResponse对象 
			HttpResponse httpResponse = httpClient.execute(post); 
			// 判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求 
			if(httpResponse.getStatusLine().getStatusCode() == 200){ 
			//获取返回结果 
				return "0".equals(EntityUtils.toString(httpResponse.getEntity()));
				//return true;
			}
		}catch(Exception e){
		}
		return false;
	}
	
	public static String gethttpString(String request) {
		if(TextUtils.isEmpty(request)){
			return "";
		}
		try{
			if(httpClient == null){
				return "";
			}

			HttpGet httpGet = new HttpGet(request); 
			HttpParams httpParameters = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParameters, 10000 );
		    HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		    httpGet.setParams(httpParameters);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200){
				return (EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8));
			}
		}catch(Exception e){
		}
		return "";
	}
}
