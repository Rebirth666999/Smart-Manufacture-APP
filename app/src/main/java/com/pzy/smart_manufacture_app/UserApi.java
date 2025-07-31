package com.pzy.smart_manufacture_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserApi {
    @GET("system/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);
}