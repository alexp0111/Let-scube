package com.example.lbar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lbar.fragments.mainMenuFragments.peopleFragments.friendsFragments.FriendsFragment;
import com.example.lbar.fragments.mainMenuFragments.peopleFragments.friendsFragments.FriendsInteractionsFragment;

public class ViewPagerFriendsFragmentAdapter extends FragmentStateAdapter {

    public ViewPagerFriendsFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FriendsFragment();
            case 1:
                return new FriendsInteractionsFragment();
            default:
                return null;

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
