package com.pzy.smart_manufacture_app;

import com.google.gson.annotations.SerializedName;

public class Records {
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

    // Getters and Setters
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(double uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public void setTextUrl(String textUrl) {
        this.textUrl = textUrl;
    }
}