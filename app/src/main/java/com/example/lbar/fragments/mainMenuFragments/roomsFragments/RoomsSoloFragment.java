package com.example.lbar.fragments.mainMenuFragments.roomsFragments;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Cube;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Random;

public class RoomsSoloFragment extends Fragment {

    private boolean isRunning = false;
    private int PUZZLE_DISCIPLINE = 1;
    private Cube cube = new Cube(PUZZLE_DISCIPLINE);

    private ArrayList<MaterialCardView> mcdList;
    private MaterialAlertDialogBuilder mdBuilderPuzzleChoice;
    private AlertDialog dialog;
    private View puzzlesView;

    private TextView chronometer;
    private TextView pMode;
    private LinearLayout layout;
    private ImageView backView;
    private ImageView settingsView;
    private ConstraintLayout bar;

    private String[] scrambleArrayForType1;
    private TextView scrambleTextView;

    private MaterialButton btnPlusTwo;
    private MaterialButton btnDNF;
    private MaterialButton btnDeleteResult;

    private Handler customHandler;
    private Runnable updateTimerThread;

    private long startTime = 0L;
    private long timeInMS = 0L;
    private long timeSwapBuffer = 0L;
    private long updateTime = 0L;

    private boolean buttonflag = true;
    private String[] puzzleNames = {"2 x 2", "3 x 3", "4 x 4", "5 x 5", "6 x 6", "7 x 7",
            "Pyraminx", "Sqube", "Clock", "Megaminx", "Square-1"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_rooms, container, false);
        puzzlesView = inflater.inflate(R.layout.dialog_puzzle_choice, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        initItems(view);
        realiseClickListners();
        realiseClickListnersForDialog();

        // Experiments with scramble generator
        scrambleTextView.setText(getRandomScrable(PUZZLE_DISCIPLINE));
        //

        DrawerLayout drawer = main_activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        updateTimerThread = new Runnable() {
            @Override
            public void run() {
                timeInMS = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuffer + timeInMS;
                chronometer.setText(convertFromMStoString(updateTime));
                customHandler.postDelayed(this, 0);
            }
        };

        return view;
    }

    private void initItems(View v) {
        chronometer = v.findViewById(R.id.chronometer_solo);
        pMode = v.findViewById(R.id.rooms_puzzle_mode);

        btnPlusTwo = v.findViewById(R.id.button_plus_two);
        btnDNF = v.findViewById(R.id.button_DNF);
        btnDeleteResult = v.findViewById(R.id.button_delete_result);

        scrambleTextView = v.findViewById(R.id.scramble_textView);
        scrambleArrayForType1 = getResources().getStringArray(R.array.scrambles_for_3x3);

        layout = v.findViewById(R.id.layout_solo_main);

        backView = v.findViewById(R.id.rooms_back_iv);
        settingsView = v.findViewById(R.id.rooms_settings);
        bar = v.findViewById(R.id.rooms_bar);

        customHandler = new Handler(Looper.getMainLooper());

        mcdList = new ArrayList<>();
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_0));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_1));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_2));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_3));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_4));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_5));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_pyraminx));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_sqube));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_clock));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_megaminx));
        mcdList.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_square1));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void realiseClickListners() {
        // Кнопка возврата
        backView.setOnClickListener(view13 -> {
            try {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                        .replace(R.id.fragment_container,
                                new RoomsStartFragment()).commit();
            } catch (Exception D) {
                Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
            }
        });

        // Фон, считывающий нажатия
        layout.setOnTouchListener((view12, motionEvent) -> {
            switch (motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    if (!isRunning){
                        if (updateTime != 0L){
                            updateDataBaseStatistic();
                        }

                        buttonflag = true;
                        chronometer.setText("00:000");

                        startTime = 0L;
                        timeInMS = 0L;
                        timeSwapBuffer = 0L;
                        updateTime = 0L;

                        btnPlusTwo.setVisibility(View.INVISIBLE);
                        btnDNF.setVisibility(View.INVISIBLE);
                        btnDeleteResult.setVisibility(View.INVISIBLE);
                        backView.setVisibility(View.INVISIBLE);
                        settingsView.setVisibility(View.INVISIBLE);
                        pMode.setVisibility(View.INVISIBLE);
                        scrambleTextView.setVisibility(View.INVISIBLE);

                        layout.setBackgroundResource(R.color.colorChronometerPress); // pressed state
                        bar.setBackgroundResource(R.color.colorChronometerPress); // pressed state
                    } else {
                        Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();

                        btnPlusTwo.setVisibility(View.VISIBLE);
                        btnDNF.setVisibility(View.VISIBLE);
                        btnDeleteResult.setVisibility(View.VISIBLE);
                        backView.setVisibility(View.VISIBLE);
                        settingsView.setVisibility(View.VISIBLE);
                        pMode.setVisibility(View.VISIBLE);
                        scrambleTextView.setText(getRandomScrable(PUZZLE_DISCIPLINE));
                        scrambleTextView.setVisibility(View.VISIBLE);

                        customHandler.removeCallbacks(updateTimerThread);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isRunning){
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        layout.setBackgroundResource(R.color.colorPrimary);
                        bar.setBackgroundResource(R.color.colorPrimary);
                        isRunning = true;
                    } else {
                        isRunning = false;
                    }
                    break;
            }
            return true;
        });

        // Выбор дисциплины
        pMode.setOnClickListener(view -> {
            mdBuilderPuzzleChoice = new MaterialAlertDialogBuilder(getContext());

            mdBuilderPuzzleChoice.setTitle("Discipline");
            mdBuilderPuzzleChoice.setBackground(getResources().getDrawable(R.drawable.dialog_drawable, null));

            if (puzzlesView.getParent() != null) {
                ((ViewGroup) puzzlesView.getParent()).removeView(puzzlesView);
            }
            mdBuilderPuzzleChoice.setView(puzzlesView);
            dialog = mdBuilderPuzzleChoice.show();
        });

        // Кнопка штрафа
        btnPlusTwo.setOnClickListener(view -> {
            if (buttonflag){
                updateTime += 2000;
                chronometer.setText(convertFromMStoString(updateTime));
                buttonflag = false;
            }
        });

        // Кнопка незачёта сборки
        btnDNF.setOnClickListener(view -> {
            if (buttonflag){
                updateTime = -2L;
                chronometer.setText("DNF");
                buttonflag = false;
            }
        });

        //TODO:
        // Кнопка удаления результата
        btnDeleteResult.setOnClickListener(view -> {
            if (buttonflag){
                chronometer.setText("00:000");
                updateTime = 0L;
                buttonflag = false;
            }
        });
    }

    private void updateDataBaseStatistic() {
        cube.updateStatistics(updateTime);
    }

    private void realiseClickListnersForDialog() {
        for (int i = 0; i < mcdList.size(); i++) {
            int finalI = i;
            mcdList.get(i).setOnClickListener(view -> {
                pMode.setText(puzzleNames[finalI]);
                PUZZLE_DISCIPLINE = finalI;
                cube = new Cube(PUZZLE_DISCIPLINE);
                dialog.dismiss();
            });
        }
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

    private String getRandomScrable(int puzzle_discipline) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        int prevInd = -1;
        int counter = 0;

        while (true){
            int ind = (int) Math.round(20*random.nextGaussian() + 9);
            if (ind >= 0 && ind <= 17){
                if (!((Math.abs(prevInd - ind) % 3) == 0))
                { // Проверяем повторения, ходы <->, ходы <<- ->, ходы параллельных граней;
                    Log.d("RoomsSoloFragment ++++++++", String.valueOf(ind));
                    prevInd = ind;
                    result.append(scrambleArrayForType1[ind]).append(" ");
                    counter++;
                } else {
                    Log.d("RoomsSoloFragment collision", String.valueOf(ind));
                }
            } else {
                Log.d("RoomsSoloFragment bad", String.valueOf(ind));
            }
            if (counter == 19) return result.toString();
        }
    }

    @Override
    public void onStart() {

        chronometer.setText("00:000");

        startTime = 0L;
        timeInMS = 0L;
        timeSwapBuffer = 0L;
        updateTime = 0L;

        scrambleTextView.setText(getRandomScrable(0));

        Log.d("RoomsSoloFragment", "onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d("RoomsSoloFragment", "onStop");
        if (updateTime != 0L){
            updateDataBaseStatistic();
        }
        super.onStop();
    }
}
