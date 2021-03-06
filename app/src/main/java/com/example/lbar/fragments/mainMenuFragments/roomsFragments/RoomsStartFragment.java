package com.example.lbar.fragments.mainMenuFragments.roomsFragments;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments.RoomsMainBattleFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.TimeZone;

import static com.example.lbar.MainActivity.SWIPE_THRESHOLD;
import static com.example.lbar.MainActivity.SWIPE_VELOCITY_THRESHOLD;
import static com.example.lbar.MainActivity.dp_width;

public class RoomsStartFragment extends Fragment{

    private static MaterialCardView collective;
    private static MaterialCardView solo;

    private DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_rooms, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_rooms);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);
        realiseClickListenerOnCards();

        return view;
    }

    private void initItems(View v) {
        collective = v.findViewById(R.id.rooms_start_collective);
        solo = v.findViewById(R.id.rooms_start_solo);
    }

    private void realiseClickListenerOnCards() {
        collective.setOnClickListener(view -> {
            collective.setTranslationX(0);
            collective.setAlpha(1);
            collective.animate().translationX(dp_width).alpha(1).setDuration(400).setStartDelay(0).start();

            solo.setTranslationX(0);
            solo.setAlpha(1);
            solo.animate().translationX(dp_width).alpha(1).setDuration(400).setStartDelay(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.to_right, R.anim.from_right)
                                .replace(R.id.fragment_container,
                                        new RoomsMainBattleFragment()).commit();
                    } catch (Exception D) {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        });
        solo.setOnClickListener(view -> {

            collective.setTranslationX(0);
            collective.setAlpha(1);
            collective.animate().translationX(-1 * dp_width).alpha(1).setDuration(400).setStartDelay(0).start();

            solo.setTranslationX(0);
            solo.setAlpha(1);
            solo.animate().translationX(dp_width).alpha(1).setDuration(400).setStartDelay(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.from_bottom, R.anim.alpha_to_low)
                                .replace(R.id.fragment_container,
                                        new RoomsSoloFragment()).commit();
                    } catch (Exception D) {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        });

        //Long clicks
        collective.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Here you can battle with other users in cubing rooms!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        solo.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Here you can training yourself!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
    }


    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.assembly_rooms);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                drawer.openDrawer(GravityCompat.START);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
