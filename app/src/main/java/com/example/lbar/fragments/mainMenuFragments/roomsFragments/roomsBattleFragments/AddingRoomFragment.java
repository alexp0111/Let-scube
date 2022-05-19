package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.example.lbar.helpClasses.Room;
import com.example.lbar.helpClasses.RoomMember;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.lbar.MainActivity.reference;

public class AddingRoomFragment extends Fragment {

    private String userID;
    private String newRef;

    private TextInputEditText etHeader;
    private TextInputEditText etDescription;

    private String textHeader = "";
    private String textDescription = "";
    private String textPassword = "";

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

    private FloatingActionButton fabExtended;
    private FloatingActionButton fabApply;
    private FloatingActionButton fabDisable;

    private Animation miniUnExplosionAnimationClose;
    private Animation miniUnExplosionAnimationGoToRoom;
    private Animation rotateExtendedFabAnimation;
    private Animation rotateBackExtendedFabAnimation;
    private Animation startApplyFabAnimation;
    private Animation startDisableFabAnimation;
    private Animation closeApplyFabAnimation;
    private Animation closeDisableFabAnimation;

    private boolean fabFlag = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_room, container, false);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initItems(view);
        setItemAnimations();
        realiseClickListenersOnCards();
        realiseLongClickListenersOnCards(view);

        fabExtended.setOnClickListener(view1 -> startFabExtended());

        fabApply.setOnClickListener(view12 -> {
            try {
                textHeader = etHeader.getText().toString();
                textDescription = etDescription.getText().toString();
                textPassword = etPassword.getText().toString();
            } catch (NullPointerException npe){
                Snackbar.make(view, R.string.sww, Snackbar.LENGTH_SHORT).show();
                textHeader = "";
            }

            if (userID == null){
                Snackbar.make(view, R.string.sww, Snackbar.LENGTH_SHORT).show();
            } else if (textHeader.equals("")){
                Snackbar.make(view, "Please, do not let Header field empty.", Snackbar.LENGTH_SHORT).show();
            } else if (pointerAccess.equals("private") && textPassword.equals("")){
                Snackbar.make(view, "Please, do not let password field empty.", Snackbar.LENGTH_SHORT).show();
            } else {
                packAndSendAllDataToDB();
            }
        });

        fabDisable.setOnClickListener(view13 -> {
            etDescription.setText("");
            etHeader.setText("");

            getBackAnimationsStart(false);
        });

        return view;
    }

    private void packAndSendAllDataToDB() {
        // Getting address of room
        newRef = reference.child("Rooms").push().getKey();

        // First room member is Admin
        RoomMember adminMember = new RoomMember(userID, false);

        // Adding admin to room members list
        ArrayList<RoomMember> roomMembers = new ArrayList<>();
        roomMembers.add(adminMember);

        if (textDescription.equals("")) textDescription = "without";


        // Creating object of Room Class
        // (pointerMember + 2 - normalization)
        Room newRoom;
        if (pointerAccess.equals("private")){
            newRoom = new Room(newRef, userID, 0L, roomMembers,
                    textHeader, textDescription, pointerAccess, textPassword,
                    pointerSync, pointerDiscipline, pointerMember+2, getString(R.string.non_scr_round));
        } else {
            newRoom = new Room(newRef, userID, 0L, roomMembers,
                    textHeader, textDescription, pointerAccess,
                    pointerSync, pointerDiscipline, pointerMember+2, getString(R.string.non_scr_round));
        }


        // Send info to DB
        if (newRef != null) FirebaseDatabase.getInstance(getString(R.string.fdb_inst))
                .getReference("Rooms")
                .child(newRef)
                .setValue(newRoom)
                .addOnCompleteListener(task -> getBackAnimationsStart(true));
    }

    private void getBackAnimationsStart(boolean isRoomCreated) {
        if (isRoomCreated){
            fabApply.startAnimation(miniUnExplosionAnimationGoToRoom);
            fabDisable.startAnimation(miniUnExplosionAnimationGoToRoom);
            fabExtended.startAnimation(miniUnExplosionAnimationGoToRoom);
        } else {
            fabApply.startAnimation(miniUnExplosionAnimationClose);
            fabDisable.startAnimation(miniUnExplosionAnimationClose);
            fabExtended.startAnimation(miniUnExplosionAnimationClose);
        }

        fabApply.setVisibility(View.INVISIBLE);
        fabDisable.setVisibility(View.INVISIBLE);
        fabExtended.setVisibility(View.INVISIBLE);

        fabFlag = false;
    }

    private void setItemAnimations() {
        miniUnExplosionAnimationClose = AnimationUtils.loadAnimation(getContext(), R.anim.mini_circle_unexplosion);
        miniUnExplosionAnimationGoToRoom = AnimationUtils.loadAnimation(getContext(), R.anim.mini_circle_unexplosion);

        rotateExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotateBackExtendedFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        startApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl2);
        startDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_lvl1);

        closeApplyFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl2);
        closeDisableFabAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_lvl1);

        rotateExtendedFabAnimation.setDuration(200);
        rotateBackExtendedFabAnimation.setDuration(200);
        miniUnExplosionAnimationClose.setDuration(200);
        miniUnExplosionAnimationGoToRoom.setDuration(200);

        miniUnExplosionAnimationClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeFragment();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        miniUnExplosionAnimationGoToRoom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                goToCreatedRoom();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void closeFragment() {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top).replace(R.id.fragment_container,
                    new RoomsMainBattleFragment()).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }
    private void goToCreatedRoom(){
        RoomsKeyBattleFragment fragment = new RoomsKeyBattleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("room_id", newRef);
        bundle.putString("newMember_id", userID);
        fragment.setArguments(bundle);

        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.to_right, R.anim.from_right)
                    .replace(R.id.fragment_container,
                            fragment).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    private void realiseLongClickListenersOnCards(View v) {
        mcdDiscipline.setOnLongClickListener(view -> {
            Snackbar.make(v, "Here you can choose discipline to compete!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdMembers.setOnLongClickListener(view -> {
            Snackbar.make(v, "Here you can choose maximum number of competitors!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdSync.setOnLongClickListener(view -> {
            Snackbar.make(v, "Set SYNC to synchronize results with collection, else - set ASYNC!",
                    BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        });
        mcdAccess.setOnLongClickListener(view -> {
            Snackbar.make(v, "Here you can choose privacy of your room!",
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
        etHeader = v.findViewById(R.id.room_adding_header_txt);
        etDescription = v.findViewById(R.id.event_adding_description_txt);

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

        fabExtended = v.findViewById(R.id.room_push);
        fabApply = v.findViewById(R.id.room_apply);
        fabDisable = v.findViewById(R.id.room_disable);

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

    private void startFabExtended() {
        if (!fabFlag) {
            fabExtended.startAnimation(rotateExtendedFabAnimation);
            fabApply.setVisibility(View.VISIBLE);
            fabDisable.setVisibility(View.VISIBLE);

            fabApply.startAnimation(startApplyFabAnimation);
            fabDisable.startAnimation(startDisableFabAnimation);

            fabFlag = true;
        } else {
            fabExtended.startAnimation(rotateBackExtendedFabAnimation);
            fabApply.startAnimation(closeApplyFabAnimation);
            fabDisable.startAnimation(closeDisableFabAnimation);

            fabApply.setVisibility(View.INVISIBLE);
            fabDisable.setVisibility(View.INVISIBLE);

            fabFlag = false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startFabExtended();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
