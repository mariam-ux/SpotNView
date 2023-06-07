package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class historyDetails extends AppCompatActivity {
    private TextView reviewTitleTextView;
    private ListView reviewsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        reviewTitleTextView = findViewById(R.id.reviewTitleTextView);
        reviewsListView = findViewById(R.id.detailsList);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("historyID")) {
            String historyID = intent.getStringExtra("historyID");
            Log.d("get extra", historyID);
            // Retrieve the reviews from the database based on the reviewId
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("history")
                        .child(historyID);

                Log.d("history", historyRef.toString());
                historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("dataSnapshot", dataSnapshot.toString());
                        String reviewTitle = dataSnapshot.child("reviewTitle").getValue(String.class);
                        List<String> reviewsList = new ArrayList<>();
                        Log.d("reviewTitle", reviewTitle);
                        DataSnapshot reviewsSnapshot = dataSnapshot.child("reviews");
                        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
                            String review = reviewSnapshot.getValue(String.class);
                            reviewsList.add(review);
                            Log.d("review", review);
                        }

                        reviewTitleTextView.setText(reviewTitle);
                        DetailsAdapter adapter = new DetailsAdapter(historyDetails.this, reviewsList);
                        reviewsListView.setAdapter(adapter);
                        // Set the reviews in the ListView adapter

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("db error", databaseError.toString());
                    }
                });
            }
        }
    }
}