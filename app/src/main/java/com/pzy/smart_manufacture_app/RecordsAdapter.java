package com.pzy.smart_manufacture_app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private Context context;
    private List<Records> recordsList;

    public RecordsAdapter(Context context, List<Records> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_records_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Records record = recordsList.get(position);

        Log.d("RecordsAdapter", "Binding record: " + record.getOriginalFilename());

        holder.textViewDescription.setText(record.getDescription() != null ? record.getDescription() : "无描述");
        holder.textViewFilename.setText(record.getOriginalFilename() != null ? record.getOriginalFilename() : "未知文件");

        // 添加图片URL调试
        Log.d("RecordsAdapter", "Image URL: " + record.getImageUrl());
        
        // 使用Glide加载图片
        Glide.with(context)
                .load(record.getImageUrl())
                .placeholder(R.drawable.ic_image_error)
                .error(R.drawable.ic_image_error)
                .into(holder.imageView);
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击查看详情
                Intent intent = new Intent(context, RecordDetailActivity.class);
                intent.putExtra("filename", record.getFilename());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewDescription;
        TextView textViewFilename;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewFilename = itemView.findViewById(R.id.textViewFilename);
        }
    }
}