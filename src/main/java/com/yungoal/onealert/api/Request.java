package com.yungoal.onealert.api;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
public class Request {
	/** 认证令牌*/
	private String token;
	/** 集成的应用key*/
	private String app;
	/** 触发告警*/
	private String eventType;
	/** 外部事件id*/
	private String eventId;
	/** 告警对象名*/
	private String entityName;
	/** 主机*/
	private String host;
	/** 服务*/
	private String service;
	/** 告警内容*/
	private String alarmContent;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
