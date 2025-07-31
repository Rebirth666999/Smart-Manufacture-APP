package com.pzy.smart_manufacture_app;

import java.util.List;

public class TaskResponse<T> {
    private Integer code;
    private String msg;
    private List<T> rows;

    // Getters and Setters
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public List<T> getRows() { return rows; }
    public void setRows(List<T> rows) { this.rows = rows; }

    // 添加Task内部类
    public static class Task {
        private String code;
        private String desc;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
    }
}