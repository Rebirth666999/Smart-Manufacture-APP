package com.pzy.smart_manufacture_app;

import java.util.List;

public class ManufacturePlanResponse {
    private int code;
    private String msg;
    private int total;
    private List<ManufacturePlan> rows;

    public static class ManufacturePlan {
        private String mpId;
        private String mpCode;
        private String orCode;
        private String odCode;
        private String mpmCode;
        private String mpStat;
        private String mpBegin;
        private String mpEndPlan;
        private String mpEndReal;
        private int mpPriority;
        private double mpQtyPlan;
        private double mpQtyReal;
        private int mpDelete;
        private String mpDesc;
        private String mpCman;
        private String mpCdate;
        private String mpRman;
        private String mpRdate;
        private String mpMman;
        private String mpMdate;

        // Getters
        public String getMpId() { return mpId; }
        public String getMpCode() { return mpCode; }
        public String getOrCode() { return orCode; }
        public String getOdCode() { return odCode; }
        public String getMpmCode() { return mpmCode; }
        public String getMpStat() { return mpStat; }
        public String getMpBegin() { return mpBegin; }
        public String getMpEndPlan() { return mpEndPlan; }
        public String getMpEndReal() { return mpEndReal; }
        public int getMpPriority() { return mpPriority; }
        public double getMpQtyPlan() { return mpQtyPlan; }
        public double getMpQtyReal() { return mpQtyReal; }
        public int getMpDelete() { return mpDelete; }
        public String getMpDesc() { return mpDesc; }
        public String getMpCman() { return mpCman; }
        public String getMpCdate() { return mpCdate; }
        public String getMpRman() { return mpRman; }
        public String getMpRdate() { return mpRdate; }
        public String getMpMman() { return mpMman; }
        public String getMpMdate() { return mpMdate; }
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public int getTotal() { return total; }
    public List<ManufacturePlan> getRows() { return rows; }
}