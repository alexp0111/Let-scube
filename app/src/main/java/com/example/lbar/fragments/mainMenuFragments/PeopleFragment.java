package com.example.lbar.fragments.mainMenuFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lbar.adapter.UserAdapter;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.helpClasses.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;
import static com.example.lbar.MainActivity.reference;

public class PeopleFragment extends Fragment implements GestureDetector.OnGestureListener {

    private RecyclerView recyclerViewInPeople;
    private UserAdapter userAdapter;

    private FirebaseUser fUser;
    private FirebaseAuth mAuth;

    private List<User> mUsers;

    private DrawerLayout drawer;
    private TextInputEditText searchUsers;
    private ImageView imgSearchUsers;
    private GestureDetector gestureDetector;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_users);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);

        SwipeMenuOpenerControl(recyclerViewInPeople);

        mUsers = new ArrayList<>();

        recyclerViewInPeople.setHasFixedSize(true);
        recyclerViewInPeople.setLayoutManager(new LinearLayoutManager(getContext()));

        searchUsers.addTextChangedListener(new TextWatcher() {
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

        imgSearchUsers.setOnClickListener(view1 -> {
            TextInputLayout textInputLayout = view.findViewById(R.id.textField_search_users);
            if (textInputLayout.getVisibility() == View.GONE){
                textInputLayout.setVisibility(View.VISIBLE);
            } else {
                textInputLayout.setVisibility(View.GONE);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        readUsers();

        srl.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            readUsers();
            progressBar.setVisibility(View.GONE);
            srl.setRefreshing(false);
        });

        return view;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_list);
        srl = v.findViewById(R.id.pull_to_refresh_users);
        gestureDetector = new GestureDetector(getContext(), this);

        searchUsers = v.findViewById(R.id.search_users);
        imgSearchUsers = v.findViewById(R.id.img_search_users);

        recyclerViewInPeople = v.findViewById(R.id.recycler_users);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.title_people);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
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

                    if (us != null) {
                        if (fUser == null) {
                            mUsers.add(us);
                        } else {
                            if (!(us.getUs_email().equals(fUser.getEmail()))) {
                                mUsers.add(us);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerViewInPeople.setAdapter(userAdapter);
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
                if (searchUsers.getText().toString().equals("")) {
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
                    recyclerViewInPeople.setAdapter(userAdapter);
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

    private void SwipeMenuOpenerControl(View v) {
        v.setOnTouchListener((view1, motionEvent) -> {
            view1.performClick();
            gestureDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean res = false;
        float diffY = 0;
        float diffX = 0;
        try {
            diffY = moveEvent.getY() - downEvent.getY();
            diffX = moveEvent.getX() - downEvent.getX();
        } catch (Exception e){
            //Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                }
                res = true;
            }
        }
        return res;
    }

    private void onSwipeRight() {
        drawer.openDrawer(GravityCompat.START);
    }
}
