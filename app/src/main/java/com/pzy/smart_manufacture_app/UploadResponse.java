package com.pzy.smart_manufacture_app;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("filename")
    private String filename;

    @SerializedName("text_filename")
    private String textFilename;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("text_url")
    private String textUrl;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getFilename() { return filename; }
    public String getTextFilename() { return textFilename; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getTextUrl() { return textUrl; }
}