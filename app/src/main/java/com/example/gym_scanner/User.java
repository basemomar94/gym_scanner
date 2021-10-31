package com.example.gym_scanner;

public class User {
    String name;
    String userid;
    String time;

    public User() {
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User(String name, String userid, String time) {
        this.name = name;
        this.userid = userid;
        this.time=time;

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




}
