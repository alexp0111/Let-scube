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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static String TAG = "FRIENDS_ADAPTER";

    private List<User> mUsers;
    private List<String> friendIDs;
    private String userID;
    private Context mContext;

    public FriendsAdapter(List<User> mUsers, List<String> friendIDs, String userID, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.userID = userID;
        this.friendIDs = friendIDs;
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
        holder.remove_button.setOnClickListener(view -> {
            DatabaseReference ref1 = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference("Users")
                    .child(friend.getUs_id())
                    .child("us_friends");

            DatabaseReference ref2 = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference("Users")
                    .child(userID)
                    .child("us_friends");

            friendIDs.remove(friend.getUs_id());
            friendIDs.set(0, String.valueOf(friendIDs.size() - 1));

            // Deleting from our user list
            ref2.setValue(friendIDs).addOnCompleteListener(task -> {
                Log.d(TAG, position + " " + friendIDs.size() + " " + mUsers.size());
                removeRecyclerElement(friend);
            });

            // TODO: Correcting ex-friends list or make new item in interractions
        });
    }

    private void removeRecyclerElement(User friend) {
        String idToDelete = friend.getUs_id();
        for (int i = 0; i < mUsers.size(); i++) {
            if (mUsers.get(i).getUs_id().equals(idToDelete)) {
                mUsers.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
