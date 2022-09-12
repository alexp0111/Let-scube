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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private static String TAG = "FRIEND_REQUESTS_ADAPTER";

    private List<User> mUsers;
    private List<String> requestsIDs;
    private String userID;
    private Context mContext;

    public FriendRequestsAdapter(List<User> mUsers, List<String> requestsIDs, String userID, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.userID = userID;
        this.requestsIDs = requestsIDs;
    }

    @NonNull
    @Override
    public FriendRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_request_item, parent, false);
        return new FriendRequestsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestsAdapter.ViewHolder holder, int position) {
        User requester = mUsers.get(position);

        holder.username.setText(requester.getUs_name());
        Glide.with(mContext).load(requester.getImage()).into(holder.profile_image);
        holder.add_button.setOnClickListener(view -> {
            //DatabaseReference ref1 = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
            //        .getReference("Users")
            //        .child(requester.getUs_id())
            //        .child("us_friends");
//
            //DatabaseReference ref2 = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
            //        .getReference("Users")
            //        .child(userID)
            //        .child("us_friends");
//
            //friendIDs.remove(friend.getUs_id());
            //friendIDs.set(0, String.valueOf(friendIDs.size() - 1));
//
            //// Deleting from our user list
            //ref2.setValue(friendIDs).addOnCompleteListener(task -> {
            //    Log.d(TAG, position + " " + friendIDs.size() + " " + mUsers.size());
            //    removeRecyclerElement(friend);
            //});
//
            // TODO: adding friend + removing request
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public TextView add_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.friend_req_item_name);
            profile_image = itemView.findViewById(R.id.friend_req_item_profile_img);
            add_button = itemView.findViewById(R.id.friend_req_item_add_button);
        }
    }
}
