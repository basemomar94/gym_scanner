package com.example.gym_scanner;

public class date_item {
    String date;
    String admin;

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

    public date_item(String date, String admin) {
        this.date = date;
        this.admin = admin;
    }
}
