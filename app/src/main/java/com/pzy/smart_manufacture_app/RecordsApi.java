package com.pzy.smart_manufacture_app;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RecordsApi {
    // 确保路径正确
    @GET("images")
    Call<RecordsResponse> getAllImages();

    @GET("image/{filename}")
    Call<RecordDetailResponse> getImageDetail(@Path("filename") String filename);

    @Multipart
    @POST("upload")
    Call<UploadResponse> uploadImage(
            @Part MultipartBody.Part image,
            @Part("description") RequestBody description
    );

    // 添加清除所有记录的接口
    @GET("clear_all")
    Call<ClearResponse> clearAllRecords();
}