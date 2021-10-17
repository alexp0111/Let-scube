package com.example.lbar.fragments;

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

public class MessageFragment extends Fragment {

    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_mess);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Messages");

            drawer = main_activity.findViewById(R.id.drawer_layout);
            //Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            toggle.syncState();
            drawer.addDrawerListener(toggle);
        }

        return view;
    }
}
