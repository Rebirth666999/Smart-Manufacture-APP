package com.pzy.smart_manufacture_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetail;
    private TextView textViewOriginalFilename;
    private TextView textViewDescription;
    private TextView textViewUploadTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewOriginalFilename = findViewById(R.id.textViewOriginalFilename);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewUploadTime = findViewById(R.id.textViewUploadTime);

        String filename = getIntent().getStringExtra("filename");
        if (filename == null || filename.isEmpty()) {
            Toast.makeText(this, "无效的记录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecordDetail(filename);
    }

    private void loadRecordDetail(String filename) {
        // 获取异常接口地址
        String exceptionBaseUrl = Routes.getExceptionBaseUrl(RecordDetailActivity.this);
        if (exceptionBaseUrl.isEmpty()) {
            Toast.makeText(this, "请先设置异常记录接口地址", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecordsApi api = ApiClient.getExceptionClient(this).create(RecordsApi.class);
        Call<RecordDetailResponse> call = api.getImageDetail(filename);
        call.enqueue(new Callback<RecordDetailResponse>() {
            @Override
            public void onResponse(Call<RecordDetailResponse> call, Response<RecordDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    RecordDetailResponse record = response.body();
                    displayRecord(record);
                } else {
                    Toast.makeText(RecordDetailActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecordDetailResponse> call, Throwable t) {
                Toast.makeText(RecordDetailActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecord(RecordDetailResponse record) {
        textViewOriginalFilename.setText("原始文件名: " + record.getOriginalFilename());
        textViewDescription.setText("描述: " + record.getDescription());

        // 格式化时间戳
        double uploadTime = record.getUploadTime();
        Date date = new Date((long)(uploadTime * 1000));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        textViewUploadTime.setText("上传时间: " + sdf.format(date));

        // 加载高清图片
        Glide.with(this)
                .load(record.getImageUrl())
                .placeholder(R.drawable.ic_image_error)
                .error(R.drawable.ic_image_error)
                .into(imageViewDetail);
    }
}