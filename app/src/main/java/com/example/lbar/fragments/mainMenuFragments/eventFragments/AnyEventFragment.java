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
import com.example.lbar.helpClasses.Liker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AnyEventFragment extends Fragment implements GestureDetector.OnGestureListener {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private DatabaseReference eventReference;
    private DatabaseReference likeRef;
    private DatabaseReference mFriendsRef;

    private RecyclerView recyclerViewInEvents;
    private EventAdapter eventAdapter;
    private View dialogView;

    private List<Event> mEvents;
    private List<Liker> mLikers;
    private ArrayList<String> mFriends = new ArrayList<>();

    private DrawerLayout drawer;

    private GestureDetector gestureDetector;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_any_event, container, false);
        dialogView = inflater.inflate(R.layout.dialog_image_only, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        eventReference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Events");
        likeRef = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/").getReference("Likes");

        initItems(view);
        if (fUser != null) {
            getUserFriendsList();
        }
        SwipeMenuOpenerControl(view);

        mEvents = new ArrayList<>();
        mLikers = new ArrayList<>();

        recyclerViewInEvents.setHasFixedSize(true);
        recyclerViewInEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar.setVisibility(View.VISIBLE);
        readEvents();

        srl.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            getUserFriendsList();
            readEvents();
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void readEvents() {
        eventReference.get().addOnCompleteListener(task1 -> {
            mEvents.clear();

            for (DataSnapshot snapshot : task1.getResult().getChildren()) {
                Event event = snapshot.getValue(Event.class);
                assert event != null;

                if (event.getEv_accessibility() == 0 || event.getEv_author_id().equals(fUser.getUid()) || isFriend(event.getEv_author_id())) {
                    mEvents.add(event);
                }
            }
        }).addOnCompleteListener(task2 -> likeRef.get().addOnCompleteListener(task3 -> {
            mLikers.clear();
            for (DataSnapshot snapshot : task3.getResult().getChildren()) {
                Liker tmp_liker = snapshot.getValue(Liker.class);
                mLikers.add(tmp_liker);
            }
        }).addOnCompleteListener(task -> {
            Collections.reverse(mEvents);
            eventAdapter = new EventAdapter(getContext(), mEvents, mLikers, dialogView);
            eventAdapter.setHasStableIds(true);
            recyclerViewInEvents.setAdapter(eventAdapter);

            progressBar.setVisibility(View.GONE);
        }));
        //
        //eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
        //    @Override
        //    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //        mEvents.clear();
//
        //        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
        //            Event event = snapshot.getValue(Event.class);
        //            assert event != null;
//
        //            if (event.getEv_accessibility() == 0 || event.getEv_author_id().equals(fUser.getUid()) || isFriend(event.getEv_author_id())) {
        //                mEvents.add(event);
        //                Log.d("ARB", event.getEv_liked_users().size() + " + " + mEvents.size());
        //            }
        //        }
        //    }
//
        //    @Override
        //    public void onCancelled(@NonNull DatabaseError error) {
        //        progressBar.setVisibility(View.GONE);
        //        Log.d("List_ev", error.getMessage());
        //    }
        //});
    }

    private boolean isFriend(String id) {
        for (int i = 1; i < mFriends.size(); i++) {
            if (mFriends.get(i).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_any_events);
        srl = v.findViewById(R.id.pull_to_refresh_any_event);
        gestureDetector = new GestureDetector(getContext(), this);

        recyclerViewInEvents = v.findViewById(R.id.recycler_any_events);
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
