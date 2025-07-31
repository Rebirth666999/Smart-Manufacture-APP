package com.pzy.smart_manufacture_app;

import java.util.List;

public class UserListResponse {
    private int code;
    private String msg;
    private int total;
    private List<User> rows;

    public static class User {
        private int userId;
        private String userName;
        private String nickName;
        private String loginIp;
        private String loginDate;
        private Dept dept;

        public int getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getNickName() {
            return nickName;
        }

        public String getLoginIp() {
            return loginIp;
        }

        public String getLoginDate() {
            return loginDate;
        }

        public Dept getDept() {
            return dept;
        }
    }

    public static class Dept {
        private int deptId;
        private String deptName;

        public int getDeptId() {
            return deptId;
        }

        public String getDeptName() {
            return deptName;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getTotal() {
        return total;
    }

    public List<User> getRows() {
        return rows;
    }
}