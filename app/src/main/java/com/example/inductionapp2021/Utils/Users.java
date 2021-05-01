package com.example.inductionapp2021.Utils;

public class Users {
    private String username, course, accommodation, campus, profileImage, status;

    public Users() {
    }

    public Users(String username, String course, String accommodation, String campus, String profileImage, String status) {
        this.username = username;
        this.course = course;
        this.accommodation = accommodation;
        this.campus = campus;
        this.profileImage = profileImage;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
