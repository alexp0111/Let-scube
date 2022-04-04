package com.example.lbar.helpClasses;

import java.util.ArrayList;

public class Room {

    private String room_id;
    private String room_admin_id;
    private Long room_start_time;
    private ArrayList<RoomMember> room_members;

    public Room(String room_id, String room_admin_id, Long room_start_time, ArrayList<RoomMember> room_members) {
        this.room_id = room_id;
        this.room_admin_id = room_admin_id;
        this.room_start_time = room_start_time;
        this.room_members = room_members;
    }

    public Room() {
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_admin_id() {
        return room_admin_id;
    }

    public void setRoom_admin_id(String room_admin_id) {
        this.room_admin_id = room_admin_id;
    }

    public Long getRoom_start_time() {
        return room_start_time;
    }

    public void setRoom_start_time(Long room_start_time) {
        this.room_start_time = room_start_time;
    }

    public ArrayList<RoomMember> getRoom_members() {
        return room_members;
    }

    public void setRoom_members(ArrayList<RoomMember> room_members) {
        this.room_members = room_members;
    }
}
