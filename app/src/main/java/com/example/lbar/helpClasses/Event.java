package com.example.lbar.helpClasses;

import com.google.firebase.database.core.utilities.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Event {
    public Event() {
    }

    private String ev_id;
    private String ev_author_id;
    private String ev_header;
    private String ev_text;
    private String ev_image;
    private int ev_accessibility;   // 0 - without restrictions; 1 - only for friends
    private int ev_likes;
    private List<String> ev_liked_users;

    public Event(String ev_id, String ev_author_id, String ev_header, String ev_text, String ev_image, int ev_likes, int ev_accessibility, List<String> ev_liked_users) {
        this.ev_id = ev_id;
        this.ev_author_id = ev_author_id;
        this.ev_header = ev_header;
        this.ev_text = ev_text;
        this.ev_image = ev_image;
        this.ev_likes = ev_likes;
        this.ev_accessibility = ev_accessibility;
        this.ev_liked_users = ev_liked_users;
    }

    public Event(String ev_id, String ev_author_id, String ev_header, String ev_text, String ev_image, int ev_likes, int ev_accessibility) {
        this.ev_id = ev_id;
        this.ev_author_id = ev_author_id;
        this.ev_header = ev_header;
        this.ev_text = ev_text;
        this.ev_image = ev_image;
        this.ev_likes = ev_likes;
        this.ev_accessibility = ev_accessibility;
    }

    public String getEv_id() {
        return ev_id;
    }

    public void setEv_id(String ev_id) {
        this.ev_id = ev_id;
    }

    public int getEv_accessibility() {
        return ev_accessibility;
    }

    public void setEv_accessibility(int ev_accessibility) {
        this.ev_accessibility = ev_accessibility;
    }

    public String getEv_author_id() {
        return ev_author_id;
    }

    public void setEv_author_id(String ev_author_id) {
        this.ev_author_id = ev_author_id;
    }

    public String getEv_header() {
        return ev_header;
    }

    public void setEv_header(String ev_header) {
        this.ev_header = ev_header;
    }

    public String getEv_text() {
        return ev_text;
    }

    public void setEv_text(String ev_text) {
        this.ev_text = ev_text;
    }

    public String getEv_image() {
        return ev_image;
    }

    public void setEv_image(String ev_image) {
        this.ev_image = ev_image;
    }

    public int getEv_likes() {
        return ev_likes;
    }

    public void setEv_likes(int ev_likes) {
        this.ev_likes = ev_likes;
    }

    public List<String> getEv_liked_users() {
        if (ev_liked_users == null) return new ArrayList<>();
        return ev_liked_users;
    }

    public void setEv_liked_users(List<String> ev_liked_users) {
        this.ev_liked_users = ev_liked_users;
    }
}
