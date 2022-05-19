package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.ViewPagerEventFragmentAdapter;
import com.example.lbar.adapter.ViewPagerRoomsFragmentAdapter;
import com.example.lbar.fragments.mainMenuFragments.eventFragments.AddingEventFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RoomsMainBattleFragment extends Fragment {

    private FloatingActionButton pullNewRoom;
    private View circle;

    private Animation animationCircle;
    private boolean isUnExploid;

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    private DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_battle_rooms, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_main_battle_rooms);
        setToolbarSettings(toolbar, activity, main_activity);

        initItems(view);
        //setItemAnimations();

        viewPager2.setAdapter(new ViewPagerRoomsFragmentAdapter(getActivity()));

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.public_rooms);
                    break;
                case 1:
                    tab.setText(R.string.private_rooms);
                    break;
            }
        }).attach();

        pullNewRoom.setOnClickListener(view1 -> {
            Animation miniUnExplosionAnimation =
                    AnimationUtils.loadAnimation(getContext()
                            , R.anim.mini_circle_unexplosion);

            miniUnExplosionAnimation.setDuration(200);
            pullNewRoom.startAnimation(miniUnExplosionAnimation);

            miniUnExplosionAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    pullNewRoom.setVisibility(View.GONE);
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.from_bottom, R.anim.alpha_to_low).replace(R.id.fragment_container,
                                new AddingRoomFragment()).commit();
                    } catch (Exception D) {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        });

        return view;
    }

    private void initItems(View v) {
        tabLayout = v.findViewById(R.id.rooms_main_battle_tabLayout);
        viewPager2 = v.findViewById(R.id.rooms_main_battle_viewpager);

        try {
            isUnExploid = this.getArguments().getBoolean("circle_anim");
        } catch (NullPointerException e){
            isUnExploid = false;
        }

        pullNewRoom = v.findViewById(R.id.rooms_main_battle_pull_new);
        circle = v.findViewById(R.id.circle_in_rooms_main_battle);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            tbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            tbar.setTitle(R.string.commandSolving);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            tbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new RoomsStartFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new RoomsStartFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
