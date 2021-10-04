package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.philcode.equalsadmin.models.Announcement;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder>{

//    ArrayList<Job> jobModels;
    Activity activity;
    Context context;


    @NonNull
    @Override
    public JobAdapter.JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.JobViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
