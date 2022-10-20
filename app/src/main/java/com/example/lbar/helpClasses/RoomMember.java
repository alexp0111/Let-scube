package com.example.lbar.helpClasses;

import android.net.Uri;

import java.util.ArrayList;

public class RoomMember {

    private String member_id;
    private Long member_result;
    private boolean member_preparation;
    private String member_scramble_img;
    private String member_result_img;

    public RoomMember(String member_id, boolean member_preparation) {
        this.member_id = member_id;
        this.member_preparation = member_preparation;
    }

    public RoomMember(String member_id, Long member_result, boolean member_preparation) {
        this.member_id = member_id;
        this.member_result = member_result;
        this.member_preparation = member_preparation;
    }

    public RoomMember(String member_id, Long member_result, boolean member_preparation, String member_scramble_img, String member_result_img) {
        this.member_id = member_id;
        this.member_result = member_result;
        this.member_preparation = member_preparation;
        this.member_scramble_img = member_scramble_img;
        this.member_result_img = member_result_img;
    }

    public RoomMember() {
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public boolean getMember_preparation() {
        return member_preparation;
    }

    public void setMember_preparation(boolean member_preparation) {
        this.member_preparation = member_preparation;
    }

    public Long getMember_result() {
        return member_result;
    }

    public void setMember_result(Long member_result) {
        this.member_result = member_result;
    }
}
