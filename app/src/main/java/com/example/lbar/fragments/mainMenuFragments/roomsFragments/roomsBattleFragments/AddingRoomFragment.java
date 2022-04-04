package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddingRoomFragment extends Fragment {

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_room, container, false);

        fab = view.findViewById(R.id.room_push);
        fab.setOnClickListener(view1 -> {
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top).replace(R.id.fragment_container,
                        new RoomsMainBattleFragment()).commit();
            } catch (Exception D) {
                Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
