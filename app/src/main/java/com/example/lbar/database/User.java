package com.example.lbar.database;

public class User {

    public String us_name;
    public String us_email;
    public String us_birthday;
    public String image;
    public String us_status;

    public User(String us_name, String us_email, String us_birthday, String image, String us_status) {
        this.us_name = us_name;
        this.us_email = us_email;
        this.us_birthday = us_birthday;
        this.image = image;
        this.us_status = us_status;
    }

    public User() {
    }
}
