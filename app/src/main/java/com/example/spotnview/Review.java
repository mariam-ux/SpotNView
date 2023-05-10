package com.example.spotnview;

public class Review {
    private String reviewText;
    private String userName;

    public Review( String userName, String reviewText) {
        this.reviewText = reviewText;
        this.userName = userName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getUserName() {
        return userName;
    }
}








