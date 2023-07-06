package com.yungoal.onealert.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
public class DefaultOneAlertApi implements OneAlertApi {

	private static final Logger logger = LoggerFactory.getLogger(DefaultOneAlertApi.class);

	private CloseableHttpClient httpClient;

	private URI uri;

	private volatile String token;

	public DefaultOneAlertApi(String url) {
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			logger.error("解析地址时出错:"+e.getMessage());
			throw new RuntimeException("url invalid", e);
		}
	}

	public DefaultOneAlertApi(URI uri) {
		this.uri = uri;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().build();
		}
	}

	@Override
	public void destroy() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				logger.error("close httpclient error!", e);
			}
		}
	}

	@Override
	public Boolean login(String user, String password) {
		this.token = null;
		String loginUrl=uri+"?user="+user+"&password="+password;
		HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(loginUrl)
				.addHeader("Content-Type", "application/json")
				.build();
		try {
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			byte[] entryData = EntityUtils.toByteArray(entity);
			JSONObject object = (JSONObject) JSON.parse(entryData);
			String token =(String)object.getJSONObject("data").get("token");
			if (token != null && !token.isEmpty()) {
				this.token = token;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("登录出错:"+e.getMessage());
			return false;
		}
		return false;
	}

	@Override
	public JSONObject call(Request request) {
		if (request.getToken() == null) {
			request.setToken(this.token);
		}
		try {
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(JSON.toJSONString(request), ContentType.APPLICATION_JSON)).build();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			return (JSONObject) JSON.parse(data);
		} catch (IOException e) {
			logger.error("数据解析时出错:"+e.getMessage());
			throw new RuntimeException("DefaultOneAlertApi call exception!", e);
		}
	}

}
