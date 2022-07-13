package com.example.lbar.helpClasses;

public class Comment {
    private String comment_id;
    private String comment_author_id;
    private String comment_event_id;
    private String comment_text;

    public Comment(String comment_author_id, String comment_event_id, String comment_text, String comment_id) {
        this.comment_id = comment_id;
        this.comment_author_id = comment_author_id;
        this.comment_event_id = comment_event_id;
        this.comment_text = comment_text;
    }

    public Comment() {
    }

    public String getComment_event_id() {
        return comment_event_id;
    }

    public void setComment_event_id(String comment_event_id) {
        this.comment_event_id = comment_event_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_author_id() {
        return comment_author_id;
    }

    public void setComment_author_id(String comment_author_id) {
        this.comment_author_id = comment_author_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }
}
