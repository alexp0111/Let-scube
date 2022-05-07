package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.SNTP.SNTPClient;
import com.example.lbar.helpClasses.Cube;
import com.example.lbar.helpClasses.Room;
import com.example.lbar.helpClasses.RoomMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RoomsKeyBattleFragment extends Fragment {

    //FIXME: Время, получаемое через системные часы не одинаковое на разных устройствах.
    // разница в две секунды примерно. И секундомер не запускается............

    //TODO: IDEA: Если в комнтае уже идёт сборка - попросить пользователя подождать
    // и заупстить колесико загрузки

    private DatabaseReference ref = FirebaseDatabase
            .getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
            .getReference("Rooms");

    //private Calendar calendar;

    private boolean isRunning = false;
    private Cube cube = new Cube(1);

    private String roomID;
    private String newMemberID;
    private Room thisRoom = null;
    private RoomMember thisRoomMember = null;

    private Handler customHandlerForTimer;
    private Runnable updateTimerThread;
    //private Runnable currentTimeCheckerThread;

    private long startTime = 0L;
    private long timeInMS = 0L;
    private long timeSwapBuffer = 0L;
    private long updateTime = 0L;

    private TextView chronometer;
    private TextView pMode;
    private LinearLayout layout;
    private ImageView backImageView;
    private ConstraintLayout bar;

    private String[] puzzleNames = {"2 x 2", "3 x 3", "4 x 4", "5 x 5", "6 x 6", "7 x 7",
            "Pyraminx", "Sqube", "Megaminx", "Clock", "Square-1"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_key_battle_rooms, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        SNTPClient.getDate(TimeZone.getDefault(), new SNTPClient.Listener() {
            @Override
            public void onTimeResponse(String rawDate, Date date, Exception ex) {
                //date.getTime()
                Log.d("SNTP", rawDate);
            }
        });

        //MuTime.enableDiskCaching(getContext());
        //MuTime.buildCustomSntpRequest("time.google.com");
//
        //try {
        //    long m = MuTime.now();
        //    Log.d("qwe", m + "");
        //} catch (MissingTimeDataException e) {
        //    e.printStackTrace();
        //}

        //calendar = Calendar.getInstance();

        getRoomClass();
        initItems(view);
        thisRoomMember = new RoomMember(newMemberID, false);
        realiseClickListeners();

        updateTimerThread = new Runnable() {
            @Override
            public void run() {
                timeInMS = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuffer + timeInMS;
                chronometer.setText(convertFromMStoString(updateTime));
                customHandlerForTimer.postDelayed(this, 0);
            }
        };

        //currentTimeCheckerThread = new Runnable() {
        //    @Override
        //    public void run() {
        //        RoomsKeyBattleFragment.this.startChronometer();
        //        customHandlerForTimer.postDelayed(this, 0);
        //    }
        //};

        return view;
    }

    private void setUpAdminControl() {
        if (thisRoom.isAllMembersPrepared()){
            startChronometer();
            //long newTime = System.currentTimeMillis() + 5000;
            //Log.d("TimeChecker", newTime + "");
            //thisRoom.setRoom_start_time(newTime);
            //updatePreparation(false, true);
        }
    }

    private void startChronometer() {
        //Log.d("TimeChecker-start", System.currentTimeMillis() + "");
        //if (System.currentTimeMillis() == thisRoom.getRoom_start_time()){
            Log.d("KeyBattle", "DONE");
            //customHandlerForTimer.removeCallbacks(currentTimeCheckerThread);
            //isRunning = true;
            //updatePreparation(false, false);

            startTime = SystemClock.uptimeMillis();
            customHandlerForTimer.postDelayed(updateTimerThread, 0);
            layout.setBackgroundResource(R.color.colorPrimary);
            isRunning = true;
            //return 0;
        //}
        //return -1;
    }

    private void initItems(View v) {
        roomID = this.getArguments().getString("room_id");
        newMemberID = this.getArguments().getString("newMember_id");

        chronometer = v.findViewById(R.id.chronometer_key_battle);
        pMode = v.findViewById(R.id.rooms_key_battle_puzzle_mode);
        layout = v.findViewById(R.id.layout_key_battle_chronometer);
        backImageView = v.findViewById(R.id.rooms_key_battle_back_iv);
        bar = v.findViewById(R.id.rooms_key_battle_bar);

        customHandlerForTimer = new Handler(Looper.getMainLooper());
        //customHandlerForChecker = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void realiseClickListeners() {
        backImageView.setOnClickListener(view1 -> {
            startSureDialog();
        });

        // Фон, считывающий нажатия
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

                        updatePreparation(true, false);
                    } else {
                        Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();

                        customHandlerForTimer.removeCallbacks(updateTimerThread);
                        updatePreparation(false, false);
                        isRunning = false;
                        //thisRoom.setRoom_start_time(0L);
                        //ref.child(roomID).setValue(thisRoom);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isRunning) {
                        //isRunning = true;
                    } else {
                        //isRunning = false;
                    }
                    break;
            }
            return true;
        });
    }

    private void updatePreparation(boolean preparation, boolean isThreadStart) {
        thisRoomMember.setMember_preparation(preparation);

        int index = thisRoom.indexOfMember(thisRoomMember.getMember_id());

        for (int i = 0; i < thisRoom.getRoom_members().size(); i++) {
            Log.d("KeyBattle-members", thisRoom.getRoom_members().get(i).getMember_id());
        }

        if (index != -1) {
            Log.d("KeyBattle", "indexFound");
            thisRoom.getRoom_members().get(index).setMember_preparation(preparation);

            if (!isThreadStart){
                ref.child(roomID).child("room_members").child(Integer.toString(index))
                        .setValue(thisRoomMember);
            } //else if (thisRoom.getRoom_start_time() != 0L)
            //ref.child(roomID).setValue(thisRoom)
            //        .addOnCompleteListener(task -> {
            //            customHandlerForTimer.postDelayed(currentTimeCheckerThread, 0);
            //        });
        } else {
            Log.d("KeyBattle", "indexNotFound");
        }
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
                if (thisRoom != null)
                    pMode.setText(puzzleNames[thisRoom.getRoom_puzzle_discipline()]);

                //if (thisRoomMember.getMember_id().equals(thisRoom.getRoom_admin_id()))
                setUpAdminControl();
                //else customHandlerForTimer.postDelayed(currentTimeCheckerThread, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startSureDialog() {
        MaterialAlertDialogBuilder mdBuilder = new MaterialAlertDialogBuilder(getContext());
        mdBuilder.setTitle("Are you sure you want to exit?");

        if (thisRoom != null && thisRoom.getRoom_members().size() == 1)
            mdBuilder.setMessage("The room will be deleted after that");
        mdBuilder.setBackground(getContext().getResources().getDrawable(R.drawable.dialog_drawable, null));

        mdBuilder.setNegativeButton(R.string.i_am_shure, (dialogInterface, i) -> {
            if (thisRoom != null && thisRoom.getRoom_members().size() == 1) {
                ref.child(roomID).removeValue().addOnCompleteListener(task -> closeFragment());
            } else if (thisRoom != null){
                for (int j = 0; j < thisRoom.getRoom_members().size(); j++) {
                    RoomMember roomMember = thisRoom.getRoom_members().get(j);
                    if (roomMember.getMember_id().equals(newMemberID))
                        thisRoom.getRoom_members().remove(j);
                }
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

        if (isRunning){
            isRunning = false;
            customHandlerForTimer.removeCallbacks(updateTimerThread);
        }
        //customHandlerForTimer.removeCallbacks(currentTimeCheckerThread);

        //TODO: Продумать эту логику и учесть все возможные ошибки,
        // если пользователь уйдёт в обход диалога
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
