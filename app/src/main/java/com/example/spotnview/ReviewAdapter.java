package com.example.spotnview;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder>{

    private List<Review> reviewList;
    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the review_item.xml layout and return a new ReviewViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(itemView);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Review> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Bind the data to the views in the ViewHolder
        Review review = reviewList.get(position);
        // Bind the data to the views in the ViewHolder
        holder.reviewText.setText(review.getReviewText());
        holder.userName.setText(review.getUserName());
        RatingBar ratingBar = holder.ratingBar;
        ratingBar.setRating(review.getRatingBar().getRating());
        Log.d("ReviewAdapter", "onBindViewHolder - Position: " + position);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
