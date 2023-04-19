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
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        setupBottomNavigationBar();
        // Set the default selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item clicks here
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.navigation_reviews) {
            startActivity(new Intent(BaseActivity.this, ReviewsActivity.class));
            return true;
        } else if (itemId == R.id.navigation_history) {
            startActivity(new Intent(BaseActivity.this, HistoryActivity.class));
            return true;
        }

        return false;
    }

    private void setupBottomNavigationBar() {
        // Set selected item
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(getContentViewId());
        menuItem.setChecked(true);
    }




    protected abstract int getContentViewId();
}