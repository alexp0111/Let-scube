package com.example.lbar.fragments.mainMenuFragments.peopleFragments.friendsFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.R;
import com.example.lbar.adapter.FriendRequestsAdapter;
import com.example.lbar.adapter.FriendsAdapter;
import com.example.lbar.helpClasses.FriendRequest;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

// For interactions: other user -> this user;

public class FriendsInteractionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendRequestsAdapter requestsAdapter;

    private DatabaseReference requestsRef;
    private DatabaseReference userRef;

    private FirebaseAuth mAuth;
    private String usId;

    private ArrayList<String> requestsIDList;
    private ArrayList<User> requestsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_interactions, container, false);

        requestsIDList = new ArrayList<>();
        requestsList = new ArrayList<>();

        requestsRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        usId = mAuth.getCurrentUser().getUid();

        initItems(view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getFriendRequestsList();

        return view;
    }

    private void initItems(View v) {
        recyclerView = v.findViewById(R.id.recycler_friends_interactions);
    }

    private void getFriendRequestsList() {
        requestsRef.get().addOnCompleteListener(task -> {
            requestsIDList.clear();

            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                FriendRequest request = snapshot.getValue(FriendRequest.class);

                if (request != null && request.getToID().equals(usId))
                    requestsIDList.add(request.getFromID());
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task1) {
                if (requestsIDList.size() == 0) return;

                int i = 0;
                getSeparateUser(i);
            }

            private void getSeparateUser(final int i) {
                userRef.child(requestsIDList.get(i)).get().addOnCompleteListener(task2 -> {
                    User user = task2.getResult().getValue(User.class);

                    if (user != null) {
                        Log.d("FRIENDS_INTER_FRAGMENT", user.getUs_name());
                        requestsList.add(user);
                        Log.d("FRIENDS_INTER_FRAGMENT added", requestsList.size() + "");
                    }

                    if (i == requestsIDList.size() - 1) {
                        createAdapter();
                    } else {
                        getSeparateUser(i + 1);
                    }
                });
            }
        });
    }

    private void createAdapter() {
        requestsAdapter = new FriendRequestsAdapter(requestsList, requestsIDList, usId, getContext());
        recyclerView.setAdapter(requestsAdapter);
    }
}
