package com.example.lbar.fragments.mainMenuFragments.peopleFragments.friendsFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.R;
import com.example.lbar.adapter.FriendsAdapter;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference friendsRef;
    private String usId;

    private ArrayList<String> friendsIDList;
    private ArrayList<User> friendsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsList = new ArrayList<>();
        friendsIDList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        usId = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference("Users");

        initItems(view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getFriendsList(usId);

        return view;
    }

    private void initItems(View v) {
        recyclerView = v.findViewById(R.id.recycler_friends);
    }

    private void getFriendsList(String usId) {
        friendsRef.child(usId).child("us_friends").get().addOnCompleteListener(task -> {
            friendsList.clear();

            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                friendsIDList.add(snapshot.getValue().toString());
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int i = 0;
                getSeparateUser(i);
            }

            private void getSeparateUser(final int i) {
                friendsRef.child(friendsIDList.get(i)).get().addOnCompleteListener(task2 -> {
                    User user = task2.getResult().getValue(User.class);

                    if (user != null) {
                        Log.d("FRIENDS_FRAGMENT", user.getUs_name());
                        friendsList.add(user);
                        Log.d("FRIENDS_FRAGMENT added", friendsList.size() + "");
                    }

                    if (i == friendsIDList.size()-1){
                        createAdapter();
                    } else {
                        getSeparateUser(i + 1);
                    }
                });
            }
        });
    }

    private void createAdapter() {
        friendsAdapter = new FriendsAdapter(friendsList, friendsIDList, usId,  getContext());
        recyclerView.setAdapter(friendsAdapter);
    }
}
