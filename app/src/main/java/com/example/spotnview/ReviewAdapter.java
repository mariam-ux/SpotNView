package com.example.spotnview;

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
        holder.ratingBar.setRating(review.getRating());
        holder.userName.setText(review.getUserName());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}