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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lbar.R;
import com.example.lbar.adapter.FriendRequestsAdapter;
import com.example.lbar.helpClasses.FriendRequest;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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

    private ArrayList<FriendRequest> requestsList;
    private ArrayList<User> requestersList;

    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_interactions, container, false);

        requestsList = new ArrayList<>();
        requestersList = new ArrayList<>();

        requestsRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        usId = mAuth.getCurrentUser().getUid();

        initItems(view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createAdapter();

        getFriendRequestsList();
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriendRequestsList();
                Snackbar.make(getView(), "Up to date", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initItems(View v) {
        recyclerView = v.findViewById(R.id.recycler_friends_interactions);
        srl = v.findViewById(R.id.pull_to_refresh_fr_interactions);
    }

    private void getFriendRequestsList() {
        requestsRef.get().addOnCompleteListener(task -> {
            requestersList.clear();
            requestsList.clear();

            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                FriendRequest request = snapshot.getValue(FriendRequest.class);

                if (request != null && request.getToID().equals(usId))
                    requestsList.add(request);
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task1) {
                if (requestsList.size() == 0) return;

                int i = 0;
                getSeparateUser(i);
            }

            private void getSeparateUser(final int i) {
                userRef.child(requestsList.get(i).getFromID()).get().addOnCompleteListener(task2 -> {
                    User user = task2.getResult().getValue(User.class);

                    if (user != null) {
                        Log.d("FRIENDS_INTER_FRAGMENT", user.getUs_name());
                        requestersList.add(user);
                        Log.d("FRIENDS_INTER_FRAGMENT added", requestersList.size() + "");
                    }

                    if (i == requestsList.size() - 1) {
                        createAdapter();
                    } else {
                        getSeparateUser(i + 1);
                    }
                });
            }
        });
    }

    private void createAdapter() {
        requestsAdapter = new FriendRequestsAdapter(requestersList, requestsList, usId, getContext());
        recyclerView.setAdapter(requestsAdapter);
        srl.setRefreshing(false);
    }
}
