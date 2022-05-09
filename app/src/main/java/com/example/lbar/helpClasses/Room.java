package com.example.lbar.helpClasses;

import java.util.ArrayList;

public class Room {

    private String room_id;
    private String room_admin_id;
    private Long room_start_time;
    private ArrayList<RoomMember> room_members;

    private String room_header;
    private String room_description;
    private String room_access;
    private String room_password;
    private boolean room_collection_synchronization; // false - without // true - with
    private Integer room_puzzle_discipline;
    private Integer room_max_number_of_members;
    private String room_scramble;

    public Room(String room_id,
                String room_admin_id,
                Long room_start_time,
                ArrayList<RoomMember> room_members,
                String room_header,
                String room_description,
                String room_access,
                String room_password,
                boolean room_collection_synchronization,
                Integer room_puzzle_discipline,
                Integer room_max_number_of_members, String room_scramble) {
        this.room_id = room_id;
        this.room_admin_id = room_admin_id;
        this.room_start_time = room_start_time;
        this.room_members = room_members;
        this.room_header = room_header;
        this.room_description = room_description;
        this.room_access = room_access;
        this.room_password = room_password;
        this.room_collection_synchronization = room_collection_synchronization;
        this.room_puzzle_discipline = room_puzzle_discipline;
        this.room_max_number_of_members = room_max_number_of_members;
        this.room_scramble = room_scramble;
    }

    public Room(String room_id,
                String room_admin_id,
                Long room_start_time,
                ArrayList<RoomMember> room_members,
                String room_header,
                String room_description,
                String room_access,
                boolean room_collection_synchronization,
                Integer room_puzzle_discipline,
                Integer room_max_number_of_members, String room_scramble) {
        this.room_id = room_id;
        this.room_admin_id = room_admin_id;
        this.room_start_time = room_start_time;
        this.room_members = room_members;
        this.room_header = room_header;
        this.room_description = room_description;
        this.room_access = room_access;
        this.room_collection_synchronization = room_collection_synchronization;
        this.room_puzzle_discipline = room_puzzle_discipline;
        this.room_max_number_of_members = room_max_number_of_members;
        this.room_scramble = room_scramble;
    }

    public Room() {
    }

    public int absoluteResultsNumber() {
        int result = 0;
        for (int i = 0; i < room_members.size(); i++) {
            if (room_members.get(i).getMember_results() != null)
                for (int j = 0; j < room_members.get(i).getMember_results().size(); j++) {
                    result++;
                }
        }
        return result;
    }

    public boolean isAllMembersPrepared() {
        for (int i = 0; i < room_members.size(); i++) {
            if (!room_members.get(i).getMember_preparation())
                return false;
        }
        return true;
    }

    public int indexOfMember(String id) {
        for (int i = 0; i < room_members.size(); i++) {
            if (room_members.get(i).getMember_id().equals(id))
                return i;
        }
        return -1;
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

    public String getRoom_header() {
        return room_header;
    }

    public void setRoom_header(String room_header) {
        this.room_header = room_header;
    }

    public String getRoom_description() {
        return room_description;
    }

    public void setRoom_description(String room_description) {
        this.room_description = room_description;
    }

    public String getRoom_access() {
        return room_access;
    }

    public void setRoom_access(String room_access) {
        this.room_access = room_access;
    }

    public boolean isRoom_collection_synchronization() {
        return room_collection_synchronization;
    }

    public void setRoom_collection_synchronization(boolean room_collection_synchronization) {
        this.room_collection_synchronization = room_collection_synchronization;
    }

    public Integer getRoom_puzzle_discipline() {
        return room_puzzle_discipline;
    }

    public void setRoom_puzzle_discipline(Integer room_puzzle_discipline) {
        this.room_puzzle_discipline = room_puzzle_discipline;
    }

    public Integer getRoom_max_number_of_members() {
        return room_max_number_of_members;
    }

    public void setRoom_max_number_of_members(Integer room_max_number_of_members) {
        this.room_max_number_of_members = room_max_number_of_members;
    }

    public String getRoom_password() {
        return room_password;
    }

    public void setRoom_password(String room_password) {
        this.room_password = room_password;
    }

    public String getRoom_scramble() {
        return room_scramble;
    }

    public void setRoom_scramble(String room_scramble) {
        this.room_scramble = room_scramble;
    }
}
