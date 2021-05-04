package com.example.inductionapp2021.Utils;

public class Friends {
    private  String course, profileImageUrl,status, username;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Friends() {
    }

    public Friends(String course, String profileImageUrl, String status, String username) {
        this.course = course;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.username = username;
    }
}
