package com.example.lbar.helpClasses;


import java.util.ArrayList;

public class Cube {
    private int cube_type;

    private String single_best;
    private String average_of_3;
    private String best_of_3;
    private String average_of_5;
    private String best_of_5;
    private String average_of_12;
    private String best_of_12;
    private String average_of_100;
    private String best_of_100;

    private ArrayList<String> puzzle_build_statistics = new ArrayList<>();

    public Cube(int cube_type, String single_best, String average_of_3, String best_of_3, String average_of_5, String best_of_5, String average_of_12, String best_of_12, String average_of_100, String best_of_100, ArrayList<String> puzzle_build_statistics) {
        this.cube_type = cube_type;
        this.single_best = single_best;
        this.average_of_3 = average_of_3;
        this.best_of_3 = best_of_3;
        this.average_of_5 = average_of_5;
        this.best_of_5 = best_of_5;
        this.average_of_12 = average_of_12;
        this.best_of_12 = best_of_12;
        this.average_of_100 = average_of_100;
        this.best_of_100 = best_of_100;
        this.puzzle_build_statistics = puzzle_build_statistics;
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

    public String getSingle_best() {
        return single_best;
    }

    public void setSingle_best(String single_best) {
        this.single_best = single_best;
    }

    public String getAverage_of_3() {
        return average_of_3;
    }

    public void setAverage_of_3(String average_of_3) {
        this.average_of_3 = average_of_3;
    }

    public String getBest_of_3() {
        return best_of_3;
    }

    public void setBest_of_3(String best_of_3) {
        this.best_of_3 = best_of_3;
    }

    public String getAverage_of_5() {
        return average_of_5;
    }

    public void setAverage_of_5(String average_of_5) {
        this.average_of_5 = average_of_5;
    }

    public String getBest_of_5() {
        return best_of_5;
    }

    public void setBest_of_5(String best_of_5) {
        this.best_of_5 = best_of_5;
    }

    public String getAverage_of_12() {
        return average_of_12;
    }

    public void setAverage_of_12(String average_of_12) {
        this.average_of_12 = average_of_12;
    }

    public String getBest_of_12() {
        return best_of_12;
    }

    public void setBest_of_12(String best_of_12) {
        this.best_of_12 = best_of_12;
    }

    public String getAverage_of_100() {
        return average_of_100;
    }

    public void setAverage_of_100(String average_of_100) {
        this.average_of_100 = average_of_100;
    }

    public String getBest_of_100() {
        return best_of_100;
    }

    public void setBest_of_100(String best_of_100) {
        this.best_of_100 = best_of_100;
    }

    public ArrayList<String> getPuzzle_build_statistics() {
        return puzzle_build_statistics;
    }

    public void setPuzzle_build_statistics(ArrayList<String> puzzle_build_statistics) {
        this.puzzle_build_statistics = puzzle_build_statistics;
    }
}
