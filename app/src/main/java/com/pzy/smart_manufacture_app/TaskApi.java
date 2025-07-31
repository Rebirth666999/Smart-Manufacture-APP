package com.pzy.smart_manufacture_app;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TaskApi {
    @GET("/system/orderDemand/list")
    Call<TaskResponse<OrderDemand>> getOrderDemands();
    
    @GET("/system/manufacturePlan/list")
    Call<TaskResponse<ManufacturePlan>> getProductionPlans();
    
    @GET("/system/manufactureTask/list") 
    Call<TaskResponse<ManufactureTaskResponse.ManufactureTask>> getProductionTasks();
}