package com.pzy.smart_manufacture_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface OrderDemandApi {
    @GET("system/orderDemand/list")
    Call<TaskResponse<OrderDemand>> getOrderDemands(@Header("Authorization") String token);
}