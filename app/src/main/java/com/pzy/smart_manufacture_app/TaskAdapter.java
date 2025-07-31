package com.pzy.smart_manufacture_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<TaskResponse.Task> tasks;
    private View emptyView;

    public void setEmptyView(View view) {
        this.emptyView = view;
        checkIfEmpty();
    }

    public void updateData(List<TaskResponse.Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (emptyView != null) {
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    public TaskAdapter(List<TaskResponse.Task> tasks) {
        this.tasks = tasks;
    }

    
    @Override 
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size(); // 直接返回任务数量
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskResponse.Task task = tasks.get(position);
        holder.task_code.setText(task.getCode());
        holder.task_desc.setText(task.getDesc());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView task_code;
        public TextView task_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            task_code = itemView.findViewById(R.id.task_code);
            task_desc = itemView.findViewById(R.id.task_desc);
        }
    }
}
