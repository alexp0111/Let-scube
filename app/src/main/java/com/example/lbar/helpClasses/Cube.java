package com.example.lbar.helpClasses;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lbar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Cube {

    private Context mContext;
    private Long lastElementOfListBuffer;
    private DatabaseReference ref;

    private int cube_type;
    private String cube_name;

    private ArrayList<Long> puzzle_build_pb_statistics; //pb, pb3, pb5, pb12, pb100;
    private ArrayList<Long> puzzle_build_avg_statistics;

    public Cube(int cube_type, String cube_name, ArrayList<Long> puzzle_build_pb_statistics, ArrayList<Long> puzzle_build_avg_statistics, Context mContext) {
        this.cube_type = cube_type;
        this.cube_name = cube_name;

        this.puzzle_build_pb_statistics = puzzle_build_pb_statistics;
        this.puzzle_build_avg_statistics = puzzle_build_avg_statistics;
        this.mContext = mContext;
    }

    public Cube(int cube_type, Context mContext) {
        this.cube_type = cube_type;
        this.mContext = mContext;

        puzzle_build_pb_statistics = new ArrayList<Long>();
        puzzle_build_avg_statistics = new ArrayList<Long>();

        ref = FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Collection")
                .child(String.valueOf(cube_type));
    }

    public Cube() {
    }

    public String bestInfo() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < puzzle_build_pb_statistics.size() - 1; i++) {
            result.append(convertFromMStoString(puzzle_build_pb_statistics.get(i))).append("\n");
        }
        result.append(convertFromMStoString(puzzle_build_pb_statistics.get(puzzle_build_pb_statistics.size() - 1)));
        return result.toString();
    }

    public String avgInfo() {
        StringBuilder result = new StringBuilder();
        result.append(convertFromMStoString(getThisAvg(1))).append("\n");
        result.append(convertFromMStoString(getThisAvg(3))).append("\n");
        result.append(convertFromMStoString(getThisAvg(5))).append("\n");
        result.append(convertFromMStoString(getThisAvg(12))).append("\n");
        result.append(convertFromMStoString(getThisAvg(100)));
        return result.toString();
    }

    private Long getThisAvg(int number) {
        long result = 0L;
        long minValue = 360000000L;
        long maxValue = -100L;
        int numOfDNF = 0;

        for (int i = puzzle_build_avg_statistics.size() - 1; i >= puzzle_build_avg_statistics.size() - number; i--) {
            if (puzzle_build_avg_statistics.get(i) == -1L) {
                return -1L;
            } else if (puzzle_build_avg_statistics.get(i) == -2L) {
                numOfDNF++;
            } else if (puzzle_build_avg_statistics.get(i) > 0L) {
                long tmp = puzzle_build_avg_statistics.get(i);
                result += tmp;
                if (tmp <= minValue) minValue = tmp;
                if (tmp >= maxValue) maxValue = tmp;
                Log.d("CubeAvg", "cycle " + i + ": " + tmp + "; minValue: " + minValue + "; maxValue: " + maxValue);
            }
        }
        Log.d("CubeAvg", "================================");
        if ((number == 100 && numOfDNF >= 5) || (number != 100 && numOfDNF >= 2) || ((number == 1 || number == 3) && numOfDNF == 1)) {
            return -2L;
        }
        if (numOfDNF == 1) {
            return (result - minValue) / (number - 2);
        }
        if (number == 1 || number == 3) {
            return result / number;
        }
        return (result - minValue - maxValue) / (number - 2);
    }

    private String convertFromMStoString(long ms) {
        if (ms == -1L) return mContext.getString(R.string.not_enough_info);
        if (ms == -2L) return "DNF";
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

    // For DataBase
    // For DataBase

    public int getCube_type() {
        return cube_type;
    }

    public String getCube_name() {
        return cube_name;
    }

    public ArrayList<Long> getPuzzle_build_pb_statistics() {
        return puzzle_build_pb_statistics;
    }

    public ArrayList<Long> getPuzzle_build_avg_statistics() {
        return puzzle_build_avg_statistics;
    }

    // For DataBase
    // For DataBase

    public Context get_mContext() {
        return mContext;
    }

    public void set_mContext(Context mContext) {
        this.mContext = mContext;
    }

    private String strCorrection(long numToCorrect, int digits) {
        StringBuilder num = new StringBuilder(Long.toString(numToCorrect));
        while (num.length() < digits) num.insert(0, "0");
        return num.toString();
    }

    public void updateStatistics(Long newElement) {
        Log.d("Cube", "enter");
        ref.child("puzzle_build_avg_statistics")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Cube", "in dataChange");
                        Log.d("Cube", dataSnapshot.getValue().toString());
                        puzzle_build_avg_statistics.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("Cube", "in for");
                            Long elementOfArray;
                            try {
                                elementOfArray = snapshot.getValue(Long.class);
                                Log.d("Cube", String.valueOf(snapshot.getValue()));
                            } catch (Exception e) {
                                Log.d("Cube_error", String.valueOf(snapshot.getValue()));
                                elementOfArray = -1L;
                            }
                            if (elementOfArray != null) {
                                puzzle_build_avg_statistics.add(elementOfArray);
                            }
                        }
                        downloadInfo(newElement);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void downloadInfo(Long newElement) {
        puzzle_build_avg_statistics.add(newElement);
        puzzle_build_avg_statistics.remove(0);
        ref.child("puzzle_build_avg_statistics").setValue(puzzle_build_avg_statistics)
                .addOnCompleteListener(task -> {
                    checkForPBUpdates();
                });
    }

    private void checkForPBUpdates() {
        ref.child("puzzle_build_pb_statistics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                puzzle_build_pb_statistics.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long elementOfArray;
                    try {
                        elementOfArray = snapshot.getValue(Long.class);
                    } catch (Exception e) {
                        elementOfArray = -1L;
                    }
                    if (elementOfArray != null) {
                        puzzle_build_pb_statistics.add(elementOfArray);
                    }
                }

                downloadPBInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void downloadPBInfo() {
        int[] tmp = {1, 3, 5, 12, 100};

        for (int i = 0; i < tmp.length; i++) {
            if ((puzzle_build_pb_statistics.get(i) <= -1L) ||
                    ((getThisAvg(tmp[i]) != -2L) && getThisAvg(tmp[i]) < puzzle_build_pb_statistics.get(i))) {
                ref.child("puzzle_build_pb_statistics")
                        .child(String.valueOf(i))
                        .setValue(getThisAvg(tmp[i]));
            }
        }
    }
}
