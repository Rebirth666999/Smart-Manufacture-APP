
        package com.pzy.smart_manufacture_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText serverIpEditText;
    private Button saveIpButton;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;  // 确保已声明
    // 添加异常接口地址输入框的引用
    private EditText exceptionServerIpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serverIpEditText = findViewById(R.id.server_ip);
        saveIpButton = findViewById(R.id.save_ip_button);
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        // 初始化异常接口地址输入框
        exceptionServerIpEditText = findViewById(R.id.exception_server_ip);

        // 统一使用 PreferenceManager
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedIp = prefs.getString("server_url", "");
        if (!savedIp.isEmpty()) {
            serverIpEditText.setText(savedIp);
        }

        // 加载已保存的异常接口地址
        String savedExceptionIp = Routes.getExceptionBaseUrl(this);
        if (!savedExceptionIp.isEmpty()) {
            exceptionServerIpEditText.setText(savedExceptionIp);
        }

        // 检查是否要记住用户名密码
        boolean rememberCredentials = prefs.getBoolean("remember_credentials", false);
        Log.d("LoginActivity", "Remember credentials setting: " + rememberCredentials);
        
        if (rememberCredentials) {
            String savedUsername = prefs.getString("saved_username", "");
            String savedPassword = prefs.getString("saved_password", "");
            
            Log.d("LoginActivity", "Saved credentials - username: " + (savedUsername.isEmpty() ? "empty" : "exists") + 
                  ", password: " + (savedPassword.isEmpty() ? "empty" : "exists"));

            if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
                usernameInput.setText(savedUsername);
                passwordInput.setText(savedPassword);
                
                Log.d("LoginActivity", "Auto-filling credentials completed");

            } else {
                Log.w("LoginActivity", "Saved credentials are incomplete");
            }
        }

        // 保存主接口地址的按钮点击事件（已有代码）
        saveIpButton.setOnClickListener(v -> {
            String ipAddress = serverIpEditText.getText().toString().trim();
            if (!ipAddress.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("server_url", ipAddress);
                editor.apply();
                Toast.makeText(this, "IP地址已保存", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
            }
        });

        // 添加异常接口地址保存按钮的点击事件（新增代码）
        Button saveExceptionIpButton = findViewById(R.id.save_exception_ip_button);
        saveExceptionIpButton.setOnClickListener(v -> {
            String url = exceptionServerIpEditText.getText().toString().trim();
            if (!url.isEmpty()) {
                // 确保URL以http://或https://开头
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                // 保存到SharedPreferences
                Routes.saveExceptionBaseUrl(LoginActivity.this, url);
                Toast.makeText(LoginActivity.this, "异常接口地址保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "请输入异常接口地址", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            // 新增网络状态检查
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isConnected()) {
                Toast.makeText(this, "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查网络权限（针对Android 6.0+）
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
                return;
            }

            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String baseUrl = Routes.getBaseUrl(LoginActivity.this);

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (baseUrl.isEmpty()) {
                Toast.makeText(this, "请先设置服务器地址", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建登录请求对象
            LoginRequest loginRequest = new LoginRequest(username, password);

            // 使用Retrofit发起登录请求
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            LoginApi loginApi = retrofit.create(LoginApi.class);
            Call<LoginResponse> call = loginApi.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.code() == 403) {
                        Toast.makeText(LoginActivity.this, "权限不足，请联系管理员", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.d("LoginActivity", "完整响应: " + loginResponse.toString());

                        if (loginResponse.getToken() == null || loginResponse.getToken().isEmpty()) {
                            String errorMsg = "服务器返回令牌为空，响应码: " + loginResponse.getCode() + ", 消息: " + loginResponse.getMsg();
                            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            Log.e("LoginActivity", errorMsg);
                            return;
                        }
                        loginSuccess(loginResponse);
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "未知错误";
                            Toast.makeText(LoginActivity.this, "登录失败: " + errorBody, Toast.LENGTH_LONG).show();
                            Log.e("LoginActivity", "登录失败: " + errorBody + ", code=" + response.code());
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "登录失败: " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "解析错误响应失败", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "网络请求失败", t);
                }
            });
        });
    }

    // 添加清除登录状态的方法
    private void clearLoginState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("token");
        editor.remove("raw_token");
        editor.apply();
        Log.d("LoginActivity", "Cleared login tokens on app restart");
    }

    // 新增权限回调处理
    @Override
    public void onRequestPermissionsResult(int requestCode, 
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && 
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，可以重新尝试登录
                loginButton.performClick();
            } else {
                Toast.makeText(this, "需要网络权限才能登录", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginSuccess(LoginResponse response) {
        String token = response.getToken();

        if (token == null || token.isEmpty()) {
            Log.e("LoginActivity", "Login error: Token is empty");
            Toast.makeText(this, "登录失败: 令牌为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();

            // 保存原始token（不带Bearer前缀）
            editor.putString("raw_token", token);
            
            // 保存带Bearer前缀的token
            if (!token.startsWith("Bearer ")) {
                editor.putString("token", "Bearer " + token);
            } else {
                editor.putString("token", token);
            }
            
            // 保存用户名密码(如果记住密码已开启)
            if (prefs.getBoolean("remember_credentials", false)) {
                editor.putString("saved_username", usernameInput.getText().toString());
                editor.putString("saved_password", passwordInput.getText().toString());
            }
            
            editor.apply();  // 使用apply()确保异步保存
        
            Log.d("LoginActivity", "Token and credentials saved successfully");

            // 延迟跳转以确保token保存完成
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                scheduleAlertCheck();
                startActivity(intent);
                finish();
            }, 300);
        } catch (Exception e) {
            Log.e("LoginActivity", "Login processing exception", e);
            Toast.makeText(this, "登录处理异常", Toast.LENGTH_SHORT).show();
        }

        scheduleAlertCheck();

    }

    private void scheduleAlertCheck() {
        OneTimeWorkRequest alertCheckRequest = new OneTimeWorkRequest.Builder(AlertCheckWorker.class).build();

        WorkManager.getInstance(this).enqueue(alertCheckRequest);

        Log.d("AlertCheck", "立即执行的报警检查已调度（调试用）");
    }


}
