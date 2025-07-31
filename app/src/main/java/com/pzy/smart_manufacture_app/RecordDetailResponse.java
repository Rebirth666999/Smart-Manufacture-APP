package com.pzy.smart_manufacture_app;

import com.google.gson.annotations.SerializedName;

public class RecordDetailResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("filename")
    private String filename;

    @SerializedName("original_filename")
    private String originalFilename;

    @SerializedName("description")
    private String description;

    @SerializedName("upload_time")
    private double uploadTime;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("text_url")
    private String textUrl;

    // Getters
    public boolean isSuccess() { return success; }
    public String getFilename() { return filename; }
    public String getOriginalFilename() { return originalFilename; }
    public String getDescription() { return description; }
    public double getUploadTime() { return uploadTime; }
    public String getImageUrl() { return imageUrl; }
    public String getTextUrl() { return textUrl; }
}