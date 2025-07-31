package com.pzy.smart_manufacture_app;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static Retrofit exceptionRetrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            String baseUrl = Routes.getBaseUrl(context);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient())
                    .build();
        }
        return retrofit;
    }


    public static Retrofit getExceptionClient(Context context) {
        if (exceptionRetrofit == null) {
            String baseUrl = Routes.getExceptionBaseUrl(context);
            exceptionRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient())
                    .build();
        }
        return exceptionRetrofit;
    }
}
