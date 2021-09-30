package com.philcode.equalsadmin.models;

public class Admin {

    private String uid;
    private String name;
    private String email;
    private String image;
    private String accountType;

    public Admin(String uid, String name, String email, String image, String accountType) {
        this.uid = uid;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
