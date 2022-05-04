package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;

public class RoomsKeyBattleFragment extends Fragment {

    private TextView chronometer;
    private TextView pMode;
    private LinearLayout layout;
    private ImageView backImageView;
    private ConstraintLayout bar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_key_battle_rooms, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        initItems(view);

        backImageView.setOnClickListener(view1 -> {
            //TODO: При попытке выхода спрашивать - действительно ли пользователь этого хочет
            // и если да, то удалять его из списка мемберов
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                        .replace(R.id.fragment_container,
                                new RoomsMainBattleFragment()).commit();
            } catch (Exception D) {
                Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initItems(View v) {
        chronometer = v.findViewById(R.id.chronometer_key_battle);
        pMode = v.findViewById(R.id.rooms_key_battle_puzzle_mode);
        layout = v.findViewById(R.id.layout_key_battle_chronometer);
        backImageView = v.findViewById(R.id.rooms_key_battle_back_iv);
        bar = v.findViewById(R.id.rooms_key_battle_bar);
    }
}
