package com.example.spotnview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class historyAdapter extends ArrayAdapter<historyItem> {
    private Context context;
    private List<historyItem> reviewList;
    private OnDeleteClickListener onDeleteClickListener;
    public historyAdapter(Context context, List<historyItem> reviewList) {
        super(context, 0);
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.history_item, parent, false);
        }


           historyItem currentReview = reviewList.get(position);

           // Find the views in the list item layout
           TextView dateTextView = itemView.findViewById(R.id.historyDate);
           TextView titleTextView = itemView.findViewById(R.id.historyTitle);
           TextView addressTextView = itemView.findViewById(R.id.historyAddress);

           // Set the values of the views
           dateTextView.setText(currentReview.getDate());
           titleTextView.setText(currentReview.getReviewTitle());
           addressTextView.setText(currentReview.getUserAddress());
            ImageButton deleteButton = itemView.findViewById(R.id.imageButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(position);
                    }
                }
        });


        return itemView;
    }
    @Override
    public int getCount() {
        return reviewList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }
}
