package com.pzy.smart_manufacture_app;

public class AlertItem {
    private String label;
    private String taskId;
    private String startUserId;
    private String startUserName;
    private String procDefName;
    private int procDefVersion;
    private String createTime;
    private String finishTime;

    public AlertItem(String label, String taskId, String startUserId, String startUserName, 
                    String procDefName, int procDefVersion, String createTime, String finishTime) {
        this.label = label;
        this.taskId = taskId;
        this.startUserId = startUserId;
        this.startUserName = startUserName;
        this.procDefName = procDefName;
        this.procDefVersion = procDefVersion;
        this.createTime = createTime;
        this.finishTime = finishTime;
    }

    public String getLabel() {
        return label;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public String getStartUserName() {
        return startUserName;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public int getProcDefVersion() {
        return procDefVersion;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }
}