package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
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


public class HistoryActivity extends BaseActivity {
    private ListView listView;
    private List<historyItem> historyItemList;
    private historyAdapter adapter;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_history;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.historyList);


        Log.d("slectedItemId", String.valueOf(R.id.navigation_history));
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_history);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef.child(userId).child("history").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    historyItemList = new ArrayList<>();



                        if (dataSnapshot.exists()) {
                            for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                String date = reviewSnapshot.child("date").getValue(String.class);
                                String reviewTitle = reviewSnapshot.child("reviewTitle").getValue(String.class);
                                String userAddress = reviewSnapshot.child("userAddress").getValue(String.class);
                                Log.d("title", reviewTitle);
                                Log.d("date", date);
                                Log.d("address", userAddress);
                                historyItem historyItem = new historyItem(date, reviewTitle, userAddress);
                                historyItemList.add(historyItem);
                            }
                        }
                    else {
                            Toast.makeText(HistoryActivity.this, "user do not have any history yet!", Toast.LENGTH_SHORT).show();
                        }
                    adapter = new historyAdapter(HistoryActivity.this, historyItemList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("db error", databaseError.toString());
                }
            });
        } else {
            Toast.makeText(this, "user is not signed in", Toast.LENGTH_SHORT).show();
        }
    }

}
