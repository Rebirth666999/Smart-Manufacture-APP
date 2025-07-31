package com.pzy.smart_manufacture_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private ImageView imagePreview;
    private EditText editDescription;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imagePreview = findViewById(R.id.imagePreview);
        editDescription = findViewById(R.id.editDescription);
        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnUpload = findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(v -> openGallery());
        btnUpload.setOnClickListener(v -> uploadImage());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取异常接口地址
        String exceptionBaseUrl = Routes.getExceptionBaseUrl(UploadActivity.this);
        if (exceptionBaseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置异常记录接口地址", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用 FileUtils 获取真实文件路径
        String filePath = FileUtils.getPath(this, selectedImageUri);
        if (filePath == null) {
            Toast.makeText(this, "无法获取图片路径", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);

        // 创建文件部分
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // 创建描述部分
        String description = editDescription.getText().toString();
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);

        // 发送请求
        RecordsApi api = ApiClient.getExceptionClient(this).create(RecordsApi.class);
        Call<UploadResponse> call = api.uploadImage(imagePart, descriptionPart);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // 添加详细的错误处理
                    String errorMsg = "上传失败: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += response.errorBody().string();
                        } catch (IOException e) {
                            errorMsg += e.getMessage();
                        }
                    } else {
                        errorMsg += "HTTP状态码: " + response.code();
                    }
                    Toast.makeText(UploadActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(UploadActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 添加 FileUtils 类
    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();
                return path;
            }
            return null;
        }
    }
}