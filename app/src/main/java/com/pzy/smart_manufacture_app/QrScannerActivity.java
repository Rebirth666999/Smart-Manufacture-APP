package com.pzy.smart_manufacture_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pzy.smart_manufacture_app.LoginApi;
import com.pzy.smart_manufacture_app.PortraitCaptureActivity;
import com.pzy.smart_manufacture_app.Routes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class QrScannerActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // +++ 关键修改：强制设置为竖屏 +++
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 检查手机端是否已登录
        String token = Routes.getValidToken(this);
        if (token.isEmpty()) {
            Toast.makeText(this, "请先在手机端登录", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 初始化扫码（优先使用内置Zxing，无则调用外部应用）
        initScanner();
    }

    // 修改 initScanner() 方法
    private void initScanner() {
        // 强制竖屏（双重保障）
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 创建自定义扫码配置
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(PortraitCaptureActivity.class); // 关键：使用自定义竖屏Activity
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("扫描网页端登录二维码\n将二维码放入框内");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setBeepEnabled(false); // 微信扫码无提示音
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 处理内置扫码结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫码取消", Toast.LENGTH_LONG).show();
                finish();
            } else {
                verifyWebQrCode(result.getContents());
            }
            return;
        }

        // 处理外部应用扫码结果（兼容原有逻辑）
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String qrCode = data.getStringExtra("SCAN_RESULT");
            verifyWebQrCode(qrCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(this, "扫码取消", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 提取令牌的方法
    private String extractToken(String raw) {
        // 多种格式支持
        if (raw.startsWith("applogin://")) {
            return raw.substring(11);
        }
        if (raw.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {
            return raw;
        }
        // 其他格式解析逻辑
        if (raw.contains("token=")) {
            int start = raw.indexOf("token=") + 6;
            int end = raw.indexOf('&', start);
            if (end == -1) end = raw.length();
            return raw.substring(start, end);
        }
        return null;
    }

    // 修改 verifyWebQrCode 方法
    private void verifyWebQrCode(String webQrCode) {
        String processedCode = webQrCode.trim();
        String qrToken = extractToken(processedCode);

        if (qrToken == null) {
            Toast.makeText(this, "二维码无效", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String mobileToken = Routes.getValidToken(this);
        String baseUrl = Routes.getBaseUrl(this);

        performLoginConfirmation(qrToken, mobileToken, baseUrl);
    }

    // 修改 performLoginConfirmation 方法中的以下部分
    private void performLoginConfirmation(String qrToken, String mobileToken, String baseUrl) {
        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")  // 注意结尾必须是 "/"
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);

        Map<String, String> params = new HashMap<>();
        params.put("qrToken", qrToken);

        // 修复：将返回类型改为 LoginResponse
        Call<LoginResponse> call = loginApi.confirmLogin(mobileToken, params);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        int code = loginResponse.getCode();
                        String msg = loginResponse.getMsg();

                        if (code == 200) {
                            Toast.makeText(QrScannerActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = msg != null ? msg : "登录失败，未获取到具体原因";
                            Toast.makeText(QrScannerActivity.this, "登录失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = "服务器响应失败，状态码：" + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += ": " + response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(QrScannerActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(QrScannerActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

}
