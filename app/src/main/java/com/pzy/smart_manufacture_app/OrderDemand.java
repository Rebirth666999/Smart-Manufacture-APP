package com.pzy.smart_manufacture_app;

public class OrderDemand {
    private String odCode;
    private String prCode;
    private String orCode;
    private String odDemand;
    private String odPrice;
    private String odCust;

    public String getOdCode() { return odCode; }
    public String getPrCode() { return prCode; }
    public String getOrCode() { return orCode; }
    public String getOrderDesc() { return "产品: " + prCode + ", 需求数量: " + odDemand + ", 单价: " + odPrice; }
    public String getOdDemand() { return odDemand; }
    public String getOdPrice() { return odPrice; }
    public String getOdCust() { return odCust; }
}