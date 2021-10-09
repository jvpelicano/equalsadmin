package com.philcode.equalsadmin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.PostDetailsActivity;
import com.philcode.equalsadmin.models.Announcement;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{

    ArrayList<Announcement> homeModels;
    Activity activity;
    Context context;

    public HomeAdapter(Context context, ArrayList<Announcement> homeModels, Activity activity) {
        this.homeModels = homeModels;
        this.activity = activity;
        this.context = context;
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

        String timeStamp = homeModels.get(position).getFormattedDate();

        holder.postTitle.setText(currentItem.getPostContentTitle());
        holder.postDescription.setText(currentItem.getPostDescription());

        //Retrieve and Convert timestamp to String
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String currentDate  = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        holder.postDate.setText("Posted on " + currentDate);

        try {
            Picasso.get().load(homeModels.get(position).getPostImage())
                    .placeholder(R.drawable.equalsplaceholder).centerCrop().fit().into(holder.postImage);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.equalsplaceholder).centerCrop().fit().into(holder.postImage);
        }


        holder.postCardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("postUid", homeModels.get(position).getPostUid());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return homeModels.size();
    }
    static class HomeViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;
        public TextView postTitle, postDescription, postDate;
        public ConstraintLayout postLayout;
        public MaterialCardView postCardView;
        ConstraintLayout constraintLayout;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postDate = itemView.findViewById(R.id.post_date);
            postLayout = itemView.findViewById(R.id.post_layout);
            postCardView = itemView.findViewById(R.id.post_cardView);

        }
    }
}
