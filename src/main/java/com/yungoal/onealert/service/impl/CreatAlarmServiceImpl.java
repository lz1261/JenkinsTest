package com.yungoal.onealert.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.yungoal.onealert.api.DefaultOneAlertApi;
import com.yungoal.onealert.api.OneAlertApi;
import com.yungoal.onealert.api.Request;
import com.yungoal.onealert.api.RequestBuilder;
import com.yungoal.onealert.domain.OneAlertEntity;
import com.yungoal.onealert.service.ICreatAlarmService;
import com.yungoal.onealert.util.DateUtil;
import com.yungoal.onealert.util.PathUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author zj
 * @version 1.0
 * @date 2019-07-31 11:28
 */
@Service
public class CreatAlarmServiceImpl implements ICreatAlarmService {
    /** 日志信息*/
    private final Logger logger = Logger.getLogger(CreatAlarmServiceImpl.class.getName());
    /** 获取到的应用APP key值*/
    private static String app;

    private static CloudStorageAccount storageAccount;

    /**lob表格对象*/
    private CloudTableClient tableClient;
    /**表格名称*/
    private final String tableName = "oneAlert";

    private final String status1 = "Activated";
    private final String status2 = "Resolved";

    static {
        try {
            //azure表格相关的配置信息
            Configuration config = new PropertiesConfiguration(PathUtil.PROJECT_PATH +"\\azure.properties");
            app=config.getString("appKey");
            String storageConnectionString= config.getString("storageConnectionString");
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public Boolean webHook(JSONObject json) {
        //调用oneAlertApi的方法
        OneAlertApi oneAlertApi = new DefaultOneAlertApi("http://api.onealert.com/alert/api/event/");
        oneAlertApi.init();

        Boolean table = createTable();
        if (table.equals(false)){
            logger.error("创建表格失败");
            return false;
        }
        //获取对应数据
        JSONObject data = json.getJSONObject("data");
        JSONObject context = data.getJSONObject("context");
        //获取到id，资源id，和主机名称
        String oldId = (String)context.get("id");
        String oldResourceId = (String)context.get("resourceId");
        if (oldId == null || oldResourceId==null){
            return false;
        }
        String id = oldId.replace("/",",");
        String resourceId = oldResourceId.replace("/",",");
        String status = data.getString("status");
        if (status==null){
            logger.error("没有获取到对应的数据");
            return false;
        }

        //主机名
        String host = (String)context.get("resourceName");
        //获取到condition中内容
        JSONArray jsonArray = context.getJSONObject("condition").getJSONArray("allOf");
        JSONObject allOf = jsonArray.getJSONObject(0);
        StringBuilder stringBuilder = new StringBuilder();
        if (host != null){
            stringBuilder.append(host);
        }
        //服务名称
        String service = allOf.getString("metricName");
        if (service!= null){
            stringBuilder.append(":").append(service);
        }
        String operator = allOf.getString("operator");
        if(operator != null){
            stringBuilder.append(" ").append(operator);
        }
        String threshold = allOf.getString("threshold");
        if (threshold != null){
            stringBuilder.append(" ").append(threshold);
        }
        //告警内容设置
        String alarmContent= stringBuilder.toString();

        //判断对应的状态
        OneAlertEntity oneAlertEntity = findTableView(id, resourceId);
        if (oneAlertEntity == null){
            if (insertOrUpdate(id,resourceId,status,"").equals(false)){
                logger.error("插入数据时出错");
                return false;
            }else {
                //当前时间格式化
                SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = sDateFormat.format(new Date());
                String eventId =null;
                try {
                    eventId = DateUtil.timeToTimeStamp(format);
                } catch (ParseException e) {
                    e.printStackTrace();
                    logger.error("解析时间时发生异常:"+e.getMessage());
                }
                if (eventId!=null){
                    return !updateTable(id, resourceId, status, host, oneAlertApi, eventId, alarmContent, service).equals(false);
                }
            }
        }else{
            //第二次状态为:"Activated"时不做任何处理
            if (status1.equals(status)){
                return true;
                //状态为"Resolved"，获取到存储的对应的eventId,更新表格和告警状态
            }else if (status2.equals(status)){
                OneAlertEntity tableView = findTableView(id, resourceId);
                String eventId = tableView.getEventId();

                return !updateTable(id, resourceId, status, host, oneAlertApi, eventId, alarmContent, service).equals(false);
            }
        }

        oneAlertApi.destroy();
        return true;
    }
    /**
     * 创建oneAlert工单和更新表格的方法
     * @param id 从json中获取到的存入表格的id
     * @param resourceId 从json中获取到的存入表格的resourceId
     * @param status 从json中获取到的存入表格的状态名
     * @param host 从json中获取到的用于调用API的主机名
     * @param oneAlertApi oneAlertAPI对象
     * @param eventId 创建的用于创建API的事件id
     * @param alarmContent 用于创建API时的调用的告警内容
     * @param service 用于创建API时的调用的服务名称
     */
    private Boolean updateTable(String id, String resourceId, String status, String host, OneAlertApi oneAlertApi, String eventId,String alarmContent, String service) {
        //判断对应的状态
        String eventType=null;
        if (status.equals(status1)){
            eventType ="trigger";
        }else if (status.equals(status2)){
            eventType ="resolve";
        }
        if (eventType==null) {
            logger.error("没有对应的事件类型");
            return false;
        }
        //封装请求
        Request getRequest = RequestBuilder.newBuilder().app(app).eventType(eventType).eventId(eventId)
                .host(host).entityName(host).alarmContent(alarmContent).service(service)
                .build();
        JSONObject getResponse = oneAlertApi.call(getRequest);
        //判断结果
        String result = (String) getResponse.get("result");
        String success = "success";
        //创建成功,就记录对应的eventId
        if (success.equals(result)) {
            insertOrUpdate(id, resourceId, status, eventId);
        }
        return true;
    }
    /**
     * 获取表格中是否有对应数据的方法
     * @param id 数据id
     * @param resourceId 数据资源id
     * @return 有没有对应的数据
     */
    private OneAlertEntity findTableView(String id,String resourceId) {

        tableClient = storageAccount.createCloudTableClient();
        CloudTable cloudTable = null;
        try {
            cloudTable = tableClient.getTableReference(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取连接时异常:"+e.getMessage());
        }
        //根据对应的id和resourceId获取是否有这条数据
        TableOperation retrieve = TableOperation.retrieve(id, resourceId, OneAlertEntity.class);

        OneAlertEntity oneAlertEntity =null;
        try {
            if (cloudTable != null) {
                oneAlertEntity = cloudTable.execute(retrieve).getResultAsType();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取对应表格数据时发生异常:"+e.getMessage());
        }
        return oneAlertEntity;
    }

    /**
     * 创建表格的方法
     * @return 成功还是失败
     */
    private Boolean createTable(){
        try {
            if (storageAccount!=null) {
                tableClient = storageAccount.createCloudTableClient();
                CloudTable cloudTable = tableClient.getTableReference(tableName);
                cloudTable.createIfNotExists();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建表格时发生异常:"+e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 进行表格数据插入的方法
     * @param id 对应的数据id
     * @param resourceId 资源组名称
     * @param status 状态
     * @return 成功还是失败
     */
    private Boolean insertOrUpdate(String id,String resourceId,String status,String eventId) {
        try {
            if (tableClient!=null) {
                CloudTable cloudTable = tableClient.getTableReference(tableName);
                OneAlertEntity oneAlertEntity = new OneAlertEntity(id, resourceId);
                oneAlertEntity.setStatus(status);
                oneAlertEntity.setEventId(eventId);

                TableOperation insertCustomer = TableOperation.insertOrReplace(oneAlertEntity);
                cloudTable.execute(insertCustomer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("给表格插入数据时异常:"+e.getMessage());
            return false;
        }
        return true;
    }

}
