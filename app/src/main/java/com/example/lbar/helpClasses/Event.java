package com.example.lbar.helpClasses;

public class Event {
    public Event() {
    }

    private String ev_author_id;
    private String ev_header;
    private String ev_text;
    private String ev_image;
    private int ev_likes;

    public Event(String ev_author_id, String ev_header, String ev_text, String ev_image, int ev_likes) {
        this.ev_author_id = ev_author_id;
        this.ev_header = ev_header;
        this.ev_text = ev_text;
        this.ev_image = ev_image;
        this.ev_likes = ev_likes;
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
}
