package com.example.lbar.fragments.mainMenuFragments.roomsFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

    private boolean pointerSync = true;
    private boolean pointerScrambles = true;
    private boolean pointerButtons = true;

    private ArrayList<MaterialCardView> mcdListPuzzleMode;
    private ArrayList<MaterialCardView> mcdListSettings;
    private ArrayList<TextView> tvListSettings;
    private MaterialAlertDialogBuilder mdBuilderPuzzleChoice;
    private AlertDialog dialog;
    private View puzzlesView;
    private View settingsSoloView;

    private TextView chronometer;
    private TextView pMode;
    private LinearLayout layout;
    private ImageView backImageView;
    private ImageView settingsImageView;
    private ConstraintLayout bar;

    private String[] scrambleArray_classic;
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

    private boolean buttonFlag = true;
    private String[] puzzleNames = {"2 x 2", "3 x 3", "4 x 4", "5 x 5", "6 x 6", "7 x 7",
            "Pyraminx", "Sqube", "Clock", "Megaminx", "Square-1"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_rooms, container, false);
        puzzlesView = inflater.inflate(R.layout.dialog_puzzle_choice, container, false);
        settingsSoloView = inflater.inflate(R.layout.dialog_solo_settings, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        initItems(view);
        getSPInfo(main_activity);
        realiseClickListeners();
        realiseClickListenersForDialog(main_activity);

        // Experiments with scramble generator
        Log.d("RoomSoloFragment_disc", PUZZLE_DISCIPLINE + ";");
        scrambleTextView.setText(getRandomScramble(PUZZLE_DISCIPLINE));
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

    private void getSPInfo(AppCompatActivity activity) {
        boolean value = true;
        final SharedPreferences preferencesSettings = activity.getSharedPreferences("roomSoloSettings", 0);
        pointerSync = preferencesSettings.getBoolean("is sync", value);
        if (pointerSync){
            tvListSettings.get(0).setText("SYNC WITH COLLECTION");
        } else {
            tvListSettings.get(0).setText("DO NOT SYNC WITH COLLECTION");
        }
        pointerScrambles = preferencesSettings.getBoolean("is scramble", value);
        if (pointerScrambles){
            tvListSettings.get(1).setText("SHOW SCRAMBLES");
        } else {
            tvListSettings.get(1).setText("HIDE SCRAMBLES");
        }
        pointerButtons = preferencesSettings.getBoolean("is buttons", value);
        if (pointerButtons){
            tvListSettings.get(2).setText("SHOW FINE BUTTONS");
        } else {
            tvListSettings.get(2).setText("HIDE FINE BUTTONS");
        }
    }

    private void initItems(View v) {
        chronometer = v.findViewById(R.id.chronometer_solo);
        pMode = v.findViewById(R.id.rooms_puzzle_mode);

        btnPlusTwo = v.findViewById(R.id.button_plus_two);
        btnDNF = v.findViewById(R.id.button_DNF);
        btnDeleteResult = v.findViewById(R.id.button_delete_result);

        scrambleTextView = v.findViewById(R.id.scramble_textView);

        layout = v.findViewById(R.id.layout_solo_main);

        backImageView = v.findViewById(R.id.rooms_back_iv);
        settingsImageView = v.findViewById(R.id.rooms_settings);
        bar = v.findViewById(R.id.rooms_bar);

        customHandler = new Handler(Looper.getMainLooper());

        mcdListPuzzleMode = new ArrayList<>();
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_0));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_1));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_2));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_3));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_4));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_type_5));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_pyraminx));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_sqube));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_clock));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_megaminx));
        mcdListPuzzleMode.add(puzzlesView.findViewById(R.id.dialog_puzzle_choice_square1));

        mcdListSettings = new ArrayList<>();
        mcdListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_synchronization));
        mcdListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_scrambles));
        mcdListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_buttons));

        tvListSettings = new ArrayList<>();
        tvListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_synchronization_txt));
        tvListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_scrambles_txt));
        tvListSettings.add(settingsSoloView.findViewById(R.id.room_solo_dialog_settings_of_buttons_txt));

        // Scrambles from res
        scrambleArray_classic = getResources().getStringArray(R.array.scrambles_for_classic);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void realiseClickListeners() {
        // Кнопка возврата
        backImageView.setOnClickListener(view13 -> {
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

                        buttonFlag = true;
                        chronometer.setText("00:000");

                        startTime = 0L;
                        timeInMS = 0L;
                        timeSwapBuffer = 0L;
                        updateTime = 0L;

                        btnPlusTwo.setVisibility(View.INVISIBLE);
                        btnDNF.setVisibility(View.INVISIBLE);
                        btnDeleteResult.setVisibility(View.INVISIBLE);
                        backImageView.setVisibility(View.INVISIBLE);
                        settingsImageView.setVisibility(View.INVISIBLE);
                        pMode.setVisibility(View.INVISIBLE);
                        scrambleTextView.setVisibility(View.INVISIBLE);

                        layout.setBackgroundResource(R.color.colorChronometerPress); // pressed state
                        bar.setBackgroundResource(R.color.colorChronometerPress); // pressed state
                    } else {
                        Snackbar.make(getView(), "Time is: " + chronometer.getText(), BaseTransientBottomBar.LENGTH_SHORT).show();

                        if (pointerButtons){
                            btnPlusTwo.setVisibility(View.VISIBLE);
                            btnDNF.setVisibility(View.VISIBLE);
                            btnDeleteResult.setVisibility(View.VISIBLE);
                        }
                        backImageView.setVisibility(View.VISIBLE);
                        settingsImageView.setVisibility(View.VISIBLE);
                        pMode.setVisibility(View.VISIBLE);
                        if (pointerScrambles){
                            scrambleTextView.setText(getRandomScramble(PUZZLE_DISCIPLINE));
                            scrambleTextView.setVisibility(View.VISIBLE);
                        } else {scrambleTextView.setText("");}

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

        // Кнопка вызова диалога настроек
        settingsImageView.setOnClickListener(view -> {
            mdBuilderPuzzleChoice = new MaterialAlertDialogBuilder(getContext());

            mdBuilderPuzzleChoice.setTitle("Settings");
            mdBuilderPuzzleChoice.setBackground(getResources().getDrawable(R.drawable.dialog_drawable, null));

            if (settingsSoloView.getParent() != null) {
                ((ViewGroup) settingsSoloView.getParent()).removeView(settingsSoloView);
            }
            mdBuilderPuzzleChoice.setView(settingsSoloView);
            dialog = mdBuilderPuzzleChoice.show();
        });

        // Кнопка штрафа
        btnPlusTwo.setOnClickListener(view -> {
            if (buttonFlag){
                updateTime += 2000;
                chronometer.setText(convertFromMStoString(updateTime));
                buttonFlag = false;
            }
        });

        // Кнопка незачёта сборки
        btnDNF.setOnClickListener(view -> {
            if (buttonFlag){
                updateTime = -2L;
                chronometer.setText("DNF");
                buttonFlag = false;
            }
        });

        // Кнопка удаления результата
        btnDeleteResult.setOnClickListener(view -> {
            if (buttonFlag){
                chronometer.setText("00:000");
                updateTime = 0L;
                buttonFlag = false;
            }
        });
    }

    private void updateDataBaseStatistic() {
        if (pointerSync){
            cube.updateStatistics(updateTime);
        }
    }

    private void realiseClickListenersForDialog(AppCompatActivity activity) {

        final SharedPreferences preferencesSettings = activity.getSharedPreferences("roomSoloSettings", 0);

        for (int i = 0; i < mcdListPuzzleMode.size(); i++) {
            int finalI = i;
            mcdListPuzzleMode.get(i).setOnClickListener(view -> {

                if (updateTime != 0L){
                    updateDataBaseStatistic();
                }

                chronometer.setText("00:000");

                startTime = 0L;
                timeInMS = 0L;
                timeSwapBuffer = 0L;
                updateTime = 0L;

                pMode.setText(puzzleNames[finalI]);
                PUZZLE_DISCIPLINE = finalI;
                cube = new Cube(PUZZLE_DISCIPLINE);
                if (pointerScrambles){
                    scrambleTextView.setText(getRandomScramble(PUZZLE_DISCIPLINE));
                } else {scrambleTextView.setText("");}
                dialog.dismiss();
            });
        }

        // Sync settings
        mcdListSettings.get(0).setOnClickListener(view -> {
            if (pointerSync){
                tvListSettings.get(0).setText("DO NOT SYNC WITH COLLECTION");
                preferencesSettings.edit().putBoolean("is sync", false).apply();
                pointerSync = false;
            } else {
                tvListSettings.get(0).setText("SYNC WITH COLLECTION");
                preferencesSettings.edit().putBoolean("is sync", true).apply();
                pointerSync = true;
            }
        });

        //Scramble settings
        mcdListSettings.get(1).setOnClickListener(view -> {
            if (pointerScrambles){
                tvListSettings.get(1).setText("HIDE SCRAMBLES");
                preferencesSettings.edit().putBoolean("is scrambles", false).apply();
                scrambleTextView.setVisibility(View.INVISIBLE);
                pointerScrambles = false;
            } else {
                tvListSettings.get(1).setText("SHOW SCRAMBLES");
                preferencesSettings.edit().putBoolean("is scrambles", true).apply();
                scrambleTextView.setVisibility(View.VISIBLE);
                pointerScrambles = true;
            }
        });

        //Buttons settings
        mcdListSettings.get(2).setOnClickListener(view -> {
            if (pointerButtons){
                tvListSettings.get(2).setText("HIDE FINE BUTTONS");
                preferencesSettings.edit().putBoolean("is buttons", false).apply();
                btnPlusTwo.setVisibility(View.INVISIBLE);
                btnDNF.setVisibility(View.INVISIBLE);
                btnDeleteResult.setVisibility(View.INVISIBLE);
                pointerButtons = false;
            } else {
                tvListSettings.get(2).setText("SHOW FINE BUTTONS");
                preferencesSettings.edit().putBoolean("is buttons", true).apply();
                pointerButtons = true;
            }
        });
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

    private String getRandomScramble(int puzzle_discipline) {
        if (puzzle_discipline >= 6){ return "Sorry, scrambles for this puzzle is currently unavailable"; }
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        Log.d("RoomSoloFragment_disc2", puzzle_discipline + ";");

        String[] libArray = scrambleArray_classic;
        int border = 17;
        int scrambleLength = 0;

        int prevInd = -1;
        int counter = 0;

        if (puzzle_discipline == 0){
            scrambleLength = 9;
        } else if (puzzle_discipline == 1){
            scrambleLength = 19;
        } else if (puzzle_discipline == 2){
            scrambleLength = 47; border = 35;
        } else if (puzzle_discipline == 3){
            scrambleLength = 60; border = 35;
        } else if (puzzle_discipline == 4){
            scrambleLength = 80; border = 53;
        } else if (puzzle_discipline == 5){
            scrambleLength = 100; border = 53;
        }

        while (true){
            // 100 - чем больше число, тем равновероятнее событие
            // + Math.round(scrambleLength / 2) - позиция пика вероятности
            int ind = (int) Math.round(100*random.nextGaussian() + Math.round(scrambleLength / 2.0));

            if (ind >= 0 && ind <= border){
                if (!((Math.abs(prevInd - ind) % 3) == 0))
                { // Проверяем повторения, ходы <->, ходы <<- ->, ходы параллельных граней;
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

    @Override
    public void onStart() {

        chronometer.setText("00:000");

        startTime = 0L;
        timeInMS = 0L;
        timeSwapBuffer = 0L;
        updateTime = 0L;

        if (pointerScrambles){
            scrambleTextView.setText(getRandomScramble(PUZZLE_DISCIPLINE));
        } else {scrambleTextView.setText("");}

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (updateTime != 0L){
                    updateDataBaseStatistic();
                }
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.alpha_to_high_1000, R.anim.to_top)
                            .replace(R.id.fragment_container,
                                    new RoomsStartFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
