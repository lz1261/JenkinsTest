package com.yungoal.onealert.api;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
public interface OneAlertApi {
	/**
	 * 初始化
	 */
	void init();
	/**
	 * 销毁
	 */
	void destroy();
	/**
	 * 请求
	 * @param request 对应请求
	 * @return 对应的值
	 */
	JSONObject call(Request request);
	/**
	 * 登陆
	 * @param user 用户名
	 * @param password 密码
	 * @return 成功还是失败
	 */
	Boolean login(String user, String password);

}
