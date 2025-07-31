package com.pzy.smart_manufacture_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {
    private List<AlertItem> alertItems;

    public AlertAdapter(List<AlertItem> alertItems) {
        this.alertItems = alertItems;
    }

    public void updateData(List<AlertItem> newItems) {
        this.alertItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertItem item = alertItems.get(position);
        holder.alertText.setText(item.getLabel());
        holder.detailText.setText(String.format(
            "发起人: %s (%s)\n流程: %s v%d\n创建时间: %s",
            item.getStartUserName(),
            item.getStartUserId(),
            item.getProcDefName(),
            item.getProcDefVersion(),
            item.getCreateTime()
        ));
    }

    @Override
    public int getItemCount() {
        return alertItems != null ? alertItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView alertText;
        public TextView detailText;

        public ViewHolder(View itemView) {
            super(itemView);
            alertText = itemView.findViewById(R.id.alert_text);
            detailText = itemView.findViewById(R.id.alert_detail);
        }
    }
}