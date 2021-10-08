package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.EmployerDetailsActivity;
import com.philcode.equalsadmin.activities.JobDetailsActivity;
import com.philcode.equalsadmin.models.Announcement;
import com.philcode.equalsadmin.models.Employer;
import com.philcode.equalsadmin.models.Job;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder>{

    ArrayList<Job> jobModels;
    Activity activity;
    Context context;

    public JobAdapter(Context context, ArrayList<Job> jobModels, Activity activity) {
        this.jobModels = jobModels;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.JobViewHolder holder, int position) {
        final Job currentItem = jobModels.get(position);

        String offerVerified = currentItem.getPermission();

        if (offerVerified.equals("Approved")){
            holder.jobVerified.setVisibility(View.VISIBLE);
            holder.jobPermission.setText("Approved");
            holder.jobPermission.setTextColor(Color.parseColor("#008000"));
        }
        else if (offerVerified.equals("pending")){
            holder.jobVerified.setVisibility(View.GONE);
            holder.jobPermission.setText("Pending");
            holder.jobPermission.setTextColor(Color.parseColor("#FF1414"));
        }
        else{
            holder.jobVerified.setVisibility(View.GONE);
            holder.jobPermission.setText("Cancelled");
            holder.jobPermission.setTextColor(Color.parseColor("#808080"));
        }

        holder.jobCompany.setText(currentItem.getCompanyName());
        holder.jobTitle.setText(currentItem.getPostTitle());
        holder.jobPosted.setText(currentItem.getPostDate());
        holder.jobPermission.setText(currentItem.getPermission());
        try {
            Picasso.get().load(jobModels.get(position).getImageURL())
                    .placeholder(R.drawable.equalsplaceholder).centerCrop().fit().into(holder.jobImg);
        }
        catch (Exception e){


        }

        holder.jobCardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, JobDetailsActivity.class);
//            intent.putExtra("postTitle", jobModels.get(position).getPostTitle());
//            intent.putExtra("companyName", jobModels.get(position).getCompanyName());
//            intent.putExtra("postDate", jobModels.get(position).getPostDate());
//            intent.putExtra("expDate", jobModels.get(position).getExpDate());
//            intent.putExtra("imageURL", jobModels.get(position).getImageURL());
            intent.putExtra("uid", jobModels.get(position).getUid());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {

        public ImageView jobVerified, jobImg;
        public TextView jobTitle, jobCompany, jobPosted, jobPermission;
        public MaterialCardView jobCardView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.job_title_item);
            jobCompany = itemView.findViewById(R.id.job_company_item);
            jobPosted = itemView.findViewById(R.id.job_date_item);
            jobPermission = itemView.findViewById(R.id.job_status_item);
            jobVerified = itemView.findViewById(R.id.job_verified_indicator);
            jobImg = itemView.findViewById(R.id.job_img_item);
            jobCardView = itemView.findViewById(R.id.job_item_layout);
        }
    }
}
