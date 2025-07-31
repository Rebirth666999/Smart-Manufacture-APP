package com.pzy.smart_manufacture_app;

import java.util.List;

public class UserProfileResponse {
    private int code;
    private String msg;
    private Data data;

    public static class Data {
        private String postGroup;
        private User user;
        private String roleGroup;
        private List<Role> roles;

        public User getUser() {
            return user;
        }

        public List<Role> getRoles() {
            return roles;
        }

        public String getPostGroup() {
            return postGroup;
        }

        public String getRoleGroup() {
            return roleGroup;
        }
    }

    public static class User {
        private Long userId;
        private String userName;
        private String nickName;
        private String email;
        private String phonenumber;
        private String sex;
        private Dept dept;

        public Long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getNickName() {
            return nickName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhonenumber() {
            return phonenumber;
        }

        public String getSex() {
            return sex;
        }

        public Dept getDept() {
            return dept;
        }
    }

    public static class Dept {
        private String deptName;

        public String getDeptName() {
            return deptName;
        }
    }

    public static class Role {
        private String roleName;

        public String getRoleName() {
            return roleName;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    // 更安全的getter方法
    public Long getUserId() {
        return (data != null && data.getUser() != null) ? data.getUser().getUserId() : null;
    }

    public String getUsername() {
        return (data != null && data.getUser() != null) ? data.getUser().getUserName() : null;
    }

    public String getNickName() {
        return (data != null && data.getUser() != null) ? data.getUser().getNickName() : null;
    }

    public String getEmail() {
        return (data != null && data.getUser() != null) ? data.getUser().getEmail() : null;
    }

    public String getPhonenumber() {
        return (data != null && data.getUser() != null) ? data.getUser().getPhonenumber() : null;
    }

    public String getSex() {
        return (data != null && data.getUser() != null) ? data.getUser().getSex() : null;
    }

    public Dept getDept() {
        return (data != null && data.getUser() != null) ? data.getUser().getDept() : null;
    }

    public List<Role> getRoles() {
        return (data != null) ? data.getRoles() : null;
    }

    public String getPostGroup() {
        return (data != null) ? data.getPostGroup() : null;
    }

    public String getRoleGroup() {
        return (data != null) ? data.getRoleGroup() : null;
    }

    public boolean isSuccess() {
        return code == 200;
    }
}