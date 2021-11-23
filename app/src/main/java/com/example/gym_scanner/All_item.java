package com.example.gym_scanner;

public class All_item {
    String fname;
    String lname;
    String userid;

 public    All_item(){

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public All_item(String fname, String lname, String userid) {
        this.fname = fname;
        this.lname = lname;
        this.userid = userid;
    }
}
