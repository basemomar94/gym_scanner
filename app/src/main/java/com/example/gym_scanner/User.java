package com.example.gym_scanner;

public class User {
    String name;
    String userid;
    String time;
    String admin="Bassem";

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public User(String name, String userid, String time, String admin) {
        this.name = name;
        this.userid = userid;
        this.time = time;
        this.admin = admin;
    }
}
