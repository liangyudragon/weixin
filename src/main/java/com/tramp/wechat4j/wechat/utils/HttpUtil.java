package com.tramp.wechat4j.wechat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


/**
 * http访问
 * @version 1.0 
 */
public class HttpUtil {
		
	
	@SuppressWarnings("deprecation")
	public static String getByHttpClient(String requestUrl, Map<String, Object> params) throws Exception{
		String result = null;
		
		
		
			CloseableHttpClient httpclient = HttpClients.createDefault();
			//请求超时
			httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			//读取超时
			httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
			if (params!=null && params.size()>0) {
				for(String key : params.keySet()) {
					if (params.get(key)!=null) {
						requestUrl += "&" + key + "=" + params.get(key);
					}
				}
			}
			
			URL url = new URL(requestUrl);
			URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
			
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setConfig(requestConfig);
			
	        CloseableHttpResponse response = httpclient.execute(httpGet);
	        
	        result = HttpUtil.getJSONStringFromResponse(response);
        
		
		
		return result;
	}

	
	public static String postByHttpClient(String requestUrl, Map<String, String> params, String sessionId) {
		String result = null;
		
		try {
		
			CloseableHttpClient httpclient = HttpClients.createDefault();
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build();//设置请求和传输超时时间
			
			HttpPost httpPost = new HttpPost(requestUrl);
			
			httpPost.setConfig(requestConfig);
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			
			if(sessionId != null) {
				httpPost.setHeader("Cookie", "JSESSIONID=" + sessionId);
			}
			
			for(String key : params.keySet()) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
			}
			
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
	        CloseableHttpResponse response = httpclient.execute(httpPost);
	        
	        result = HttpUtil.getJSONStringFromResponse(response);
        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public static String getJSONStringFromResponse(HttpResponse response) {
		StringBuilder buffer = new StringBuilder();
		
		if (response != null) {
			try {
				HttpEntity entity = response.getEntity();
				// do something useful with the response body
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

				String s;

				while ((s = reader.readLine()) != null) {
					buffer.append(s);
				}
				reader.close();
				
				//String data = EntityUtils.toString(entity);

				EntityUtils.consume(entity);
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}
		System.out.println("--"+buffer.toString());
		return buffer.toString();
	}
	
	public static InputStream HttpPostWithJson(String url, String json) {  
		InputStream iStream=null;
        CloseableHttpClient httpClient = HttpClients.createDefault();  
       // ResponseHandler<String> responseHandler = new BasicResponseHandler();  
        try{  
            //第一步：创建HttpClient对象  
        	httpClient = HttpClients.createDefault();  
              
            //第二步：创建httpPost对象  
            HttpPost httpPost = new HttpPost(url);  
              
            //第三步：给httpPost设置JSON格式的参数  
            StringEntity requestEntity = new StringEntity(json,"utf-8");  
            requestEntity.setContentEncoding("UTF-8");                
            httpPost.setHeader("Content-type", "application/json");  
            httpPost.setEntity(requestEntity);  
             
           //第四步：发送HttpPost请求，获取返回值  
           //returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法  
            CloseableHttpResponse httpResonse = httpClient.execute(httpPost);  
            int statusCode = httpResonse.getStatusLine().getStatusCode();  
	        if(statusCode==200){  
	        	iStream =  httpResonse.getEntity().getContent();
	        	byte[] Buffer = new byte[4096*5];  
				File file=new File("C:\\Users\\chen\\Desktop\\images\\9.png");//本地生成的文件
				@SuppressWarnings("resource")
				FileOutputStream outputStream = new FileOutputStream(file);  
	            int size=0;  
	            while((size=iStream.read(Buffer))!=-1){  
	                 System.out.println(size);  
	                 outputStream.write(Buffer,0,size);  
	            }  
	        } else{
	            System.out.println("请求发送失败，失败的返回参数为："+httpResonse.getStatusLine());  
	        }
        }catch(Exception e){  
             e.printStackTrace();  
        }finally {  
           try {  
            httpClient.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        }  
         //第五步：处理返回值  
         return iStream;  
    }  
	
	public static String HttpPostWithJson2(String url, String json) {  
		String result=null;
        CloseableHttpClient httpClient = HttpClients.createDefault();  
       // ResponseHandler<String> responseHandler = new BasicResponseHandler();  
        try{  
            //第一步：创建HttpClient对象  
        	httpClient = HttpClients.createDefault();  
              
            //第二步：创建httpPost对象  
            HttpPost httpPost = new HttpPost(url);  
              
            //第三步：给httpPost设置JSON格式的参数  
            StringEntity requestEntity = new StringEntity(json,"utf-8");  
            requestEntity.setContentEncoding("UTF-8");                
            httpPost.setHeader("Content-type", "application/json");  
            httpPost.setEntity(requestEntity);  
             
           //第四步：发送HttpPost请求，获取返回值  
           //returnValue = httpClient.execute(httpPost,responseHandler); //调接口获取返回值时，必须用此方法  
	        CloseableHttpResponse response = httpClient.execute(httpPost);
	        result = HttpUtil.getJSONStringFromResponse(response);
          
        }catch(Exception e){  
             e.printStackTrace();  
        }finally {  
           try {  
            httpClient.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        }  
         //第五步：处理返回值  
         return result;  
    }  
}
