package com.pzy.smart_manufacture_app;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.view.View;
import androidx.preference.PreferenceManager;

public class AlertsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView alertsRecyclerView;
    private AlertAdapter alertAdapter;
    private TextView emptyView;
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "alert_channel";

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        // 创建通知渠道（Android 8.0+需要）
        createNotificationChannel();

        // 检查通知权限（Android 13+需要）
        checkNotificationPermission();
        
        // 初始化视图
        alertsRecyclerView = findViewById(R.id.alerts_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        alertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 设置Toolbar和Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("报警信息"); // 设置Toolbar标题
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(AlertsActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_tasks) {
                    startActivity(new Intent(AlertsActivity.this, TasksActivity.class));
                } else if (id == R.id.nav_alerts) {
                    // 当前就在报警信息页面
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(AlertsActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(AlertsActivity.this, SettingsActivity.class));
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(AlertsActivity.this, AboutActivity.class));
                } else if (id == R.id.nav_records) {
                    startActivity(new Intent(AlertsActivity.this, RecordsActivity.class));
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 加载报警数据
        loadAlerts();
    }

    // 检查并请求通知权限（Android 13+）
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // 请求通知权限
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    // 创建通知渠道
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "报警通知",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("接收报警信息的通知");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，重新加载报警数据
                loadAlerts();
            } else {
                Toast.makeText(this, "通知权限被拒绝，您将不会收到报警提醒", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadAlerts() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = Routes.getValidToken(this);
        
        if (token.isEmpty()) {
            Toast.makeText(this, "请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        String baseUrl = Routes.getBaseUrl(this);
        if (baseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", token)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            })
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AlertApi alertApi = retrofit.create(AlertApi.class);
        Call<AlertResponse> call = alertApi.getAlerts(); // 移除路径参数
    
        call.enqueue(new Callback<AlertResponse>() {
            @Override
            public void onResponse(Call<AlertResponse> call, Response<AlertResponse> response) {
                if (response.code() == 403) {
                    runOnUiThread(() -> {
                        emptyView.setText("您的权限不足，请联系管理员");
                        emptyView.setVisibility(View.VISIBLE);
                        alertsRecyclerView.setVisibility(View.GONE);
                    });
                    return;
                }
            
                if (response.isSuccessful() && response.body() != null) {
                    List<AlertItem> alertItems = new ArrayList<>();
                    if (response.body().getData() != null) {
                        for (AlertResponse.AlertData data : response.body().getData()) {
                            alertItems.add(new AlertItem(
                                data.getExName() + " - " + data.getTaskName(),
                                data.getTaskId(),
                                data.getStartUserId(),
                                data.getStartUserName(),
                                data.getProcDefName(),
                                data.getProcDefVersion(),
                                data.getCreateTime(),
                                data.getFinishTime()
                            ));
                        }
                    }
                
                    runOnUiThread(() -> {
                        // 修改: 移除发送通知的逻辑，只负责显示数据
                        if (!alertItems.isEmpty()) {
                            Log.d("Alerts", "获取到报警信息");
                            emptyView.setVisibility(View.GONE);
                            alertsRecyclerView.setVisibility(View.VISIBLE);
                            alertAdapter = new AlertAdapter(alertItems);
                            alertsRecyclerView.setAdapter(alertAdapter);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            alertsRecyclerView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AlertsActivity.this, "获取报警信息失败", Toast.LENGTH_SHORT).show();
                        emptyView.setVisibility(View.VISIBLE);
                        alertsRecyclerView.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onFailure(Call<AlertResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(AlertsActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    emptyView.setVisibility(View.VISIBLE);
                    alertsRecyclerView.setVisibility(View.GONE);
                });
            }
        });
    }
}