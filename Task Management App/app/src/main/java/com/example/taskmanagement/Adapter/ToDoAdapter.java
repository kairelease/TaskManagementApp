package com.example.taskmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.AddNewTask;
import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.Model.ToDoModel;
import com.example.taskmanagement.R;
import com.example.taskmanagement.TaskDetailsActivity;
import com.example.taskmanagement.Utils.DatabaseHelper;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private MainActivity activity;
    private DatabaseHelper myDB;

    public ToDoAdapter(DatabaseHelper myDB, MainActivity mainActivity) {
        this.activity = mainActivity;
        this.myDB = myDB;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item = mList.get(position);
        holder.checkBox.setText(item.getTask());
        holder.checkBox.setChecked(toBoolean(item.getStatus()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    myDB.updateStatus(item.getId(), 1);
                } else {
                    myDB.updateStatus(item.getId(), 0);
                }
            }
        });

        // Add an OnClickListener for the whole task
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the new activity and pass data
                Intent intent = new Intent(activity, TaskDetailsActivity.class);
                intent.putExtra("task_id", item.getId());
                intent.putExtra("task_name", item.getTask());
                intent.putExtra("task_status", item.getStatus());
                intent.putExtra("task_description", item.getDescription()); // Pass the description
                activity.startActivity(intent);
            }
        });
    }

    public boolean toBoolean(int num) {
        return num!=0;
    }

    public Context getContext() {
        return activity;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setTask(List<ToDoModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId());

        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItems(int position) {
        ToDoModel item = mList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("ID", item.getId());
        bundle.putString("task", item.getTask());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
