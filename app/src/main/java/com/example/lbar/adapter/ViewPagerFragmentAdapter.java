package com.example.lbar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lbar.fragments.mainMenuFragments.EventFragments.AnyEventFragment;
import com.example.lbar.fragments.mainMenuFragments.EventFragments.FriendsEventFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {


    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AnyEventFragment();
            case 1:
                return new FriendsEventFragment();
            default:
                return null;

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
