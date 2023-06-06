package com.example.spotnview;

public class historyItem {

    private String date;
    private String reviewTitle;
    private String userAddress;

    public historyItem(String date, String reviewTitle, String userAddress) {
        this.date = date;
        this.reviewTitle = reviewTitle;
        this.userAddress = userAddress;
    }

    public String getDate() {
        return date;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
