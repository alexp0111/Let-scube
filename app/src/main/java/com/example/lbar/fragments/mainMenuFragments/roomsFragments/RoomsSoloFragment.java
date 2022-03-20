package com.example.lbar.fragments.mainMenuFragments.roomsFragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;
import com.example.lbar.fragments.mainMenuFragments.accountFragments.LogInFragment;
import com.example.lbar.helpClasses.Cube;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class RoomsSoloFragment extends Fragment {

    private boolean isRunning = false;

    private TextView chronometer;
    private LinearLayout layout;

    private MaterialButton plusTwo;
    private MaterialButton buttonStart;
    private MaterialButton buttonStop;

    private Handler customHandler;
    private Runnable updateTimerThread;

    private long startTime = 0L;
    private long timeInMS = 0L;
    private long timeSwapBuffer = 0L;
    private long updateTime = 0L;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_rooms, container, false);

        chronometer = view.findViewById(R.id.chronometer_solo);

        plusTwo = view.findViewById(R.id.button_plus_two);
        buttonStart = view.findViewById(R.id.rooms_solo_start_btn);
        buttonStop = view.findViewById(R.id.rooms_solo_stop_btn);
        layout = view.findViewById(R.id.layout_solo_main);

        customHandler = new Handler(Looper.getMainLooper());

        updateTimerThread = new Runnable() {
            @Override
            public void run() {
                timeInMS = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuffer + timeInMS;
                chronometer.setText(convertFromMStoString(updateTime));
                customHandler.postDelayed(this, 0);
            }
        };

        plusTwo.setOnClickListener(view1 -> {
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                        .replace(R.id.fragment_container,
                                new RoomsStartFragment()).commit();
            } catch (Exception D) {
                Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
            }
        });

        layout.setOnTouchListener((view12, motionEvent) -> {
            switch (motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    if (isRunning == false){
                        chronometer.setText("00:000");
                        layout.setBackgroundColor(Color.GREEN); // pressed state
                    } else {
                        Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();
                        startTime = 0L;
                        timeInMS = 0L;
                        timeSwapBuffer = 0L;
                        updateTime = 0L;
                        customHandler.removeCallbacks(updateTimerThread);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isRunning == false){
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        layout.setBackgroundResource(R.color.colorPrimary);
                        isRunning = true;
                    } else {
                        isRunning = false;
                    }
                    break;
            }
            return true;
        });

        //layout.setOnLongClickListener(view14 -> {
        //    startTime = SystemClock.uptimeMillis();
        //    customHandler.postDelayed(updateTimerThread, 0);
        //    return true;
        //});
//
        //layout.setOnClickListener(view15 -> {
        //    Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();
        //    startTime = 0L;
        //    timeInMS = 0L;
        //    timeSwapBuffer = 0L;
        //    updateTime = 0L;
        //    customHandler.removeCallbacks(updateTimerThread);
        //});


        //buttonStart.setOnClickListener(view13 -> {
        //    startTime = SystemClock.uptimeMillis();
        //    customHandler.postDelayed(updateTimerThread, 0);
        //});
//
        //buttonStop.setOnClickListener(view12 -> {
        //    Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();
        //    timeSwapBuffer+=timeInMS;
        //    customHandler.removeCallbacks(updateTimerThread);
        //});

        return view;
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
}
