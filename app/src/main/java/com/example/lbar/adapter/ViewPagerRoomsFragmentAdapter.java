package com.example.lbar.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lbar.fragments.mainMenuFragments.eventFragments.AnyEventFragment;
import com.example.lbar.fragments.mainMenuFragments.eventFragments.FriendsEventFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments.RoomsPrivateBattleFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments.RoomsPublicBattleFragment;

public class ViewPagerRoomsFragmentAdapter extends FragmentStateAdapter {

    public ViewPagerRoomsFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RoomsPublicBattleFragment();
            case 1:
                return new RoomsPrivateBattleFragment();
            default:
                return null;

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
