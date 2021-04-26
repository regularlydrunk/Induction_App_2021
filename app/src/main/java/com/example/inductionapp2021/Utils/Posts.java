package com.example.inductionapp2021.Utils;

public class Posts {

    private String date, statusDescription, postImageURL, userProfileImage, username;

    public Posts() {
    }

    public Posts(String date, String statusDescription, String postImageURL, String userProfileImage, String username) {
        this.date = date;
        this.statusDescription = statusDescription;
        this.postImageURL = postImageURL;
        this.userProfileImage = userProfileImage;
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getPostImageURL() {
        return postImageURL;
    }

    public void setPostImageURL(String postImageURL) {
        this.postImageURL = postImageURL;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
