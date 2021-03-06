package com.example.lbar.helpClasses;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String us_id;
    private String us_name;
    private String us_email;
    private String us_birthday;
    private String image;
    private String us_status;
    private String search_tool;
    private ArrayList<String> us_friends = new ArrayList<>();

    public String getSearch_tool() {
        return search_tool;
    }

    public void setSearch_tool(String search_tool) {
        this.search_tool = search_tool;
    }

    public User(String us_name, String us_email, String us_birthday, String image, String search_tool, String us_status, String us_id) {
        this.us_name = us_name;
        this.us_email = us_email;
        this.us_birthday = us_birthday;
        this.image = image;
        this.us_status = us_status;
        this.search_tool = search_tool;
        this.us_id = us_id;
    }

    public User(String us_name, String us_email, String us_birthday, String image, String search_tool, String us_status, String us_id, ArrayList<String> us_friends) {
        this.us_name = us_name;
        this.us_email = us_email;
        this.us_birthday = us_birthday;
        this.image = image;
        this.us_status = us_status;
        this.search_tool = search_tool;
        this.us_id = us_id;
        this.us_friends = us_friends;
    }

    public String getUs_name() {
        return us_name;
    }

    public void setUs_name(String us_name) {
        this.us_name = us_name;
    }

    public String getUs_email() {
        return us_email;
    }

    public void setUs_email(String us_email) {
        this.us_email = us_email;
    }

    public String getUs_birthday() {
        return us_birthday;
    }

    public void setUs_birthday(String us_birthday) {
        this.us_birthday = us_birthday;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUs_status() {
        return us_status;
    }

    public void setUs_status(String us_status) {
        this.us_status = us_status;
    }

    public User() {
    }

    public String getUs_id() {
        return us_id;
    }

    public ArrayList<String> getUs_friends() {
        return us_friends;
    }

    public void setUs_friends(ArrayList<String> us_friends) {
        this.us_friends = us_friends;
    }
}
