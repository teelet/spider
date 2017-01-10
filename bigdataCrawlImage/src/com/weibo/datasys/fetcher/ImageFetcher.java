package com.weibo.datasys.fetcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.model.SeedData;
import com.weibo.datasys.service.ConfigService;
import com.weibo.datasys.util.FileUtils;
import com.weibo.datasys.util.StringUtils;

public class ImageFetcher {
	
	private static Logger logger = LoggerFactory.getLogger(ImageFetcher.class);
	private static final String IMG_DIR = ConfigService.getString("baseSavePath");
	private static int subDirCount = 2;
	private static int subDirLength = 10;

	
	/* 下载 url 指向的网页 */
	public static String downloadFile(SeedData seedData) {

		// 存储文件的绝对路径
		String filePath = null;
		// 存储文件的相对路径
		String relativePath = null;

		/* 1.生成 HttpClinet 对象并设置参数 */
		HttpClient httpClient = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

		/* 2.生成 GetMethod 对象并设置参数 */
		String url = seedData.getUrl();
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

		// 设置method的请求头
		getMethod.addRequestHeader("Accept", "*/*");
		getMethod.addRequestHeader("Accept-Language", "zh-cn");
		getMethod.addRequestHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

		/* 3.执行 HTTP GET 请求 */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				logger.warn("Method failed: " + getMethod.getStatusLine());
			}

			/* 4.处理 HTTP 响应内容 */
			//byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组
			InputStream inStream = getMethod.getResponseBodyAsStream();
			byte[] responseBody = input2byte(inStream);
			
			// 根据网页 url 生成保存时的文件名
			String suffix = url.substring(url.lastIndexOf("."));
			String imageId = seedData.getId();
			//relativePath = StringUtils.md5ToPath(imageId,subDirCount,subDirLength);
			filePath = IMG_DIR +File.separator+seedData.getDay() +File.separator +imageId+suffix;

			// 判断如果该图片已经被抓过，就不再抓取
			File imgFile = new File(filePath);
			if (imgFile.exists()) {
				return relativePath;
			}
			try {
				FileUtils.createFile(filePath);
			} catch (Exception e) {
				if(seedData.getState() > 0){
					seedData.setState(-1);
				}else{
					seedData.setState(seedData.getState()-1);
				}
				logger.error("文件创建失败:"+filePath, e);
			}
			FileUtils.saveToLocal(responseBody, filePath, false);
			seedData.setState(2);
			seedData.setLocalPath(filePath);
			seedData.setSize(String.valueOf(responseBody.length));
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			if(seedData.getState() > 0){
				seedData.setState(-1);
			}else{
				seedData.setState(seedData.getState()-1);
			}
			logger.error("Please check your provided http address! url: " + url, e);
		} catch (IOException e) {
			// 发生网络异常
			if(seedData.getState() > 0){
				seedData.setState(-1);
			}else{
				seedData.setState(seedData.getState()-1);
			}
			logger.error("根据url下载页面异常，url: " + url, e);
		}catch(Exception e){
			if(seedData.getState() > 0){
				seedData.setState(-1);
			}else{
				seedData.setState(seedData.getState()-1);
			}
			logger.error("fetch error: " + url, e);
		} 
		finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return relativePath;
	}
	
	private static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
		ByteArrayOutputStream swapStream = null;
		try
		{
	        swapStream = new ByteArrayOutputStream();  
	        byte[] buff = new byte[1024];  
	        int rc = 0;  
	        while ((rc = inStream.read(buff, 0, 1024)) > 0) {  
	            swapStream.write(buff, 0, rc);  
	        }  
	        byte[] in2b = swapStream.toByteArray();  
	        return in2b;  
		}finally{
			if(null != inStream){
				inStream.close();
			}
			if(null != swapStream){
				swapStream.close();
			}
		}
    }
}
