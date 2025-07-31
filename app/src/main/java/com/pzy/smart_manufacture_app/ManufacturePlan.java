package com.pzy.smart_manufacture_app;

public class ManufacturePlan {
    private String mpCode;
    private String orCode;
    private String mpStat;
    private String mpEndPlan;
    private String mpQtyPlan;

    public String getMpCode() { return mpCode; }
    public String getOrCode() { return orCode; }
    public String getMpStat() { return mpStat; }
    public String getMpEndPlan() { return mpEndPlan; }
    public String getPlanDesc() { 
        return "计划数量: " + mpQtyPlan + ", 状态: " + mpStat + ", 计划完成时间: " + mpEndPlan; 
    }
    public String getMpQtyPlan() { return mpQtyPlan; }
}