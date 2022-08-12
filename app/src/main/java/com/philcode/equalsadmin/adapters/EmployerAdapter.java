package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.EmployerDetailsActivity;
import com.philcode.equalsadmin.activities.PostDetailsActivity;
import com.philcode.equalsadmin.models.Announcement;
import com.philcode.equalsadmin.models.Employer;
import com.philcode.equalsadmin.models.Job;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerAdapter extends RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>{

    ArrayList<Employer> employerModels;
    Activity activity;
    Context context;

    public EmployerAdapter( Context context, ArrayList<Employer> employerModels, Activity activity) {
        this.context = context;
        this.employerModels = employerModels;
        this.activity = activity;
    }


    @NonNull
    @Override
    public EmployerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employer_item, parent, false);
        return new EmployerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EmployerViewHolder holder, final int position) {
        final Employer currentItem = employerModels.get(position);

        String verified = currentItem.getTypeStatus();

        if (verified.equals("EMPApproved")){
            holder.empVerified.setVisibility(View.VISIBLE);
            holder.empStatus.setText("Approved");
            holder.empStatus.setTextColor(Color.parseColor("#008000"));
        }
        else if (verified.equals("EMPPending")){
            holder.empVerified.setVisibility(View.GONE);
            holder.empStatus.setText("Pending");
            holder.empStatus.setTextColor(Color.parseColor("#FF1414"));
        }
        else{
            holder.empVerified.setVisibility(View.GONE);
            holder.empStatus.setText("Cancelled");
            holder.empStatus.setTextColor(Color.parseColor("#808080"));
        }

         String branch = currentItem.getBranch();

         if (branch.equals("")){
             holder.empLabelBranch.setVisibility(View.GONE);
             holder.empBranch.setVisibility(View.GONE);
             holder.branchLayout.setVisibility(View.GONE);
         }
         else if(branch != null){
             holder.empLabelBranch.setVisibility(View.VISIBLE);
             holder.empBranch.setVisibility(View.VISIBLE);
         }

        holder.empCompany.setText(currentItem.getFullname());
        holder.empFname.setText(currentItem.getFirstname());
        holder.empLname.setText(currentItem.getLastname());
        holder.empEmail.setText(currentItem.getEmail());
        holder.empPhone.setText(currentItem.getContact());
        holder.empBranch.setText(currentItem.getBranch());

        try {
            Picasso.get().load(employerModels.get(position).getAvatar())
                    .placeholder(R.drawable.emp_placeholder).centerCrop().fit().into(holder.empImage);
        }
        catch (Exception e){

//            Toast.makeText(getActiv, "Error"+ e.getMessage(), Toast.LENGTH_LONG).show();
        }

        holder.empCardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EmployerDetailsActivity.class);
            intent.putExtra("email", employerModels.get(position).getEmail());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return employerModels.size();
    }

    public class EmployerViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView empImage;
        public ImageView empVerified;
        public TextView empFname, empLname,empCompany, empEmail, empPhone, empStatus, empBranch, empLabelBranch;
        public MaterialCardView empCardView;
        public LinearLayout branchLayout;

        public EmployerViewHolder(@NonNull View itemView) {
            super(itemView);

            empImage = itemView.findViewById(R.id.emp_img_profile);
            empFname = itemView.findViewById(R.id.emp_fname_item);
            empLname = itemView.findViewById(R.id.emp_lname_item);
            empCompany = itemView.findViewById(R.id.emp_company_item);
            empEmail = itemView.findViewById(R.id.emp_email_item);
            empPhone = itemView.findViewById(R.id.emp_phone_item);
            empStatus = itemView.findViewById(R.id.emp_status_item);
            empVerified = itemView.findViewById(R.id.img_verified_indicator);
            empCardView = itemView.findViewById(R.id.employer_item_layout);
            empBranch = itemView.findViewById(R.id.emp_branch_item);
            branchLayout = itemView.findViewById(R.id.layout_branch);
            empLabelBranch = itemView.findViewById(R.id.tv_branch);
        }
    }

}
