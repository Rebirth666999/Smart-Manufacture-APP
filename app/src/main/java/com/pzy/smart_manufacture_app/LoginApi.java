package com.pzy.smart_manufacture_app;

import com.pzy.smart_manufacture_app.LoginRequest;
import com.pzy.smart_manufacture_app.LoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginApi {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 新增：手机端授权网页登录接口
    @POST("qrLogin/confirm")
    Call<LoginResponse> confirmLogin(
            @Header("Authorization") String authorization,
            @Body Map<String, String> params
    );
}
