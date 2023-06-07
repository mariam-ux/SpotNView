package com.example.spotnview;

public class historyItem {

    private String date;
    private String reviewTitle;
    private String userAddress;
    private String historyID;

    public historyItem(String date, String reviewTitle, String userAddress, String historyID) {
        this.date = date;
        this.reviewTitle = reviewTitle;
        this.userAddress = userAddress;
        this.historyID = historyID;
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

    public String getHistoryID() {
        return historyID;
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

    public void setHistoryID(String historyID) {
        this.historyID = historyID;
    }
}
