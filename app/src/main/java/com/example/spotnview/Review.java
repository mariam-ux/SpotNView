package com.example.spotnview;

import android.widget.RatingBar;

import java.awt.font.TextAttribute;

public class Review {
    private String reviewText;
    private String userName;
    private RatingBar ratingBar;
    private String reviewDate;

    public Review(String userName, String reviewText, RatingBar ratingBar, String reviewDate) {
        this.reviewText = reviewText;
        this.userName = userName;
        this.ratingBar = ratingBar;
        this.reviewDate = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }
    public String getUserName() {
        return userName;
    }
    public RatingBar getRatingBar() { return ratingBar; }
    public String getReviewDate() { return reviewDate; }
}








