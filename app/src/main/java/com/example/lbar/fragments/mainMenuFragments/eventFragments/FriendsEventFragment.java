package com.example.lbar.fragments.mainMenuFragments.eventFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.EventAdapter;
import com.example.lbar.helpClasses.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;
import static com.example.lbar.MainActivity.reference;

public class FriendsEventFragment extends Fragment implements GestureDetector.OnGestureListener {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private DatabaseReference eventReference;
    private DatabaseReference mFriendsRef;

    private RecyclerView recyclerViewInFriendsEvents;
    private EventAdapter eventAdapter;

    private List<Event> mEvents;
    private ArrayList<String> mFriends = new ArrayList<>();

    private DrawerLayout drawer;

    private GestureDetector gestureDetector;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_event, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        eventReference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Events");

        initItems(view);
        getUserFriendsList();
        SwipeMenuOpenerControl(view);

        mEvents = new ArrayList<>();

        recyclerViewInFriendsEvents.setHasFixedSize(true);
        recyclerViewInFriendsEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar.setVisibility(View.VISIBLE);
        readEvents();

        srl.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            getUserFriendsList();
            readEvents();
            progressBar.setVisibility(View.GONE);
            srl.setRefreshing(false);
        });

        return view;
    }

    private void getUserFriendsList() {
        mFriendsRef = reference.child(fUser.getUid()).child("us_friends");
        mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriends.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friend_id = snapshot.getValue().toString();
                    mFriends.add(friend_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void readEvents() {
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvents.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    assert event != null;

                    if (isFriend(event.getEv_author_id())){
                        mEvents.add(event);
                    }
                }

                Collections.reverse(mEvents);
                eventAdapter = new EventAdapter(getContext(), mEvents);
                recyclerViewInFriendsEvents.setAdapter(eventAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("List_ev", error.getMessage());
            }
        });
    }

    private boolean isFriend(String id) {
        for (int i = 1; i < mFriends.size(); i++) {
            if (mFriends.get(i).equals(id)){
                return true;
            }
        }
        return false;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_fr_events);
        srl = v.findViewById(R.id.pull_to_refresh_fr_event);
        gestureDetector = new GestureDetector(getContext(), this);

        recyclerViewInFriendsEvents = v.findViewById(R.id.recycler_fr_events);
    }

    private void SwipeMenuOpenerControl(View v) {
        v.setOnTouchListener((view1, motionEvent) -> {
            view1.performClick();
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean res = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

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
}

