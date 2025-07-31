package com.pzy.smart_manufacture_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ManufacturePlanApi {
    @GET("system/manufacturePlan/list") 
    Call<TaskResponse<ManufacturePlan>> getManufacturePlans(@Header("Authorization") String token);
}