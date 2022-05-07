package com.example.lbar.helpClasses;

import java.util.ArrayList;

public class RoomMember {

    private String member_id;
    private ArrayList<Long> member_results;
    private boolean member_preparation;

    public RoomMember(String member_id, boolean member_preparation) {
        this.member_id = member_id;
        this.member_preparation = member_preparation;
    }

    public RoomMember(String member_id, ArrayList<Long> member_results, boolean member_preparation) {
        this.member_id = member_id;
        this.member_results = member_results;
        this.member_preparation = member_preparation;
    }

    public RoomMember() {
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public ArrayList<Long> getMember_results() {
        return member_results;
    }

    public void setMember_results(ArrayList<Long> member_results) {
        this.member_results = member_results;
    }

    public boolean getMember_preparation() {
        return member_preparation;
    }

    public void setMember_preparation(boolean member_preparation) {
        this.member_preparation = member_preparation;
    }
}
