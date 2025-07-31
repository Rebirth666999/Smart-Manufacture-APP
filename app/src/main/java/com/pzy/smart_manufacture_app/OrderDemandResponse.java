package com.pzy.smart_manufacture_app;

import java.util.List;

public class OrderDemandResponse {
    private int code;
    private String msg;
    private int total;
    private List<OrderDemand> rows;

    public static class OrderDemand {
        private String odId;
        private String odCode;
        private String prCode;
        private String orCode;
        private double odDemand;
        private double odPrice;
        private String odCust;
        private int odDelete;

        // Getters
        public String getOdId() { return odId; }
        public String getOdCode() { return odCode; }
        public String getPrCode() { return prCode; }
        public String getOrCode() { return orCode; }
        public double getOdDemand() { return odDemand; }
        public double getOdPrice() { return odPrice; }
        public String getOdCust() { return odCust; }
        public int getOdDelete() { return odDelete; }
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public int getTotal() { return total; }
    public List<OrderDemand> getRows() { return rows; }
}