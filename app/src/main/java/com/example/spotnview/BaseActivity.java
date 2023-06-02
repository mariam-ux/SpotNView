package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected BottomNavigationView bottomNavigationView;
    private Boolean shouldStartWebDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());



        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_signin);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks here
        Log.d("ONIS", "called");
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_reviews) {
            SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", BaseActivity.MODE_PRIVATE);
            shouldStartWebDriver = false;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("shouldStartWebdriver", false);
            editor.apply();
            if (!(this instanceof ReviewsActivity)) {

                startActivity(new Intent(this, ReviewsActivity.class));
                return true;
            }
            Log.d("slected", String.valueOf(itemId));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("getter", String.valueOf(bottomNavigationView.getSelectedItemId()));
                }
            }, 1000);
        } else if (itemId == R.id.navigation_history) {
            SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", BaseActivity.MODE_PRIVATE);
            shouldStartWebDriver = false;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("shouldStartWebdriver", false);
            editor.apply();
            if (!(this instanceof HistoryActivity)) {

                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            }
            Log.d("slected", String.valueOf(itemId));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("getter", String.valueOf(bottomNavigationView.getSelectedItemId()));
                }
            }, 1000);
        } else if (itemId == R.id.navigation_home) {
            SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", BaseActivity.MODE_PRIVATE);
            shouldStartWebDriver = false;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("shouldStartWebdriver", false);
            editor.apply();
            if (!(this instanceof HomeActivity)) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            }
            Log.d("slected", String.valueOf(itemId));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("getter", String.valueOf(bottomNavigationView.getSelectedItemId()));
                }
            }, 1000);
        } else if (itemId == R.id.navigation_signin) {
            SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", BaseActivity.MODE_PRIVATE);
            shouldStartWebDriver = false;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("shouldStartWebdriver", false);
            editor.apply();
            if (!(this instanceof MainActivity)){
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            Log.d("slected", String.valueOf(itemId));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("getter", String.valueOf(bottomNavigationView.getSelectedItemId()));
                }
            }, 1000);
        }
        return true;
    }

    protected abstract int getContentViewId();







}