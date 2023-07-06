package com.yungoal.onealert.service;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author zj
 * @version 1.0
 * @date 2019-07-31 11:28
 */
public interface ICreatAlarmService {
    /**
     * 进行数据解析的方法
     * @param json 访问后传输的json 数据
     * @return 成功还是失败
     */
    Boolean webHook(JSONObject json);
}
