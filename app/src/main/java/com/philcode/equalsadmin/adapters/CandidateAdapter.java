package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Candidate;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>{

    ArrayList<Candidate> candidateModels;
    Activity activity;
    Context context;



    public CandidateAdapter( Context context, ArrayList<Candidate> candidateModels, Activity activity) {
        this.context = context;
        this.candidateModels = candidateModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pwd_item, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CandidateViewHolder holder, final int position) {
            final Candidate currentItem = candidateModels.get(position);

        String verified = currentItem.getTypeStatus();

        if (verified.equals("PWDApproved")){
            holder.pwdVerified.setVisibility(View.VISIBLE);
            holder.pwdStatus.setText("Approved");
            holder.pwdStatus.setTextColor(Color.parseColor("#008000"));
        }
        else if (verified.equals("PWDPending")){
            holder.pwdVerified.setVisibility(View.GONE);
            holder.pwdStatus.setText("Pending");
            holder.pwdStatus.setTextColor(Color.parseColor("#FF1414"));
        }
        else{
            holder.pwdVerified.setVisibility(View.GONE);
            holder.pwdStatus.setText("Cancelled");
            holder.pwdStatus.setTextColor(Color.parseColor("#808080"));
        }

        holder.pwdFname.setText(currentItem.getFirstName());
        holder.pwdLname.setText(currentItem.getLastName());
        holder.pwdEmail.setText(currentItem.getEmail());
        holder.pwdPhone.setText(currentItem.getContact());

        try {
            Picasso.get().load(candidateModels.get(position).getPwdProfilePic())
                    .placeholder(R.drawable.emp_placeholder).centerCrop().fit().into(holder.pwdImage);
        }
        catch (Exception e){
        }

    }

    @Override
    public int getItemCount() {
            return candidateModels.size();
    }

    public class CandidateViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView pwdImage;
        public ImageView pwdVerified;
        public TextView pwdFname, pwdLname, pwdEmail, pwdPhone, pwdStatus;
        public MaterialCardView pwdCardView;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            pwdImage = itemView.findViewById(R.id.pwd_img_profile);
            pwdFname = itemView.findViewById(R.id.pwd_fname_item);
            pwdLname = itemView.findViewById(R.id.pwd_lname_item);
            pwdEmail = itemView.findViewById(R.id.pwd_email_item);
            pwdPhone = itemView.findViewById(R.id.pwd_phone_item);
            pwdStatus = itemView.findViewById(R.id.pwd_status_item);
            pwdVerified = itemView.findViewById(R.id.img_verified_indicator);
            pwdCardView = itemView.findViewById(R.id.pwd_item_layout);
        }
    }
}

