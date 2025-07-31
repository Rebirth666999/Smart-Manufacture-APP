package com.pzy.smart_manufacture_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertCheckWorker extends Worker {
    private static final String CHANNEL_ID = "alert_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String PREFS_NAME = "AlertsPrefs";
    private static final String KEY_LAST_ALERT_COUNT = "last_alert_count";

    public AlertCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 1. 获取上次保存的报警数量
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int lastAlertCount = prefs.getInt(KEY_LAST_ALERT_COUNT, 0);

        // 2. 获取当前报警信息
        int currentAlertCount = fetchAlertsCount();

        // 3. 修改逻辑：只要有报警信息就发送通知（不再比较数量）
        if (currentAlertCount > 0) {
            sendNotification(currentAlertCount);
            Log.d("AlertWorker", "检测到报警信息，发送通知");
        }

        // 4. 保存当前报警数量
        prefs.edit().putInt(KEY_LAST_ALERT_COUNT, currentAlertCount).apply();

        return Result.success();
    }

    private int fetchAlertsCount() {
        Context context = getApplicationContext();
        String token = Routes.getValidToken(context);
        String baseUrl = Routes.getBaseUrl(context);

        if (token.isEmpty() || baseUrl.isEmpty()) {
            return 0;
        }

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", token)
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AlertApi alertApi = retrofit.create(AlertApi.class);
            Call<AlertResponse> call = alertApi.getAlerts();
            Response<AlertResponse> response = call.execute();

            Log.d("AlertWorker", "token: " + token + ", baseUrl: " + baseUrl);

            if (response.isSuccessful() && response.body() != null) {
                List<AlertResponse.AlertData> data = response.body().getData();
                Log.d("AlertWorker", "获取到报警数量: " + (data != null ? data.size() : 0));
                return data != null ? data.size() : 0;
            } else {
                Log.e("AlertWorker", "报警接口响应失败: " + response.code());
            }

        } catch (Exception e) {
            Log.e("AlertWorker", "获取报警信息失败: " + e.getMessage());
        }
        return 0;
    }

    private void sendNotification(int alertCount) {
        // 1. 确保通知渠道存在
        createNotificationChannel();

        // 2. 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("报警信息")
                .setContentText("您有 " + alertCount + " 条报警信息需要处理")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 3. 发送通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            Log.e("AlertWorker", "通知发送失败: " + e.getMessage());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "报警通知",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("接收报警信息的通知");
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}