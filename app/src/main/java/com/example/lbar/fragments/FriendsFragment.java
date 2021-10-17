package com.example.lbar.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.Adapter.UserAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.database.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;


    private DatabaseReference reference, fStatusRef;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private List<User> mUsers;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextInputEditText search_users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        AppCompatActivity main_activity = (MainActivity)getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_users);
        if (toolbar != null){
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Users");

            drawer = main_activity.findViewById(R.id.drawer_layout);
            //Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }

        reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.prog_bar_list);

        // SEARCHING FOR USERS
        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase(), fUser);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // SEARCHING FOR USERS

        recyclerView = view.findViewById(R.id.recycler_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        readUsers();

        return view;
    }

    private void searchUsers(String s, FirebaseUser fUser) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search_tool")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User us = snapshot.getValue(User.class);

                    if (us != null){
                        if (fUser == null){
                            mUsers.add(us);
                        } else {
                            if (!(us.getUs_email().equals(fUser.getEmail()))) {
                                mUsers.add(us);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if (search_users.getText().toString().equals("")) {
                    if (fUser == null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            mUsers.add(user);
                        }
                    } else {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);

                            if (!Objects.equals(snapshot.getKey(), fUser.getUid())) {
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
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
