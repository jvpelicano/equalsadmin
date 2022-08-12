package com.philcode.equalsadmin.models;

public class Employer {

    public String email;
    public String password;
    public String typeStatus;
    public String firstname;
    public String lastname;
    public String fullname;
    public String companybg;
    public String contact;
    public String avatar;

    public String empValidID;
    public String companyaddress;
    public String companycity;
    public String companyTelNum;
    public String branch;

    public Employer(){
    }

    public Employer(String email, String password, String typeStatus, String firstname, String lastname, String fullname, String companybg, String contact, String avatar, String empValidID, String companyaddress, String companycity, String companyTelNum, String branch) {
        this.email = email;
        this.password = password;
        this.typeStatus = typeStatus;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullname = fullname;
        this.companybg = companybg;
        this.contact = contact;
        this.avatar = avatar;
        this.empValidID = empValidID;
        this.companyaddress = companyaddress;
        this.companycity = companycity;
        this.companyTelNum = companyTelNum;
        this.branch = branch;
    }
    public Employer(String email, String password, String typeStatus, String firstname, String lastname, String fullname, String companybg, String contact, String avatar, String empValidID, String companyaddress, String companycity) {
        this.email = email;
        this.password = password;
        this.typeStatus = typeStatus;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullname = fullname;
        this.companybg = companybg;
        this.contact = contact;
        this.avatar = avatar;
        this.empValidID = empValidID;
        this.companyaddress = companyaddress;
        this.companycity = companycity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTypeStatus() {
        return typeStatus;
    }

    public void setTypeStatus(String typeStatus) {
        this.typeStatus = typeStatus;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCompanybg() {
        return companybg;
    }

    public void setCompanybg(String companybg) {
        this.companybg = companybg;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmpValidID() {
        return empValidID;
    }

    public void setEmpValidID(String empValidID) {
        this.empValidID = empValidID;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public String getCompanycity() {
        return companycity;
    }

    public void setCompanycity(String companycity) {
        this.companycity = companycity;
    }

    public String getCompanyTelNum() {
        return companyTelNum;
    }

    public void setCompanyTelNum(String companyTelNum) {
        this.companyTelNum = companyTelNum;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
