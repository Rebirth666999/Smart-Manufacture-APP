package com.pzy.smart_manufacture_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ManufactureTaskApi {
    @GET("system/manufactureTask/list")
    Call<TaskResponse<ManufactureTask>> getManufactureTasks(@Header("Authorization") String token);
}