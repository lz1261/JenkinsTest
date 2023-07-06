package com.yungoal.onealert.domain;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 *
 * 创建的OneAlert 实体类
 * @author zj
 * @version 1.0
 * @date 2019-08-01 10:47
 */
public class OneAlertEntity extends TableServiceEntity {

    public OneAlertEntity() {
    }

    public OneAlertEntity(String id, String resourceId) {
        this.partitionKey = id;
        this.rowKey = resourceId;
    }
    private String status;

    private String eventId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
