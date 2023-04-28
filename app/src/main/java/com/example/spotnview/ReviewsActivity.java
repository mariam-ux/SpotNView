package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ReviewsActivity extends BaseActivity {
    private String targetText;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_reviews;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_reviews);
        Log.d("slectedItemId", String.valueOf(R.id.navigation_reviews));

        //handle the review http requests
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Create an HTTP request using Volley to the Place Autocomplete endpoint
        String apiKey = "AIzaSyC1kLAxNb5eoqhFd698mhDphVuxdPDX-KE";
        String encodedInput = null;
        try {
            encodedInput = URLEncoder.encode(targetText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" +
                encodedInput +
                "&key=" + apiKey;

        //Make the request and handle the response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the autocomplete response here
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray reviews = jsonObject.getJSONArray("reviews");
                            if (reviews.length() > 0) {
                                JSONObject place = reviews.getJSONObject(0);
                                String placeId = place.getString("place_id");
                                // Use the placeId for further operations
                                for (int i = 0; i < reviews.length(); i++) {
                                    JSONObject review = reviews.getJSONObject(i);

                                    // Extract relevant information from the review
                                    String authorName = review.getString("author_name");
                                    int rating = review.getInt("rating");
                                    String reviewText = review.getString("text");

                                    // Do something with the review information
                                    // e.g., store it in a list or display it in your app's UI
                                }
                            } else {
                                // No predictions found for the restaurant name
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error here
                    }
                });

        requestQueue.add(stringRequest);

        //Parse the response and extract the place_id
        //Inside the onResponse() method, you can parse the JSON response and extract the place_id for the restaurant
    }

}