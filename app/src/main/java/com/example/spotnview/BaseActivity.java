package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setupBottomNavigationBar();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks here
        int itemId = item.getItemId();
        Intent intent = null;
        if (itemId == R.id.navigation_reviews) {
            intent = new Intent(this, ReviewsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (itemId == R.id.navigation_history) {
            intent = new Intent(this, HistoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (itemId == R.id.navigation_profile) {
            if (!(this instanceof ProfileActivity)) {
                intent = new Intent(this, ProfileActivity.class);
            }

        } else if (itemId == R.id.navigation_signin) {
            intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }
        if (intent != null) {
            startActivity(intent);
            return true;
        } else {
            return false;
        }


    }

    protected void setupBottomNavigationBar() {
        // Set selected item
        int menuId = getBottomNavigationMenuId();
        bottomNavigationView.setSelectedItemId(menuId);

    }




    protected abstract int getContentViewId();

    protected abstract int getBottomNavigationMenuId();
}