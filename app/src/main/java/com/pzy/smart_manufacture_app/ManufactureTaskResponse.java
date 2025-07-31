package com.pzy.smart_manufacture_app;

import java.util.List;

public class ManufactureTaskResponse {
    private int code;
    private String msg;
    private int total;
    private List<ManufactureTask> rows;

    public static class ManufactureTask {
        private String mtCode;
        private String mpCode;
        private String procCode;
        private String mtStat;
        private String mtQtyPlan;
        private String mtEndPlan;

        // 修改getter方法名，添加缺失的方法
        public String getMtCode() { return mtCode; }
        public String getMpCode() { return mpCode; }
        public String getProcCode() { return procCode; }
        public String getMtStat() { return mtStat; }
        public String getMtQtyPlan() { return mtQtyPlan; }
        public String getMtEndPlan() { return mtEndPlan; }
        public String getTaskDesc() {
            return "数量: " + mtQtyPlan + ", 状态: " + mtStat + ", 计划完成时间: " + mtEndPlan;
        }
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public int getTotal() { return total; }
    public List<ManufactureTask> getRows() { return rows; }
}