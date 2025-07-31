package com.pzy.smart_manufacture_app;

import com.google.gson.annotations.SerializedName;

public class ClearResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}