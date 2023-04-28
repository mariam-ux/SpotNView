package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends BaseActivity {
    private String targetText;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_reviews;
    }
    private TextView text;
    private String placeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intent = getIntent();
        String detectedText = intent.getStringExtra("detectedText");
        if (detectedText != null) {
            int length = detectedText.length();
            Toast.makeText(this, "not null text", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "null text", Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_reviews);
        Log.d("slectedItemId", String.valueOf(R.id.navigation_reviews));

        //handle the review http requests
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String apiKey = getString(R.string.API_KEY);
        String encodedInput = null;
        try {
            encodedInput = URLEncoder.encode(detectedText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String autocompleteUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" +
                encodedInput +
                "&key=" + apiKey;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, autocompleteUrl ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray predictions = jsonObject.getJSONArray("predictions");
                            if (predictions.length() > 0) {
                                JSONObject place = predictions.getJSONObject(0);
                                placeId = place.getString("place_id"); // Assign the placeId to the member variable
                                String placeDetailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" +
                                        placeId +
                                        "&key=" + apiKey;
                                //retrieve the reviews
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, placeDetailsUrl,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    JSONObject result = jsonObject.getJSONObject("result");
                                                    JSONArray reviews = result.getJSONArray("reviews");

                                                    List<String> reviewList = new ArrayList<>();
                                                    for (int i = 0; i < reviews.length(); i++) {
                                                        JSONObject review = reviews.getJSONObject(i);
                                                        String reviewText = review.getString("text");
                                                        reviewList.add(reviewText);
                                                    }

                                                    // Handle the retrieved reviews
                                                    // You can access the reviewList here and do further processing or display them in your activity

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

        // You can now use the placeId in the onCreate() method or other methods in your activity

        //Parse the response and extract the place_id
        //Inside the onResponse() method, you can parse the JSON response and extract the place_id for the restaurant
    }

}