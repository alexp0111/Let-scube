package com.example.lbar.fragments.mainMenuFragments.EventFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

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

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.EventAdapter;
import com.example.lbar.fragments.AddingEventFragment;
import com.example.lbar.helpClasses.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class AnyEventFragment extends Fragment implements GestureDetector.OnGestureListener {

    private RecyclerView recyclerViewInEvents;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private FloatingActionButton pullNewEvent;

    private EventAdapter eventAdapter;

    private DatabaseReference eventReference;

    private List<Event> mEvents;

    private DrawerLayout drawer;
    private Animation animationCircle;
    private View circle;
    private boolean isUnExploid;

    private GestureDetector gestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_any_event, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        eventReference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Events");

        initItems(view);
        setItemAnimations();
        SwipeMenuOpenerControl(view);

        mEvents = new ArrayList<>();

        recyclerViewInEvents.setHasFixedSize(true);
        recyclerViewInEvents.setLayoutManager(new LinearLayoutManager(getContext()));


        pullNewEvent.setOnClickListener(view1 -> {
            circle.setVisibility(View.VISIBLE);
            circle.startAnimation(animationCircle);
        });

        progressBar.setVisibility(View.VISIBLE);
        readEvents();

        return view;
    }

    private void setItemAnimations() {
        if (isUnExploid){
            Animation animationUnCircle = AnimationUtils.loadAnimation(getContext(), R.anim.circle_unexplosion);

            animationUnCircle.setDuration(700);
            circle.startAnimation(animationUnCircle);
        }

        animationCircle = AnimationUtils.loadAnimation(getContext(), R.anim.circle_explosion);
        animationCircle.setDuration(700);

        animationCircle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AddingEventFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void readEvents() {
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvents.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    mEvents.add(event);
                }

                Collections.reverse(mEvents);
                eventAdapter = new EventAdapter(getContext(), mEvents);
                recyclerViewInEvents.setAdapter(eventAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("List_ev", error.getMessage());
            }
        });
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_events);
        gestureDetector = new GestureDetector(getContext(), this);

        try {
            isUnExploid = this.getArguments().getBoolean("circle_anim");
        } catch (NullPointerException e){
            isUnExploid = false;
        }
        recyclerViewInEvents = v.findViewById(R.id.recycler_events);
        pullNewEvent = v.findViewById(R.id.event_pull_new);
        circle = v.findViewById(R.id.circle);
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
