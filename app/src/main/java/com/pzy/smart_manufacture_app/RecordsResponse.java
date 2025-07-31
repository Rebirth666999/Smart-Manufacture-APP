package com.pzy.smart_manufacture_app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecordsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("count")
    private int count;

    @SerializedName("images")
    private List<Records> images;

    // 添加 getter 方法
    public boolean isSuccess() { return success; }
    public int getCount() { return count; }
    public List<Records> getImages() { return images; }
}