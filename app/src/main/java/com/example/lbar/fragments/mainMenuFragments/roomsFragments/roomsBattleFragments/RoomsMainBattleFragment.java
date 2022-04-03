package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

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
                    tab.setText("Public");
                    break;
                case 1:
                    tab.setText("Private");
                    break;
            }
        }).attach();

        //pullNewRoom.setOnClickListener(view1 -> {
        //    circle.setVisibility(View.VISIBLE);
        //    circle.startAnimation(animationCircle);
        //});

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
                            .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                            .replace(R.id.fragment_container,
                                    new RoomsStartFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }
}
