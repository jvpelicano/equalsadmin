package com.philcode.equalsadmin.models;

public class Admin {

    private String uid;
    private String fname;
    private String lname;
    private String email;
    private String image;
    private String accountType;

    public Admin(String uid, String fname, String lname, String email, String image, String accountType) {
        this.uid = uid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.image = image;
        this.accountType = accountType;
    }

    public Admin(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
