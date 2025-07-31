package com.pzy.smart_manufacture_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExceptionMessageAdapter extends RecyclerView.Adapter<ExceptionMessageAdapter.ViewHolder> {
    private List<ExceptionMessage> messages;

    public ExceptionMessageAdapter(List<ExceptionMessage> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exception_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExceptionMessage message = messages.get(position);
        holder.codeView.setText(message.getExmrCode());
        holder.descView.setText(message.getExmrDesc());
        holder.timeView.setText(message.getExmrTime());
    }

    @Override
    public int getItemCount() {
        // 修复空指针异常：当messages为null时返回0
        return messages != null ? messages.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView codeView;
        public TextView descView;
        public TextView timeView;

        public ViewHolder(View view) {
            super(view);
            codeView = view.findViewById(R.id.exception_code);
            descView = view.findViewById(R.id.exception_desc);
            timeView = view.findViewById(R.id.exception_time);
        }
    }
}