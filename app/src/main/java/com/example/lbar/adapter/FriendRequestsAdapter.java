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
import com.example.lbar.helpClasses.FriendRequest;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private static final String TAG = "FRIEND_REQUESTS_ADAPTER";

    private List<User> mUsers;
    private List<FriendRequest> requestsList;
    private String userID;
    private Context mContext;

    public FriendRequestsAdapter(List<User> mUsers, List<FriendRequest> requestsList, String userID, Context mContext) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.userID = userID;
        this.requestsList = requestsList;
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
            ArrayList<String> usFriendsList = new ArrayList<>();
            ArrayList<String> reqFriendsList = new ArrayList<>();


            DatabaseReference refReqFriends = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference("Users")
                    .child(requester.getUs_id())
                    .child("us_friends");

            DatabaseReference refUsFriends = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference("Users")
                    .child(userID)
                    .child("us_friends");

            DatabaseReference requestRef = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference("FriendRequests");

            // getting this user friends
            refUsFriends.get().addOnCompleteListener(task -> {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    usFriendsList.add(snapshot.getValue().toString());
                }

                Log.d(TAG, "Us_friends_list_get; Size: " + usFriendsList.size());
            }).addOnCompleteListener(task2 -> refReqFriends.get().addOnCompleteListener(task3 -> {
                // getting requester friends
                for (DataSnapshot snapshot : task3.getResult().getChildren()) {
                    reqFriendsList.add(snapshot.getValue().toString());
                }

                Log.d(TAG, "Requester_friends_list_get; Size: " + usFriendsList.size());
            }).addOnCompleteListener(task4 -> {
                usFriendsList.add(requester.getUs_id());
                reqFriendsList.add(userID);

                usFriendsList.set(0, String.valueOf(usFriendsList.size()-1));
                reqFriendsList.set(0, String.valueOf(reqFriendsList.size()-1));

                Log.d(TAG, "Friends_interactions_in_lists: DONE");
                Log.d(TAG, "US_friends_list_get; Size: " + usFriendsList.size());
                Log.d(TAG, "Requester_friends_list_get; Size: " + reqFriendsList.size());
                refUsFriends.setValue(usFriendsList).addOnCompleteListener(task5 -> refReqFriends.setValue(reqFriendsList).addOnCompleteListener(task6 -> {
                    requestRef.child(getRequest(requester)).removeValue().addOnCompleteListener(task7 -> {
                        removeRecyclerElement(requester);
                        Log.d(TAG, "item removed");
                    });
                }));
            }));
        });
    }

    private String getRequest(User requester) {
        for (int i = 0; i < requestsList.size(); i++) {
            if (requestsList.get(i).getToID().equals(userID) && requestsList.get(i).getFromID().equals(requester.getUs_id()))
                return requestsList.get(i).getRequestID();
        }
        return "";
    }

    private void removeRecyclerElement(User requester) {
        String idToDelete = requester.getUs_id();
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
        public TextView add_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.friend_req_item_name);
            profile_image = itemView.findViewById(R.id.friend_req_item_profile_img);
            add_button = itemView.findViewById(R.id.friend_req_item_add_button);
        }
    }
}
