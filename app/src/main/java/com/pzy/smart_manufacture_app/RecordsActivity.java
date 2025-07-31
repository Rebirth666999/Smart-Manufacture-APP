package com.pzy.smart_manufacture_app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// 添加导入
import android.content.Intent;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// 添加必要的导入
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordsAdapter adapter;
    private List<Records> recordsList;
    private ProgressBar progressBar;
    private Button refreshButton;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    // 添加 ActionBarDrawerToggle
    private ActionBarDrawerToggle drawerToggle;

    // 添加按钮声明
    private Button btnUpload;
    private Button btnClear;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private String descriptionText = ""; // 存储用户输入的描述

    // 添加相机权限请求码
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initViews();
        setupRecyclerView();
        loadRecords();

        // 初始化按钮
        btnUpload = findViewById(R.id.btn_upload);
        btnClear = findViewById(R.id.btn_clear);

        // 修改上传按钮点击事件 - 启动拍照
        btnUpload.setOnClickListener(v -> {
            // 检查相机权限
            if (ContextCompat.checkSelfPermission(RecordsActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RecordsActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                launchCamera();
            }
        });

        // 清除按钮点击事件
        btnClear.setOnClickListener(v -> {
            clearAllRecords();
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
        // 添加toolbar和drawerLayout的初始化
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        
        // 设置ActionBar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("异常记录");
        }

        // 添加导航栏切换功能
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // 设置导航栏菜单点击事件
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(RecordsActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_tasks) {
                    startActivity(new Intent(RecordsActivity.this, TasksActivity.class));
                } else if (id == R.id.nav_alerts) {
                    startActivity(new Intent(RecordsActivity.this, AlertsActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(RecordsActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(RecordsActivity.this, SettingsActivity.class));
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(RecordsActivity.this, AboutActivity.class));
                } else if (id == R.id.nav_records) {
                    // 当前页面，无需操作
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRecords();
            }
        });
    }

    private void setupRecyclerView() {
        recordsList = new ArrayList<>();
        adapter = new RecordsAdapter(this, recordsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // 修改 loadRecords 方法
    private void loadRecords() {
        progressBar.setVisibility(View.VISIBLE);

        // 获取异常接口地址
        String exceptionBaseUrl = Routes.getExceptionBaseUrl(RecordsActivity.this);
        if (exceptionBaseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置异常记录接口地址", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // 使用异常接口的Retrofit实例
        RecordsApi api = ApiClient.getExceptionClient(RecordsActivity.this).create(RecordsApi.class);
        Call<RecordsResponse> call = api.getAllImages();

        call.enqueue(new Callback<RecordsResponse>() {
            @Override
            public void onResponse(Call<RecordsResponse> call, Response<RecordsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    recordsList.clear();
                    recordsList.addAll(response.body().getImages());
                    adapter.notifyDataSetChanged();

                    // 添加调试信息
                    Log.d("RecordsActivity", "Loaded " + recordsList.size() + " records from: " + exceptionBaseUrl);
                } else {
                    String errorMsg = "获取记录失败: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(RecordsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("RecordsActivity", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<RecordsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = "网络错误: " + t.getMessage();
                Toast.makeText(RecordsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e("RecordsActivity", errorMsg);
                t.printStackTrace();
            }
        });
    }

    // 清除所有记录的方法
    private void clearAllRecords() {
        String exceptionBaseUrl = Routes.getExceptionBaseUrl(RecordsActivity.this);
        if (exceptionBaseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置异常记录接口地址", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认清除");
        builder.setMessage("确定要清除服务器上的所有异常记录吗？此操作不可撤销！\n除非您知到您现在在做什么，否则不要进行此操作！后果自负！");
        builder.setPositiveButton("确定", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);
            clearRecordsFromServer();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void clearRecordsFromServer() {
        RecordsApi api = ApiClient.getExceptionClient(this).create(RecordsApi.class);
        Call<ClearResponse> call = api.clearAllRecords();

        call.enqueue(new Callback<ClearResponse>() {
            @Override
            public void onResponse(Call<ClearResponse> call, Response<ClearResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RecordsActivity.this, "所有记录已清除", Toast.LENGTH_SHORT).show();
                    // 刷新列表
                    loadRecords();
                } else {
                    String errorMsg = "清除失败: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(RecordsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ClearResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RecordsActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 修改 dispatchTakePictureIntent 方法
    private static final int PICK_IMAGE_REQUEST = 200;

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }



    // 新增方法：启动相机
    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.pzy.smart_manufacture_app.fileprovider",
                        photoFile);
                currentPhotoPath = photoFile.getAbsolutePath();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // 授予临时权限给相机应用
                List<ResolveInfo> cameraApps = getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo app : cameraApps) {
                    grantUriPermission(app.activityInfo.packageName, photoURI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (IOException ex) {
            Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
            Log.e("RecordsActivity", "Error creating image file", ex);
        }
    }


    // 添加权限请求结果处理方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，启动相机
                launchCamera();
            } else {
                Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 创建临时图片文件
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 获取应用专用的图片目录
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 确保目录存在
        if (storageDir != null && !storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("RecordsActivity", "Failed to create directory: " + storageDir.getAbsolutePath());
                return null;
            }
        }

        // 创建临时文件
        return File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 目录 */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理拍照结果
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // 拍照成功，显示描述对话框
                showDescriptionDialog();
            } else {
                // 拍照取消，删除临时文件
                if (currentPhotoPath != null) {
                    new File(currentPhotoPath).delete();
                    currentPhotoPath = null;
                }
            }
        }
    }


    // 显示描述输入对话框
    private void showDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加图片描述");
        builder.setMessage("请输入图片描述（可选）：");

        // 创建输入框
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("上传", (dialog, which) -> {
            // 获取用户输入的描述
            descriptionText = input.getText().toString();
            // 开始上传
            uploadImageWithDescription();
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            // 删除临时文件
            if (currentPhotoPath != null) {
                new File(currentPhotoPath).delete();
                currentPhotoPath = null;
            }
            dialog.cancel();
        });

        builder.show();
    }

    // 上传图片和描述
    // 修改后的 uploadImageWithDescription 方法
    private void uploadImageWithDescription() {
        if (currentPhotoPath == null) {
            Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        String exceptionBaseUrl = Routes.getExceptionBaseUrl(RecordsActivity.this);
        if (exceptionBaseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置异常记录接口地址", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        try {
            // 直接使用文件路径创建File对象
            File sourceFile = new File(currentPhotoPath);
            if (!sourceFile.exists()) {
                Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            String fileName = sourceFile.getName();

            Log.d("RecordsActivity", "开始上传文件: " + fileName);
            Log.d("RecordsActivity", "文件路径: " + sourceFile.getAbsolutePath());
            Log.d("RecordsActivity", "描述内容: " + descriptionText);
            Log.d("RecordsActivity", "API 地址: " + exceptionBaseUrl);

            // 创建文件请求体
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), sourceFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile);

            // 创建描述文本请求体
            RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), descriptionText);

            RecordsApi api = ApiClient.getExceptionClient(this).create(RecordsApi.class);
            Call<UploadResponse> call = api.uploadImage(imagePart, descriptionPart);

            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(RecordsActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                        loadRecords();
                    } else {
                        // 详细的错误处理
                        String errorMsg = "上传失败";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += ": " + response.errorBody().string();
                            } catch (IOException e) {
                                Log.e("RecordsActivity", "解析错误响应失败", e);
                            }
                        } else if (response.code() == 400) {
                            errorMsg = "请求格式错误";
                        } else if (response.code() == 404) {
                            errorMsg = "上传接口不存在，请检查路径";
                        } else if (response.code() == 500) {
                            errorMsg = "服务器内部错误";
                        }
                        Toast.makeText(RecordsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("RecordsActivity", "上传失败 - HTTP状态: " + response.code());
                    }
                    // 无论成功失败都删除临时文件
                    if (currentPhotoPath != null) {
                        new File(currentPhotoPath).delete();
                        currentPhotoPath = null;
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = "网络错误: " + t.getMessage();
                    Toast.makeText(RecordsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("RecordsActivity", "上传失败: " + t.getMessage(), t);
                    // 删除临时文件
                    if (currentPhotoPath != null) {
                        new File(currentPhotoPath).delete();
                        currentPhotoPath = null;
                    }
                }
            });

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            String errorMsg = "图片处理失败: " + e.getMessage();
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            Log.e("RecordsActivity", errorMsg, e);
            // 删除临时文件
            if (currentPhotoPath != null) {
                new File(currentPhotoPath).delete();
                currentPhotoPath = null;
            }
        }
    }
}