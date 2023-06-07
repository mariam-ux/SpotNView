package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference usersRef = database.getReference("users");
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String userId = currentUser.getUid();
    @Override
    protected int getContentViewId() {
        return R.layout.activity_history;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_history);

            listView = findViewById(R.id.historyList);
            FirebaseApp.initializeApp(this);

            Log.d("slectedItemId", String.valueOf(R.id.navigation_history));
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            // set the selected item
            bottomNavigationView.setSelectedItemId(R.id.navigation_history);
            // Initialize Firebase
            if (currentUser != null) {
                usersRef.child(userId).child("history").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        historyItemList = new ArrayList<>();
                                if (dataSnapshot.exists()) {
                                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                    String historyId = reviewSnapshot.getKey();
                                    String date = reviewSnapshot.child("date").getValue(String.class);
                                    String reviewTitle = reviewSnapshot.child("reviewTitle").getValue(String.class);
                                    String userAddress = reviewSnapshot.child("userAddress").getValue(String.class);
                                    Log.d("historyId", historyId);
                                    Log.d("title", reviewTitle);
                                    Log.d("date", date);
                                    Log.d("address", userAddress);
                                    historyItem historyItem = new historyItem(date, reviewTitle, userAddress, historyId);
                                    historyItem.setHistoryID(historyId);
                                    historyItemList.add(historyItem);
                                }
                            }
                        else {
                                Toast.makeText(HistoryActivity.this, "user do not have any history yet!", Toast.LENGTH_SHORT).show();
                            }
                        adapter = new historyAdapter(HistoryActivity.this, historyItemList);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                historyItem selectedItem = historyItemList.get(position);
                                String historyID = selectedItem.getHistoryID();
                                Intent intent = new Intent(HistoryActivity.this, historyDetails.class);
                                intent.putExtra("historyID", historyID);
                                startActivity(intent);
                            }
                        });
                        adapter.setOnDeleteClickListener(new historyAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick(int position) {
                                historyItem selectedItem = historyItemList.get(position);
                                String historyID = selectedItem.getHistoryID();
                                DatabaseReference historyRef = usersRef.child(userId).child("history").child(historyID);
                                historyRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(HistoryActivity.this, "History item deleted", Toast.LENGTH_SHORT).show();
                                            historyItemList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(HistoryActivity.this, "Failed to delete history item", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
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
