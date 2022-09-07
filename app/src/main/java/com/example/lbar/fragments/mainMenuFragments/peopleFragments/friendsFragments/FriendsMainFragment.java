package com.example.lbar.fragments.mainMenuFragments.peopleFragments.friendsFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.ViewPagerFriendsFragmentAdapter;
import com.example.lbar.adapter.ViewPagerRoomsFragmentAdapter;
import com.example.lbar.fragments.mainMenuFragments.peopleFragments.PeopleFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsMainFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    private DrawerLayout drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_main_friends);
        setToolbarSettings(toolbar, main_activity);

        initItems(view);

        viewPager2.setAdapter(new ViewPagerFriendsFragmentAdapter(getActivity()));

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.friends);
                    break;
                case 1:
                    tab.setText("Interactions");
                    break;
            }
        }).attach();

        return view;
    }

    private void initItems(View v){
        tabLayout = v.findViewById(R.id.main_friends_tabLayout);
        viewPager2 = v.findViewById(R.id.main_friends_viewpager);
    }

    private void setToolbarSettings(Toolbar tbar, AppCompatActivity main_activity) {
        if (tbar != null) {
            tbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            tbar.setTitle(R.string.friends);

            drawer = main_activity.findViewById(R.id.drawer_layout);

            tbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            //.setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new PeopleFragment()).commit();
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
                            //.setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new PeopleFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}