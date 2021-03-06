package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.RoomMemberAdapter;
import com.example.lbar.helpClasses.Cube;
import com.example.lbar.helpClasses.Room;
import com.example.lbar.helpClasses.RoomMember;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class RoomsKeyBattleFragment extends Fragment {

    private DatabaseReference ref = FirebaseDatabase
            .getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
            .getReference("Rooms");

    private boolean isRunning = false;
    private Cube cube;

    private String roomID;
    private String newMemberID;
    private Room thisRoom = null;
    private RoomMember thisRoomMember = null;
    private ArrayList<Long> results;

    private RoomMemberAdapter roomMemberAdapter;
    private ArrayList<RoomMember> newList = new ArrayList<>();

    private Handler customHandlerForTimer;
    private Runnable updateTimerThread;

    private long startTime = 0L;
    private long timeInMS = 0L;
    private long timeSwapBuffer = 0L;
    private long updateTime = 0L;
    private long inspectionTime = 0L;
    private long delay = 0L;

    private TextView chronometer;
    private TextView pMode;
    private TextView scramble;
    private LinearLayout layout;
    private ImageView backImageView;
    private ConstraintLayout bar;
    private RecyclerView recyclerView;

    private static final long INSPECTION_TIME = 15000L;
    private String[] scrambleArray_classic;
    private String[] puzzleNames = {"2 x 2", "3 x 3", "4 x 4", "5 x 5", "6 x 6", "7 x 7",
            "Pyraminx", "Sqube", "Megaminx", "Clock", "Square-1"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_key_battle_rooms, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getRoomClass();
        initItems(view);
        results = new ArrayList<>();
        thisRoomMember = new RoomMember(newMemberID, false);
        realiseClickListeners();

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        roomMemberAdapter = new RoomMemberAdapter(getContext(), newList);
        recyclerView.setAdapter(roomMemberAdapter);

        updateTimerThread = new Runnable() {
            @Override
            public void run() {
                if (inspectionTime > 0) {
                    delay = 1000;
                    chronometer.setText(convertFromMStoString(inspectionTime));
                    inspectionTime -= 1000L;
                } else if (inspectionTime == 0) {
                    scramble.setText("");
                    delay = 0;
                    startTime = SystemClock.uptimeMillis();
                    inspectionTime = -1;
                } else {
                    delay = 0;
                    timeInMS = SystemClock.uptimeMillis() - startTime;
                    updateTime = timeSwapBuffer + timeInMS;
                    chronometer.setText(convertFromMStoString(updateTime));
                }
                customHandlerForTimer.postDelayed(this, delay);
            }
        };

        return view;
    }

    private void setUpControl() {
        if (thisRoom.isAllMembersPrepared()) {
            updatePreparation(false, false, true);
            scramble.setText(thisRoom.getRoom_scramble());

            scramble.setText(R.string.inspection_time);
            inspectionTime = INSPECTION_TIME; // inspection time
            startChronometer();
        }
    }

    private void startChronometer() {
        customHandlerForTimer.postDelayed(updateTimerThread, 0);
        layout.setBackgroundResource(R.color.colorPrimary);
        isRunning = true;
    }

    private void initItems(View v) {
        roomID = this.getArguments().getString("room_id");
        newMemberID = this.getArguments().getString("newMember_id");

        chronometer = v.findViewById(R.id.chronometer_key_battle);
        pMode = v.findViewById(R.id.rooms_key_battle_puzzle_mode);
        scramble = v.findViewById(R.id.scramble_key_battle_textView);
        layout = v.findViewById(R.id.layout_key_battle_chronometer);
        backImageView = v.findViewById(R.id.rooms_key_battle_back_iv);
        bar = v.findViewById(R.id.rooms_key_battle_bar);
        recyclerView = v.findViewById(R.id.key_battle_members_list);

        customHandlerForTimer = new Handler(Looper.getMainLooper());

        scrambleArray_classic = getResources().getStringArray(R.array.scrambles_for_classic);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void realiseClickListeners() {
        backImageView.setOnClickListener(view1 -> {
            startSureDialog();
        });

        // ??????, ?????????????????????? ??????????????
        layout.setOnTouchListener((view12, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isRunning) {
                        chronometer.setText("00:000");

                        startTime = 0L;
                        timeInMS = 0L;
                        timeSwapBuffer = 0L;
                        updateTime = 0L;

                        layout.setBackgroundResource(R.color.colorChronometerPress); // pressed state

                        updatePreparation(true, false, false);
                    } else {
                        Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();

                        scramble.setText(thisRoom.getRoom_scramble());
                        updateDataBaseStatistic();
                        updatePreparation(false, true, false);

                        customHandlerForTimer.removeCallbacks(updateTimerThread);
                        isRunning = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        });
    }

    private void updateDataBaseStatistic() {
        if (thisRoom.isRoom_collection_synchronization() && cube != null && updateTime != 0L){
            cube.updateStatistics(updateTime);
        }
    }

    private void updateResultsList() {
        RoomMember roomMember = new RoomMember(newMemberID, updateTime, false);
        thisRoom.getRoom_members().add(roomMember);
        ref.child(roomID).setValue(thisRoom);
    }

    private void updatePreparation(boolean preparation, boolean resUpdate, boolean scrambleUpdate) {
        for (int i = 0; i < thisRoom.getRoom_members().size(); i++) {
            if (thisRoom.getRoom_members().get(i).getMember_id().equals(newMemberID))
                thisRoom.getRoom_members().get(i).setMember_preparation(preparation);
        }

        ref.child(roomID).setValue(thisRoom).addOnCompleteListener(task -> {
            if (resUpdate && updateTime != 0)
                updateResultsList();
            if (scrambleUpdate && newMemberID.equals(thisRoom.getRoom_admin_id()))
                downloadNewScramble();
        });
    }

    private void getRoomClass() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    assert room != null;
                    if (room.getRoom_id().equals(roomID))
                        thisRoom = room;
                }
                if (thisRoom != null) {
                    pMode.setText(puzzleNames[thisRoom.getRoom_puzzle_discipline()]);
                    cube = new Cube(thisRoom.getRoom_puzzle_discipline(), getContext());
                }
                setUpControl();
                if (thisRoom.getRoom_members().size() - newList.size() == 1){
                    Log.d("UPDATE_RECYCLER", "false");
                    updateRecyclerView(false);
                }
                else if (thisRoom.getRoom_members().size() - newList.size() > 1){
                    Log.d("UPDATE_RECYCLER", "true");
                    updateRecyclerView(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void downloadNewScramble() {
        ref.child(roomID).child("room_scramble").setValue(getRandomScramble(thisRoom.getRoom_puzzle_discipline()));
    }

    private String getRandomScramble(int puzzle_discipline) {
        if (puzzle_discipline >= 6) {
            return "Sorry, scrambles for this puzzle is currently unavailable";
        }
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        Log.d("RoomSoloFragment_disc2", puzzle_discipline + ";");

        String[] libArray = scrambleArray_classic;
        int border = 17;
        int scrambleLength = 0;

        int prevInd = -1;
        int counter = 0;

        if (puzzle_discipline == 0) {
            scrambleLength = 9;
        } else if (puzzle_discipline == 1) {
            scrambleLength = 19;
        } else if (puzzle_discipline == 2) {
            scrambleLength = 47;
            border = 35;
        } else if (puzzle_discipline == 3) {
            scrambleLength = 60;
            border = 35;
        } else if (puzzle_discipline == 4) {
            scrambleLength = 80;
            border = 53;
        } else if (puzzle_discipline == 5) {
            scrambleLength = 100;
            border = 53;
        }

        while (true) {
            // 100 - ?????? ???????????? ??????????, ?????? ???????????????????????????? ??????????????
            // + Math.round(scrambleLength / 2) - ?????????????? ???????? ??????????????????????
            int ind = (int) Math.round(100 * random.nextGaussian() + Math.round(scrambleLength / 2.0));

            if (ind >= 0 && ind <= border) {
                if (!((Math.abs(prevInd - ind) % 3) == 0)) { // ?????????????????? ????????????????????, ???????? <->, ???????? <<- ->, ???????? ???????????????????????? ????????????;
                    Log.d("RoomsSoloFragment ++++++++", String.valueOf(ind));
                    prevInd = ind;
                    result.append(scrambleArray_classic[ind]).append(" ");
                    counter++;
                } else {
                    Log.d("RoomsSoloFragment collision", String.valueOf(ind));
                }
            } else {
                Log.d("RoomsSoloFragment bad", String.valueOf(ind));
            }
            if (counter == scrambleLength) return result.toString();
        }
    }

    private void updateRecyclerView(boolean fullUpdate) {
        if (fullUpdate) {
            newList.clear();
            newList.addAll(thisRoom.getRoom_members());
            roomMemberAdapter = new RoomMemberAdapter(getContext(), newList);
            recyclerView.setAdapter(roomMemberAdapter);
        } else {
            newList.add(thisRoom.getRoom_members().get(thisRoom.getRoom_members().size() - 1));
            roomMemberAdapter.notifyItemInserted(newList.size() - 1);
            recyclerView.scrollToPosition(newList.size() - 1);
        }
    }

    private void startSureDialog() {
        MaterialAlertDialogBuilder mdBuilder = new MaterialAlertDialogBuilder(getContext());
        mdBuilder.setTitle(R.string.room_exit_shure);

        if (thisRoom != null && thisRoom.memberAmount() == 1)
            mdBuilder.setMessage(R.string.room_will_be_deleted);
        mdBuilder.setBackground(getContext().getResources().getDrawable(R.drawable.dialog_drawable, null));

        mdBuilder.setNegativeButton(R.string.i_am_shure, (dialogInterface, i) -> {
            if (thisRoom != null && thisRoom.memberAmount() == 1) {
                ref.child(roomID).removeValue().addOnCompleteListener(task -> closeFragment());
            } else if (thisRoom != null) {
                ArrayList<RoomMember> tmpList = new ArrayList<>();
                for (int j = 0; j < thisRoom.getRoom_members().size(); j++) {
                    if (!thisRoom.getRoom_members().get(j).getMember_id().equals(newMemberID))
                        tmpList.add(thisRoom.getRoom_members().get(j));
                }
                thisRoom.getRoom_members().clear();
                thisRoom.getRoom_members().addAll(tmpList);
                tmpList.clear();
                if (thisRoom.getRoom_admin_id().equals(newMemberID)) {
                    thisRoom.setRoom_admin_id(thisRoom.getRoom_members().get(0).getMember_id());
                }
                ref.child(roomID).setValue(thisRoom).addOnCompleteListener(task -> {
                    closeFragment();
                });
            }
        });

        mdBuilder.show();
    }

    private String convertFromMStoString(long ms) {
        if (ms == -1L) return "Not enough info";
        String result = "";
        if (ms / 3600000 != 0) {
            result += strCorrection(ms / 3600000, 2) + ":";
        }
        ms %= 3600000;
        if (ms / 60000 != 0) {
            result += strCorrection(ms / 60000, 2) + ":";
        }
        ms %= 60000;
        result += strCorrection(ms / 1000, 2) + ":" + strCorrection(ms %= 1000, 3);

        return result;
    }

    private String strCorrection(long numToCorrect, int digits) {
        StringBuilder num = new StringBuilder(Long.toString(numToCorrect));
        while (num.length() < digits) num.insert(0, "0");
        return num.toString();
    }

    private void closeFragment() {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                    .replace(R.id.fragment_container,
                            new RoomsMainBattleFragment()).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        chronometer.setText("00:000");

        startTime = 0L;
        timeInMS = 0L;
        timeSwapBuffer = 0L;
        updateTime = 0L;
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (isRunning) {
            isRunning = false;
            customHandlerForTimer.removeCallbacks(updateTimerThread);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("KeyBattle", "backPressed");
                startSureDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
