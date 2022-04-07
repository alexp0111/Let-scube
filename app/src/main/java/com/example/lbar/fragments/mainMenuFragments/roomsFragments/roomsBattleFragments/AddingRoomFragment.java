package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AddingRoomFragment extends Fragment {

    private int pointerDiscipline = 1;
    private int pointerMember = 0;
    private boolean pointerSync = true;
    private String pointerAccess = "public";

    private MaterialCardView mcdDiscipline;
    private MaterialCardView mcdMembers;
    private MaterialCardView mcdSync;
    private MaterialCardView mcdAccess;

    private ImageView imgDiscipline;
    private TextView txtMembers;
    private TextView txtSync;
    private ImageView imgAccess;

    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;

    private ArrayList<Integer> puzzlesList;
    private int[] membersList = {2, 3, 4, 5, 6, 7, 8, 9, 10};

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_room, container, false);

        initItems(view);
        realiseClickListenersOnCards();
        realiseLongClickListenersOnCards();

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

    private void realiseLongClickListenersOnCards() {
        mcdDiscipline.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Here you can choose discipline to compete!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdMembers.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Here you can choose maximum number of competitors!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdSync.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Set SYNC to synchronize results with collection, else - set ASYNC!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdAccess.setOnLongClickListener(view -> {
            Snackbar.make(getView(), "Here you can choose privacy of your room!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
    }

    private void realiseClickListenersOnCards() {
        mcdDiscipline.setOnClickListener(view -> {
            pointerDiscipline = (pointerDiscipline + 1) % 11;
            imgDiscipline.setImageResource(puzzlesList.get(pointerDiscipline));
        });
        mcdMembers.setOnClickListener(view -> {
            pointerMember = (pointerMember + 1) % 9;
            txtMembers.setText(String.valueOf(membersList[pointerMember]));
        });
        mcdSync.setOnClickListener(view -> {
            if (pointerSync) {
                pointerSync = false;
                txtSync.setText("ASYNC");
            } else {
                pointerSync = true;
                txtSync.setText("SYNC");
            }
        });
        mcdAccess.setOnClickListener(view -> {
            if (pointerAccess.equals("public")) {
                pointerAccess = "private";
                imgAccess.setImageResource(R.drawable.ic_lock);
                tilPassword.setVisibility(View.VISIBLE);
            } else {
                pointerAccess = "public";
                imgAccess.setImageResource(R.drawable.ic_lock_open);
                tilPassword.setVisibility(View.GONE);
            }
        });
    }

    private void initItems(View v) {
        mcdDiscipline = v.findViewById(R.id.room_settings_of_discipline);
        mcdMembers = v.findViewById(R.id.room_settings_of_max_members);
        mcdSync = v.findViewById(R.id.room_settings_of_synchronization);
        mcdAccess = v.findViewById(R.id.room_settings_of_access);

        imgDiscipline = v.findViewById(R.id.room_settings_of_discipline_img);
        imgAccess = v.findViewById(R.id.room_settings_of_access_img);
        txtMembers = v.findViewById(R.id.room_settings_of_max_members_txt);
        txtSync = v.findViewById(R.id.room_settings_of_synchronization_txt);

        tilPassword = v.findViewById(R.id.room_access_pass);
        etPassword = v.findViewById(R.id.room_et_pass);

        fab = v.findViewById(R.id.room_push);

        puzzlesList = new ArrayList<>();
        puzzlesList.add(R.drawable.cube_typo0);
        puzzlesList.add(R.drawable.cube_typo1);
        puzzlesList.add(R.drawable.cube_typo2);
        puzzlesList.add(R.drawable.cube_typo3);
        puzzlesList.add(R.drawable.cube_typo4);
        puzzlesList.add(R.drawable.cube_typo5);
        puzzlesList.add(R.drawable.cube_pyraminx);
        puzzlesList.add(R.drawable.cube_sqube);
        puzzlesList.add(R.drawable.cube_megaminx);
        puzzlesList.add(R.drawable.cube_clock);
        puzzlesList.add(R.drawable.cube_square1);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top).replace(R.id.fragment_container,
                            new RoomsMainBattleFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
