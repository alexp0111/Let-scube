package com.example.lbar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    private List<User> mUsers;
    private Context mContext;

    public FriendsAdapter(List<User> mUsers, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_item, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User friend = mUsers.get(position);

        holder.username.setText(friend.getUs_name());
        Glide.with(mContext).load(friend.getImage()).into(holder.profile_image);
        holder.remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: removing from friends
                Log.d("ADAPTER_FRIENDS", "tap tap");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public TextView remove_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.friend_item_name);
            profile_image = itemView.findViewById(R.id.friend_item_profile_img);
            remove_button = itemView.findViewById(R.id.friend_item_remove_button);
        }
    }
}
