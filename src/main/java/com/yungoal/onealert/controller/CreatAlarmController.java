package com.yungoal.onealert.controller;

import com.alibaba.fastjson.JSONObject;
import com.yungoal.onealert.service.ICreatAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author zj
 * @version 1.0
 * @date 2019-07-31 11:27
 */
@Controller
@RequestMapping("/yungoal")
public class CreatAlarmController {

    @Autowired
    private ICreatAlarmService creatAlarmService;

    @RequestMapping(value = "/webhook",method= RequestMethod.POST )
    public void  webHook(@RequestBody JSONObject json){
        creatAlarmService.webHook(json);
    }
}
