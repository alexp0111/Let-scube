package com.example.lbar.helpClasses;


import java.util.ArrayList;

public class Cube {
    private int cube_type;
    private String cube_name;

    private ArrayList<Long> puzzle_build_pb_statistics; //pb, pb3, pb5, pb12, pb100;
    private ArrayList<Long> puzzle_build_avg_statistics;

    public Cube(int cube_type, String cube_name, ArrayList<Long> puzzle_build_pb_statistics, ArrayList<Long> puzzle_build_avg_statistics) {
        this.cube_type = cube_type;
        this.cube_name = cube_name;

        this.puzzle_build_pb_statistics = puzzle_build_pb_statistics;
        this.puzzle_build_avg_statistics = puzzle_build_avg_statistics;
    }

    public Cube() {
    }

    public String bestInfo() { //TODO: make logic of best results forming
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
        Long result = 0L;
        int realNumber = 0;
        for (int i = puzzle_build_avg_statistics.size() - 1; i >= puzzle_build_avg_statistics.size() - number; i--) {
            if (puzzle_build_avg_statistics.get(i) != -1L){
                result += puzzle_build_avg_statistics.get(i);
                realNumber++;
            }
        }
        if (realNumber < number) return -1L;
        return result / number;
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

    public int getCube_type() {
        return cube_type;
    }

    public String getCube_name() {
        return cube_name;
    }

    public void setCube_name(String cube_name) {
        this.cube_name = cube_name;
    }

    public ArrayList<Long> getPuzzle_build_pb_statistics() {
        return puzzle_build_pb_statistics;
    }

    public void setPuzzle_build_pb_statistics(ArrayList<Long> puzzle_build_pb_statistics) {
        this.puzzle_build_pb_statistics = puzzle_build_pb_statistics;
    }

    public ArrayList<Long> getPuzzle_build_avg_statistics() {
        return puzzle_build_avg_statistics;
    }

    public void setPuzzle_build_avg_statistics(ArrayList<Long> puzzle_build_avg_statistics) {
        this.puzzle_build_avg_statistics = puzzle_build_avg_statistics;
    }
}
