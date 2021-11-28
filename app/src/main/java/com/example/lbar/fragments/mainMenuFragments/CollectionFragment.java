package com.example.lbar.fragments.mainMenuFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.google.android.material.card.MaterialCardView;


import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;

public class CollectionFragment extends Fragment implements GestureDetector.OnGestureListener {

    private DrawerLayout drawer;
    private ScrollView sv;

    private static MaterialCardView cubeType0;
    private static MaterialCardView cubeType1;
    private static MaterialCardView cubeType2;
    private static MaterialCardView cubeType3;
    private static MaterialCardView cubeType4;
    private static MaterialCardView cubeType5;
    private static MaterialCardView cubePyraminx;
    private static MaterialCardView cubeSqube;
    private static MaterialCardView cubeClock;
    private static MaterialCardView cubeMegaminx;
    private static MaterialCardView cubeSquare1;



    private GestureDetector gestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_collection);
        setToolbarSettings(toolbar, activity, main_activity);
        
        initItems(view);

        realiseClickListenerOnCards();

        gestureDetector = new GestureDetector(getContext(), this);

        SwipeMenuOpenerControl(sv);

        return view;
    }

    private void initItems(View v) {
        sv = v.findViewById(R.id.scrollView_in_collection);

        cubeType0 = v.findViewById(R.id.collection_card_type0);
        cubeType1 = v.findViewById(R.id.collection_card_type1);
        cubeType2 = v.findViewById(R.id.collection_card_type2);
        cubeType3 = v.findViewById(R.id.collection_card_type3);
        cubeType4 = v.findViewById(R.id.collection_card_type4);
        cubeType5 = v.findViewById(R.id.collection_card_type5);

        cubePyraminx = v.findViewById(R.id.collection_card_pyraminx);
        cubeSqube = v.findViewById(R.id.collection_card_sqube);
        cubeClock = v.findViewById(R.id.collection_card_clock);
        cubeMegaminx = v.findViewById(R.id.collection_card_megaminx);
        cubeSquare1 = v.findViewById(R.id.collection_card_square1);
    }

    private void realiseClickListenerOnCards() {
        cubeType0.setOnClickListener(view -> Toast.makeText(getContext(), "2 x 2", Toast.LENGTH_SHORT).show());
        cubeType1.setOnClickListener(view -> Toast.makeText(getContext(), "3 x 3", Toast.LENGTH_SHORT).show());
        cubeType2.setOnClickListener(view -> Toast.makeText(getContext(), "4 x 4", Toast.LENGTH_SHORT).show());
        cubeType3.setOnClickListener(view -> Toast.makeText(getContext(), "5 x 5", Toast.LENGTH_SHORT).show());
        cubeType4.setOnClickListener(view -> Toast.makeText(getContext(), "6 x 6", Toast.LENGTH_SHORT).show());
        cubeType5.setOnClickListener(view -> Toast.makeText(getContext(), "7 x 7", Toast.LENGTH_SHORT).show());
        cubeClock.setOnClickListener(view -> Toast.makeText(getContext(), "Clocks", Toast.LENGTH_SHORT).show());
        cubePyraminx.setOnClickListener(view -> Toast.makeText(getContext(), "Pyraminx", Toast.LENGTH_SHORT).show());
        cubeMegaminx.setOnClickListener(view -> Toast.makeText(getContext(), "Megaminx", Toast.LENGTH_SHORT).show());
        cubeSqube.setOnClickListener(view -> Toast.makeText(getContext(), "Sqube", Toast.LENGTH_SHORT).show());
        cubeSquare1.setOnClickListener(view -> Toast.makeText(getContext(), "Square-1", Toast.LENGTH_SHORT).show());
    }

    private void SwipeMenuOpenerControl(View v) {
        v.setOnTouchListener((view1, motionEvent) -> {
            view1.performClick();
            gestureDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle("Collection");

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
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
            Log.d("Collection", e.toString());
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
