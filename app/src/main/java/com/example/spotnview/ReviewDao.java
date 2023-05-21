package com.example.spotnview;

import android.content.Context;

import java.util.List;

public interface ReviewDao {
    void addReview(Review review);
    List<Review> getAllReviews();


}
