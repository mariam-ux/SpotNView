package com.example.spotnview;

import android.widget.RatingBar;

public class Review {
    private String reviewText;
    private String userName;
    private RatingBar ratingBar;

    public Review( String userName, String reviewText, RatingBar ratingBar) {
        this.reviewText = reviewText;
        this.userName = userName;
        this.ratingBar = ratingBar;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getUserName() {
        return userName;
    }
    public RatingBar getRatingBar() { return ratingBar; }
}








