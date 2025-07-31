package com.pzy.smart_manufacture_app;

public class LoginResponse {
    private int code;
    private String msg;
    private Data data;

    public static class Data {
        private String token;

        public String getToken() {
            return token;
        }
    }

    public String getToken() {
        return data != null ? data.getToken() : null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}