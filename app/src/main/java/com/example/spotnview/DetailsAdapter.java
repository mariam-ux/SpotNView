package com.example.spotnview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DetailsAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> detailList;

    public DetailsAdapter(@NonNull Context context,List<String> detailList) {
        super(context, 0);
        this.context = context;
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.detail_item, parent, false);
        }


        String currentReview = detailList.get(position);

        // Find the views in the list item layout
        TextView dateTextView = itemView.findViewById(R.id.historyTitle);

        // Set the values of the views
        dateTextView.setText(currentReview);

        return itemView;
    }
    @Override
    public int getCount() {
        return detailList.size();
    }
}
