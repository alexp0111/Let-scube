package com.example.lbar.fragments.mainMenuFragments.eventFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.ViewPagerEventFragmentAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainEventFragment extends Fragment {

    private FloatingActionButton pullNewEvent;
    private View circle;

    private Animation animationCircle;
    private boolean isUnExploid;

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    private DrawerLayout drawer;
    private AppBarLayout appBarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_event, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_event);
        setToolbarSettings(toolbar, activity, main_activity);


        initItems(view);
        setItemAnimations();

        viewPager2.setAdapter(new ViewPagerEventFragmentAdapter(getActivity()));

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Any");
                    break;
                case 1:
                    tab.setText("Friends");
                    break;
            }
        }).attach();

        pullNewEvent.setOnClickListener(view1 -> {
            circle.setVisibility(View.VISIBLE);
            circle.startAnimation(animationCircle);
        });

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

    private void initItems(View v) {
        tabLayout = v.findViewById(R.id.event_main_tablayout);
        viewPager2 = v.findViewById(R.id.event_main_viewpager);

        appBarLayout = v.findViewById(R.id.app_bar_layout_in_main_event);

        try {
            isUnExploid = this.getArguments().getBoolean("circle_anim");
        } catch (NullPointerException e){
            isUnExploid = false;
        }

        pullNewEvent = v.findViewById(R.id.event_pull_new);
        circle = v.findViewById(R.id.circle_in_event);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity activity, AppCompatActivity main_activity) {
        if (tbar != null) {
            activity.setSupportActionBar(tbar);
            tbar.setTitle(R.string.title_event);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, tbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }
    }


}
