package com.pzy.smart_manufacture_app;

import java.util.List;

public class ExceptionMessageResponse {
    private int code;
    private String msg;
    private int total;
    private List<ExceptionMessage> rows;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getTotal() {
        return total;
    }

    public List<ExceptionMessage> getRows() {
        return rows;
    }
}