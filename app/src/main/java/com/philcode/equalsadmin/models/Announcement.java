package com.philcode.equalsadmin.models;

public class Announcement {

    public String postImage;
    public String postUid;
    public String postContentTitle;
    public String postDescription;
    public String formattedDate;
    

    public Announcement(String postImage, String postUid, String postContentTitle, String postDescription, String formattedDate) {
        this.postImage = postImage;
        this.postUid = postUid;
        this.postContentTitle = postContentTitle;
        this.postDescription = postDescription;
        this.formattedDate = formattedDate;
    }

    public Announcement(){

    }

    public String getPostImage() {
        return postImage;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostContentTitle() {
        return postContentTitle;
    }

    public void setPostContentTitle(String postContentTitle) {
        this.postContentTitle = postContentTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
