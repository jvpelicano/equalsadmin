package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        holder.empCompany.setText(currentItem.getFullname());
        holder.empFname.setText(currentItem.getFirstname());
        holder.empLname.setText(currentItem.getLastname());
        holder.empEmail.setText(currentItem.getEmail());
        holder.empPhone.setText(currentItem.getContact());
        holder.empStatus.setText(currentItem.getTypeStatus());
        try {
            Picasso.get().load(employerModels.get(position).getAvatar())
                    .placeholder(R.drawable.emp_placeholder).centerCrop().fit().into(holder.empImage);
        }
        catch (Exception e){

//            Toast.makeText(getActiv, "Error"+ e.getMessage(), Toast.LENGTH_LONG).show();
        }

        holder.empCardView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EmployerDetailsActivity.class);
//            intent.putExtra("postContentTitle", homeModels.get(position).getPostContentTitle());
//            intent.putExtra("postDescription", homeModels.get(position).getPostDescription());
//            intent.putExtra("formattedDate", homeModels.get(position).getFormattedDate());
//            intent.putExtra("postImage", homeModels.get(position).getPostImage());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return employerModels.size();
    }

    public class EmployerViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView empImage;
        public TextView empFname, empLname,empCompany, empEmail, empPhone, empStatus;
        public MaterialCardView empCardView;

        public EmployerViewHolder(@NonNull View itemView) {
            super(itemView);

            empImage = itemView.findViewById(R.id.emp_img_profile);
            empFname = itemView.findViewById(R.id.emp_fname_item);
            empLname = itemView.findViewById(R.id.emp_lname_item);
            empCompany = itemView.findViewById(R.id.emp_company_item);
            empEmail = itemView.findViewById(R.id.emp_email_item);
            empPhone = itemView.findViewById(R.id.emp_phone_item);
            empStatus = itemView.findViewById(R.id.emp_status_item);
            empCardView = itemView.findViewById(R.id.employer_item_layout);
        }
    }

}
