package com.example.lbar.helpClasses;


import java.util.ArrayList;

public class Cube {
    private int cube_type;
    private String cube_name;

    private ArrayList<String> puzzle_build_pb_statistics = new ArrayList<>(); //pb, pb3, pb5, pb12, pb100;
    private ArrayList<String> puzzle_build_avg_statistics = new ArrayList<>();

    public Cube(int cube_type, String cube_name, ArrayList<String> puzzle_build_pb_statistics, ArrayList<String> puzzle_build_avg_statistics) {
        this.cube_type = cube_type;
        this.cube_name = cube_name;

        this.puzzle_build_pb_statistics = puzzle_build_pb_statistics;
        this.puzzle_build_avg_statistics = puzzle_build_avg_statistics;
    }

    public Cube(int cube_type) {
        this.cube_type = cube_type;
    }

    public String convertFromMStoString(long ms){
        String result = "";
        result += strCorrection(ms / 3600000, 2) + ":"; ms %= 3600000;
        result += strCorrection(ms / 60000, 2) + ":"; ms %= 60000;
        result += strCorrection(ms / 1000, 2) + ":" + strCorrection(ms %= 1000, 3);

        return result;
    }

    private String strCorrection(long numToCorrect, int digits){
        StringBuilder num = new StringBuilder(Long.toString(numToCorrect));
        while (num.length() < digits) num.insert(0, "0");
        return num.toString();
    }

    public int getCube_type() {
        return cube_type;
    }

    public void setCube_type(int cube_type) {
        this.cube_type = cube_type;
    }

    public String getCube_name() {
        return cube_name;
    }

    public void setCube_name(String cube_name) {
        this.cube_name = cube_name;
    }

    public ArrayList<String> getPuzzle_build_pb_statistics() {
        return puzzle_build_pb_statistics;
    }

    public void setPuzzle_build_pb_statistics(ArrayList<String> puzzle_build_pb_statistics) {
        this.puzzle_build_pb_statistics = puzzle_build_pb_statistics;
    }

    public ArrayList<String> getPuzzle_build_avg_statistics() {
        return puzzle_build_avg_statistics;
    }

    public void setPuzzle_build_avg_statistics(ArrayList<String> puzzle_build_avg_statistics) {
        this.puzzle_build_avg_statistics = puzzle_build_avg_statistics;
    }
}
