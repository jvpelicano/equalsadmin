package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Announcement;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{

    ArrayList<Announcement> homeModels;
    Activity activity;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid;


    public HomeAdapter(ArrayList<Announcement> homeModels, Activity activity) {
        this.homeModels = homeModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder,final int position) {
        final Announcement currentItem = homeModels.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        holder.postTitle.setText(currentItem.getPostContentTitle());
        holder.postDescription.setText(currentItem.getPostDescription());
        holder.postDate.setText(currentItem.getFormattedDate());

    }

    @Override
    public int getItemCount() {
        return homeModels.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle, postDescription, postDate;
        ConstraintLayout postLayout;


        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = (ImageView)itemView.findViewById(R.id.post_image);
            postTitle = (TextView) itemView.findViewById(R.id.post_title);
            postDescription = (TextView) itemView.findViewById(R.id.post_description);
            postDate = (TextView) itemView.findViewById(R.id.post_date);
            postLayout = (ConstraintLayout) itemView.findViewById(R.id.post_layout);

        }
    }
}
