package com.example.spotnview;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder{

    public TextView reviewText;
    public RatingBar ratingBar;
    public TextView userName;
    public TextView reviewDate;

    public ReviewViewHolder(@NonNull View itemView){
        super(itemView);
        reviewText = itemView.findViewById(R.id.reviewText);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        userName = itemView.findViewById(R.id.userName);
        reviewDate = itemView.findViewById(R.id.reviewDate);
    }

}
