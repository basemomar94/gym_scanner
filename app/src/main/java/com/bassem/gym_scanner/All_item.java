package com.bassem.gym_scanner;

public class All_item {
    String fname;
    String lname;
    String phone;
    String mail;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public All_item(String fname, String lname, String phone, String mail) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.mail = mail;
    }
}
