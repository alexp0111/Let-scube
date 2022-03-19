package com.example.lbar.fragments.mainMenuFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class RoomsFragment extends Fragment {

    private static MaterialCardView collective;
    private static MaterialCardView solo;

    private DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
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
            Snackbar.make(getView(), "Battle with anyone!", BaseTransientBottomBar.LENGTH_LONG).show();
        });
        solo.setOnClickListener(view -> {
            Snackbar.make(getView(), "Training with yourself!", BaseTransientBottomBar.LENGTH_LONG).show();
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
        }
    }
}
