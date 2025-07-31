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
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView userIdView;
    private TextView usernameView;
    private TextView nickNameView;
    private TextView emailView;
    private TextView phoneView;
    private TextView genderView;
    private TextView deptView;
    private TextView rolesView;
    private TextView postGroupView;
    private TextView roleGroupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 统一使用Routes.getValidToken检查
        String token = Routes.getValidToken(this);
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setContentView(R.layout.activity_profile);
        
        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 设置导航抽屉
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        
        // 初始化ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 
            R.string.navigation_drawer_open, 
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        
        // 导航菜单点击事件
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_tasks) {
                // 修改：直接跳转到TasksActivity
                startActivity(new Intent(this, TasksActivity.class));
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, AlertsActivity.class));
            } else if (id == R.id.nav_profile) {
                // 当前就在个人信息页面
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
        
        // Initialize views
        userIdView = findViewById(R.id.user_id);
        usernameView = findViewById(R.id.username);
        nickNameView = findViewById(R.id.nickname);
        emailView = findViewById(R.id.email);
        phoneView = findViewById(R.id.phone);
        genderView = findViewById(R.id.gender);
        deptView = findViewById(R.id.dept);
        rolesView = findViewById(R.id.roles);

        // Initialize new views
        postGroupView = findViewById(R.id.post_group);
        roleGroupView = findViewById(R.id.role_group);

        // 先显示本地缓存数据
        displayLocalProfile();
        
        // 再获取网络数据
        getUserProfile(token);
    }

    private void updateProfileDisplay(int tabPosition) {
        // 根据tab位置显示/隐藏某些字段
        if(tabPosition == 0) { // 基本信息
            emailView.setVisibility(View.GONE);
            phoneView.setVisibility(View.GONE);
        } else { // 详细信息
            emailView.setVisibility(View.VISIBLE);
            phoneView.setVisibility(View.VISIBLE);
        }
    }

    // 同步抽屉状态
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // 处理菜单项点击
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void getUserProfile(String token) {
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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApi userApi = retrofit.create(UserApi.class);
        Call<UserProfileResponse> call = userApi.getUserProfile(token); 

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.code() == 401 || response.code() == 403) {
                    handleAuthError();
                    return;
                }
                
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserProfileResponse profile = response.body();
                        if (profile.getCode() == 200 && profile.getData() != null) {
                            runOnUiThread(() -> {
                                displayUserProfile(profile);
                                saveProfileLocally(profile);
                            });
                        } else {
                            String errorMsg = "服务器返回错误: " + (profile != null ? profile.getCode() : "null") + " - " + (profile != null ? profile.getMsg() : "null");
                            handleError(errorMsg);
                        }
                    } else {
                        handleError("服务器返回数据为空");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "未知错误";
                        handleError("服务器响应错误: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        handleError("解析错误信息失败: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                handleError("网络请求失败: " + t.getMessage());
            }
        });
    }

    private void handleAuthError() {
        runOnUiThread(() -> {
            Toast.makeText(ProfileActivity.this, "认证失败，请重新登录", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
            editor.remove("token");
            editor.remove("raw_token");
            editor.apply();
            
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleError(String errorMsg) {
        runOnUiThread(() -> {
            Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            Log.e("ProfileActivity", errorMsg);
            
            // 在错误时只显示本地缓存数据
            displayLocalProfile();
        });
    }

    private void displayLocalProfile() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        // 修改为兼容Integer和Long类型的获取方式
        long userId;
        try {
            userId = prefs.getLong("user_id", 0L);
        } catch (ClassCastException e) {
            // 如果之前存储的是Integer类型，则先获取Integer再转为Long
            userId = prefs.getInt("user_id", 0);
        }
        userIdView.setText("用户ID: " + userId);
        usernameView.setText("用户名: " + prefs.getString("username", "未知"));
        nickNameView.setText("昵称: " + prefs.getString("nickname", "未设置"));
        emailView.setText("邮箱: " + prefs.getString("email", "未设置"));
        phoneView.setText("手机号: " + prefs.getString("phone", "未设置"));
        genderView.setText("性别: " + (prefs.getString("gender", "未设置").equals("1") ? "男" : "女"));
        deptView.setText("部门: " + prefs.getString("dept_name", "未设置"));
        rolesView.setText("角色: " + prefs.getString("roles", "未设置"));
        postGroupView.setText("岗位组: " + prefs.getString("post_group", "未设置"));
        roleGroupView.setText("角色组: " + prefs.getString("role_group", "未设置"));
    }

    private void saveProfileLocally(UserProfileResponse profile) {
        try {
            if (profile == null || profile.getData() == null || profile.getData().getUser() == null) {
                return;
            }
            
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            UserProfileResponse.User user = profile.getData().getUser();
            
            // 确保统一使用Long类型存储userId
            if (user.getUserId() != null) {
                editor.putLong("user_id", user.getUserId());
            } else {
                editor.putLong("user_id", 0L);
            }
            editor.putString("username", user.getUserName() != null ? user.getUserName() : "");
            editor.putString("nickname", user.getNickName() != null ? user.getNickName() : "");
            editor.putString("email", user.getEmail() != null ? user.getEmail() : "");
            editor.putString("phone", user.getPhonenumber() != null ? user.getPhonenumber() : "");
            editor.putString("gender", user.getSex() != null ? user.getSex() : "");
            
            if (user.getDept() != null) {
                editor.putString("dept_name", user.getDept().getDeptName() != null ? user.getDept().getDeptName() : "");
            }
            
            if (profile.getData().getRoles() != null) {
                StringBuilder rolesBuilder = new StringBuilder();
                for (UserProfileResponse.Role role : profile.getData().getRoles()) {
                    if (role.getRoleName() != null) {
                        rolesBuilder.append(role.getRoleName()).append(", ");
                    }
                }
                if (rolesBuilder.length() > 0) {
                    editor.putString("roles", rolesBuilder.substring(0, rolesBuilder.length() - 2));
                }
            }
            
            editor.putString("post_group", profile.getData().getPostGroup() != null ? profile.getData().getPostGroup() : "");
            editor.putString("role_group", profile.getData().getRoleGroup() != null ? profile.getData().getRoleGroup() : "");
            
            editor.apply();
        } catch (Exception e) {
            Log.e("ProfileActivity", "保存用户信息异常", e);
        }
    }

    private void displayUserProfile(UserProfileResponse profile) {
        try {
            if (profile == null || profile.getData() == null || profile.getData().getUser() == null) {
                Toast.makeText(this, "用户信息获取失败，请重试", Toast.LENGTH_SHORT).show();
                displayLocalProfile();
                return;
            }
            
            UserProfileResponse.Data data = profile.getData();
            UserProfileResponse.User user = data.getUser();
            
            userIdView.setText("用户ID: " + (user.getUserId() != null ? user.getUserId() : "未知"));
            usernameView.setText("用户名: " + (user.getUserName() != null ? user.getUserName() : "未知"));
            nickNameView.setText("昵称: " + (user.getNickName() != null ? user.getNickName() : "未设置"));
            emailView.setText("邮箱: " + (user.getEmail() != null ? user.getEmail() : "未设置"));
            phoneView.setText("手机号: " + (user.getPhonenumber() != null ? user.getPhonenumber() : "未设置"));
            
            String gender = "未设置";
            if (user.getSex() != null) {
                gender = user.getSex().equals("1") ? "男" : "女";
            }
            genderView.setText("性别: " + gender);
            
            deptView.setText("部门: " + (user.getDept() != null && user.getDept().getDeptName() != null ? user.getDept().getDeptName() : "未设置"));
            
            if (data.getRoles() != null && !data.getRoles().isEmpty()) {
                StringBuilder rolesText = new StringBuilder("角色: ");
                for (UserProfileResponse.Role role : data.getRoles()) {
                    if (role.getRoleName() != null) {
                        rolesText.append(role.getRoleName()).append(", ");
                    }
                }
                rolesView.setText(rolesText.length() > 3 ? rolesText.substring(0, rolesText.length() - 2) : "角色: 未设置");
            } else {
                rolesView.setText("角色: 未设置");
            }

            postGroupView.setText("岗位组: " + (data.getPostGroup() != null ? data.getPostGroup() : "未设置"));
            roleGroupView.setText("角色组: " + (data.getRoleGroup() != null ? data.getRoleGroup() : "未设置"));

        } catch (Exception e) {
            Log.e("ProfileActivity", "显示用户信息异常", e);
            Toast.makeText(this, "显示信息时发生错误", Toast.LENGTH_SHORT).show();
            displayLocalProfile();
        }
    }
}