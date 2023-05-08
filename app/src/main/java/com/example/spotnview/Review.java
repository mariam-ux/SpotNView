package com.example.spotnview;

public class Review {
    private String reviewText;
    private float rating;
    private String userName;

    public Review(String reviewText, float rating, String userName) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.userName = userName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public float getRating() {
        return rating;
    }

    public String getUserName() {
        return userName;
    }
}








