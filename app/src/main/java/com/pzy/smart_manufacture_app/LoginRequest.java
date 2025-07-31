package com.pzy.smart_manufacture_app;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 添加getter方法
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}