package com.example.lbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.Adapter.UserAdapter;
import com.example.lbar.R;
import com.example.lbar.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;


    private DatabaseReference reference, fStatusRef;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private List<User> mUsers;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.prog_bar_list);

        recyclerView = view.findViewById(R.id.recycler_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        readUsers();

        return view;
    }

    private void readUsers() {
        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if (fUser == null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!snapshot.getKey().equals(fUser.getUid())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }

                recyclerView.setClickable(true);
                recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        Toast.makeText(getContext(), "...", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("List_fr", error.getMessage());
            }
        });
    }
}
