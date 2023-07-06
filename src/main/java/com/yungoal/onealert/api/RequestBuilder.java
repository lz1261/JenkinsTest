package com.yungoal.onealert.api;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
public class RequestBuilder {

	private Request request = new Request();
	
	private RequestBuilder(){

	}

	static public RequestBuilder newBuilder(){
		return new RequestBuilder();
	}

	public Request build(){
		return request;
	}

	public RequestBuilder app(String app){
		request.setApp(app);
		return this;
	}

	public RequestBuilder eventType(String eventType){
		request.setEventType(eventType);
		return this;
	}

	public RequestBuilder eventId(String eventId){
		request.setEventId(eventId);
		return this;
	}
	public RequestBuilder entityName(String entityName){
		request.setEntityName(entityName);
		return this;
	}
	public RequestBuilder host(String host){
		request.setHost(host);
		return this;
	}
	public RequestBuilder service(String service){
		request.setService(service);
		return this;
	}
	public RequestBuilder alarmContent(String alarmContent){
		request.setAlarmContent(alarmContent);
		return this;
	}
	public RequestBuilder token(String token){
		request.setToken(token);
		return this;
	}

}
