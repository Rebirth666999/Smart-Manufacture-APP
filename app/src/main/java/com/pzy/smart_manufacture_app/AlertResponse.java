package com.pzy.smart_manufacture_app;

import java.util.List;

public class AlertResponse {
    private int code;
    private String msg;
    private List<AlertData> data;

    public static class AlertData {
        private String taskId;
        private String taskName;
        private String exName;
        private String startUserId;
        private String startUserName;
        private String procDefName;
        private int procDefVersion;
        private String createTime;
        private String finishTime;

        public String getTaskId() {
            return taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public String getExName() {
            return exName;
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

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public List<AlertData> getData() {
        return data;
    }
}