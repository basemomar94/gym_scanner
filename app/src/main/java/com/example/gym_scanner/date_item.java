package com.example.gym_scanner;

public class date_item {
    String date;
    String admin;
    String time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public date_item(String date, String admin, String time) {
        this.date = date;
        this.admin = admin;
        this.time = time;
    }
}
