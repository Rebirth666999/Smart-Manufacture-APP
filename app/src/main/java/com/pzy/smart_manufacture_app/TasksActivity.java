
        package com.pzy.smart_manufacture_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

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

import androidx.preference.PreferenceManager;

        public class TasksActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // 初始化视图
        initViews();

        // 设置TabLayout
        setupTabs();

        // 默认加载第一个Tab的数据
        loadTasks(0);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_tasks) {
                // 当前就在任务页面，不需要再做任何操作
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            // 添加异常记录菜单项的处理
            else if (id == R.id.nav_records) {
                startActivity(new Intent(this, RecordsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        tabLayout = findViewById(R.id.tab_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);
        
        View emptyView = findViewById(R.id.empty_view);
        taskAdapter.setEmptyView(emptyView);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("订单需求"));
        tabLayout.addTab(tabLayout.newTab().setText("生产计划"));
        tabLayout.addTab(tabLayout.newTab().setText("生产任务"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override 
            public void onTabSelected(TabLayout.Tab tab) {
                // 保存当前选中的Tab位置
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                prefs.edit().putInt("last_selected_tab", tab.getPosition()).apply();
                
                loadTasks(tab.getPosition());
            }
            
            @Override 
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override 
            public void onTabReselected(TabLayout.Tab tab) {
                loadTasks(tab.getPosition());
            }
        });

        // 恢复上次选中的Tab
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int lastTab = prefs.getInt("last_selected_tab", 0);
        TabLayout.Tab tab = tabLayout.getTabAt(lastTab);
        if (tab != null) {
            tab.select();
        }
    }

    private void loadTasks(int tabPosition) {
        taskAdapter.updateData(new ArrayList<>());
        findViewById(R.id.empty_view).setVisibility(View.GONE);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = Routes.getValidToken(this);
        
        // 增加Token有效性检查
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

        TaskApi taskApi = retrofit.create(TaskApi.class);

        switch (tabPosition) {
            case 0: // 订单需求
                loadOrderDemands(taskApi, token);
                break;
            case 1: // 生产计划
                loadProductionPlans(taskApi, token);
                break;
            case 2: // 生产任务
                loadProductionTasks(taskApi, token);
                break;
        }
    }

    private void loadOrderDemands(TaskApi taskApi, String token) {
        taskApi.getOrderDemands().enqueue(new Callback<TaskResponse<OrderDemand>>() { // 移除路径参数
            @Override
            public void onResponse(Call<TaskResponse<OrderDemand>> call, Response<TaskResponse<OrderDemand>> response) {
                if (response.code() == 403) {
                    runOnUiThread(() -> {
                        TextView emptyViewText = findViewById(R.id.empty_view);
                        emptyViewText.setText("您的权限不足，请联系管理员");
                        emptyViewText.setVisibility(View.VISIBLE);
                        taskAdapter.updateData(new ArrayList<>());
                        recyclerView.setVisibility(View.GONE);
                    });
                    return;
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDemand> orders = response.body().getRows();
                    List<TaskResponse.Task> tasks = new ArrayList<>();
                    if (orders != null) {
                        for (OrderDemand order : orders) {
                            TaskResponse.Task task = new TaskResponse.Task();
                            task.setCode(order.getOdCode());
                            task.setDesc("产品: " + order.getPrCode() + ", 数量: " + order.getOdDemand());
                            tasks.add(task);
                        }
                    }
                    runOnUiThread(() -> {
                        taskAdapter.updateData(tasks);
                        if (tasks.isEmpty()) {
                            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.empty_view).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handleErrorResponse("订单需求", response);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse<OrderDemand>> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void loadProductionPlans(TaskApi taskApi, String token) {
        taskApi.getProductionPlans().enqueue(new Callback<TaskResponse<ManufacturePlan>>() { // 移除路径参数
            @Override
            public void onResponse(Call<TaskResponse<ManufacturePlan>> call, Response<TaskResponse<ManufacturePlan>> response) {
                if (response.code() == 403) {
                    runOnUiThread(() -> {
                        TextView emptyViewText = findViewById(R.id.empty_view);
                        emptyViewText.setText("您的权限不足，请联系管理员");
                        emptyViewText.setVisibility(View.VISIBLE);
                        taskAdapter.updateData(new ArrayList<>());
                        recyclerView.setVisibility(View.GONE);
                    });
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<ManufacturePlan> plans = response.body().getRows();
                    List<TaskResponse.Task> tasks = new ArrayList<>();
                    if (plans != null) {
                        for (ManufacturePlan plan : plans) {
                            TaskResponse.Task task = new TaskResponse.Task();
                            task.setCode(plan.getMpCode());
                            task.setDesc("订单: " + plan.getOrCode() + ", 数量: " + plan.getMpQtyPlan() + ", 状态: " + getPlanStatusText(plan.getMpStat()));
                            tasks.add(task);
                        }
                    }
                    runOnUiThread(() -> {
                        taskAdapter.updateData(tasks);
                        if (tasks.isEmpty()) {
                            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.empty_view).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handleErrorResponse("生产计划", response);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse<ManufacturePlan>> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void loadProductionTasks(TaskApi taskApi, String token) {
        taskApi.getProductionTasks().enqueue(new Callback<TaskResponse<ManufactureTaskResponse.ManufactureTask>>() { // 移除路径参数
            @Override
            public void onResponse(Call<TaskResponse<ManufactureTaskResponse.ManufactureTask>> call,
                                   Response<TaskResponse<ManufactureTaskResponse.ManufactureTask>> response) {
                if (response.code() == 403) {
                    runOnUiThread(() -> {
                        TextView emptyViewText = findViewById(R.id.empty_view);
                        emptyViewText.setText("您的权限不足，请联系管理员");
                        emptyViewText.setVisibility(View.VISIBLE);
                        taskAdapter.updateData(new ArrayList<>());
                        recyclerView.setVisibility(View.GONE);
                    });
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<ManufactureTaskResponse.ManufactureTask> taskItems = response.body().getRows();
                    List<TaskResponse.Task> tasks = new ArrayList<>();
                    if (taskItems != null) {
                        for (ManufactureTaskResponse.ManufactureTask taskItem : taskItems) {
                            TaskResponse.Task task = new TaskResponse.Task();
                            task.setCode(taskItem.getMtCode());
                            task.setDesc("工序: " + taskItem.getProcCode() + ", 数量: " + taskItem.getMtQtyPlan() + ", 状态: " + getTaskStatusText(taskItem.getMtStat()));
                            tasks.add(task);
                        }
                    }
                    runOnUiThread(() -> {
                        taskAdapter.updateData(tasks);
                        if (tasks.isEmpty()) {
                            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.empty_view).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handleErrorResponse("生产任务", response);
                }
            }

            @Override
            public void onFailure(Call<TaskResponse<ManufactureTaskResponse.ManufactureTask>> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void showError(String type, Response<?> response) {
        String errorMessage = "未知错误";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (Exception e) {
                errorMessage = "解析错误信息失败: " + e.getMessage();
            }
        } else if (response.message() != null) {
            errorMessage = response.message();
        }
        Toast.makeText(this, "获取" + type + "失败: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showNetworkError(Throwable t) {
        Toast.makeText(this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("TasksActivity", "网络请求失败", t);
    }

    private String getPlanStatusText(String status) {
        switch(status) {
            case "1": return "新建";
            case "4": return "执行中";
            case "5": return "已完成";
            case "6": return "已取消";
            default: return "未知";
        }
    }

    private String getTaskStatusText(String status) {
        switch(status) {
            case "1": return "新建";
            case "4": return "执行中";
            case "5": return "已完成";
            case "6": return "已取消";
            default: return "未知";
        }
    }

    private List<TaskResponse.Task> convertToTaskList(List<OrderDemand> orders) {
        List<TaskResponse.Task> tasks = new ArrayList<>();
        // 添加null检查
        if (orders == null) {
            return tasks;
        }
        for (OrderDemand order : orders) {
            TaskResponse.Task task = new TaskResponse.Task();
            task.setCode(order.getOdCode());
            task.setDesc("产品: " + order.getPrCode() + ", 数量: " + order.getOdDemand());
            tasks.add(task);
        }
        return tasks;
    }

    private void handleErrorResponse(String type, Response<?> response) {
        String errorMsg = "获取" + type + "失败: ";
        if (response.code() == 403) {
            errorMsg += "权限不足";
        } else {
            errorMsg += response.message();
        }
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        taskAdapter.updateData(new ArrayList<>());
    }

    private void handleNetworkError(Throwable t) {
        Toast.makeText(this, "网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
        taskAdapter.updateData(new ArrayList<>());
    }
}
