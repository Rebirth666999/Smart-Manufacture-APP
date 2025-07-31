package com.pzy.smart_manufacture_app;

import android.content.Intent;
import androidx.core.view.GravityCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView currentTimeTextView;
    private TextView usernameTextView;
    private RecyclerView taskRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (prefs.getString("token", null) == null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }, 300);
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_tasks) {
                startActivity(new Intent(this, TasksActivity.class));
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        currentTimeTextView = findViewById(R.id.current_time);
        usernameTextView = findViewById(R.id.username);
        taskRecyclerView = findViewById(R.id.task_recycler_view);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (isUserLoggedIn()) {
            loadTaskData();
        }
    }

    private void loadTaskData() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        
        // 检查token有效性
        if (token.isEmpty() || !token.startsWith("Bearer ")) {
            Toast.makeText(this, "请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String selectedTaskType = "生产任务"; // 默认值
        String baseUrl = Routes.getBaseUrl(this);
        if (baseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 根据选择的类型调用不同接口
        switch(selectedTaskType) {
            case "订单需求":
                OrderDemandApi orderApi = retrofit.create(OrderDemandApi.class);
                orderApi.getOrderDemands(token).enqueue(new Callback<TaskResponse<OrderDemand>>() {
                    @Override
                    public void onResponse(Call<TaskResponse<OrderDemand>> call, Response<TaskResponse<OrderDemand>> response) {
                        handleTaskResponse(response);
                    }
                    @Override
                    public void onFailure(Call<TaskResponse<OrderDemand>> call, Throwable t) {
                        handleTaskError(t);
                    }
                });
                break;
                
            case "生产计划":
                ManufacturePlanApi planApi = retrofit.create(ManufacturePlanApi.class);
                planApi.getManufacturePlans(token).enqueue(new Callback<TaskResponse<ManufacturePlan>>() {
                    @Override
                    public void onResponse(Call<TaskResponse<ManufacturePlan>> call, Response<TaskResponse<ManufacturePlan>> response) {
                        handleTaskResponse(response);
                    }
                    @Override
                    public void onFailure(Call<TaskResponse<ManufacturePlan>> call, Throwable t) {
                        handleTaskError(t);
                    }
                });
                break;
                
            case "生产任务":
                ManufactureTaskApi taskApi = retrofit.create(ManufactureTaskApi.class);
                taskApi.getManufactureTasks(token).enqueue(new Callback<TaskResponse<ManufactureTask>>() {
                    @Override
                    public void onResponse(Call<TaskResponse<ManufactureTask>> call, Response<TaskResponse<ManufactureTask>> response) {
                        handleTaskResponse(response);
                    }
                    @Override
                    public void onFailure(Call<TaskResponse<ManufactureTask>> call, Throwable t) {
                        handleTaskError(t);
                    }
                });
                break;
        }
    }

    private <T> void handleTaskResponse(Response<TaskResponse<T>> response) {
        if (response.code() == 403) {
            Toast.makeText(MainActivity.this, "权限不足，请联系管理员", Toast.LENGTH_LONG).show();
            taskRecyclerView.setAdapter(new TaskAdapter(new ArrayList<>()));
            return;
        }

        if (response.isSuccessful() && response.body() != null) {
            List<T> items = response.body().getRows();
            TaskAdapter adapter = new TaskAdapter(convertToTaskList(items));
            taskRecyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(MainActivity.this, "获取任务信息失败", Toast.LENGTH_SHORT).show();
            taskRecyclerView.setAdapter(new TaskAdapter(new ArrayList<>()));
        }
    }

    private void handleTaskError(Throwable t) {
        Toast.makeText(MainActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        taskRecyclerView.setAdapter(new TaskAdapter(new ArrayList<>()));
    }

    private boolean isUserLoggedIn() {
        // 检查SharedPreferences中是否有token
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("token", null) != null;
    }

    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        currentTimeTextView.setText(currentTime);
    }

    // 在导航项点击事件中使用Routes
    private NavigationView.OnNavigationItemSelectedListener navItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_tasks) {
                startActivity(new Intent(MainActivity.this, TasksActivity.class));
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(MainActivity.this, AlertsActivity.class));
            } else if (id == R.id.nav_profile) {
                // 检查token是否存在
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                if (prefs.getString("token", null) == null) {
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return false;
                }
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<TaskResponse.Task> convertToTaskList(List<?> items) {
        List<TaskResponse.Task> tasks = new ArrayList<>();
        for (Object item : items) {
            TaskResponse.Task task = new TaskResponse.Task();
            // 根据item类型设置task的code和desc
            if (item instanceof OrderDemand) {
                OrderDemand demand = (OrderDemand) item;
                task.setCode(demand.getOdCode());
                task.setDesc(demand.getOrderDesc());
            } else if (item instanceof ManufacturePlan) {
                ManufacturePlan plan = (ManufacturePlan) item;
                task.setCode(plan.getMpCode());
                task.setDesc(plan.getPlanDesc());
            } else if (item instanceof ManufactureTaskResponse.ManufactureTask) {
                ManufactureTaskResponse.ManufactureTask taskItem = (ManufactureTaskResponse.ManufactureTask) item;
                task.setCode(taskItem.getMtCode());
                task.setDesc(taskItem.getTaskDesc());
            }
            tasks.add(task);
        }
        return tasks;
    }
}