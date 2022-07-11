package com.example.lbar.helpClasses;

public class Liker {
    private String like_id;
    private String like_user_id;
    private String like_event_id;

    public Liker(String like_id, String like_user_id, String like_event_id) {
        this.like_id = like_id;
        this.like_user_id = like_user_id;
        this.like_event_id = like_event_id;
    }

    public Liker() {
    }

    public String getLike_id() {
        return like_id;
    }

    public void setLike_id(String like_id) {
        this.like_id = like_id;
    }

    public String getLike_user_id() {
        return like_user_id;
    }

    public void setLike_user_id(String like_user_id) {
        this.like_user_id = like_user_id;
    }

    public String getLike_event_id() {
        return like_event_id;
    }

    public void setLike_event_id(String like_event_id) {
        this.like_event_id = like_event_id;
    }
}
