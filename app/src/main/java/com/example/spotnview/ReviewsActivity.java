package com.example.spotnview;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;


public class ReviewsActivity extends BaseActivity {
    private String targetText;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_reviews;
    }
    private TextView text;
    private String placeId;
    private List<Review> reviewList = new ArrayList<>();


    private  ReviewAdapter reviewAdapter;
    private RecyclerView reviewsRecyclerView;
    private String userAddress;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_reviews);
        Log.d("slectedItemId", String.valueOf(R.id.navigation_reviews));


        //recycleView initialization
        reviewsRecyclerView = findViewById(R.id.reviewsRecycler);
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewsRecyclerView.setAdapter(reviewAdapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String detectedText = intent.getStringExtra("detectedText");
        if (detectedText != null) {
            int length = detectedText.length();
            Toast.makeText(this, "not null text", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "null text", Toast.LENGTH_SHORT).show();
        }


        performLocationOperation(ReviewsActivity.this);
        Thread scrapingThread = new Thread(new RetrieveReviewsRunnable());
        scrapingThread.start();

    }

    public void performLocationOperation(Context context) {
        // Check if the location permissions are granted
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request the permissions
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permissions already granted, proceed with location-related operations
            onRequestPermissionsResult(REQUEST_LOCATION_PERMISSION,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED});
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, proceed with location-related operations
                 userAddress = retrieveUserAddress(ReviewsActivity.this);

            } else {
                // Location permissions denied, handle accordingly (e.g., show a message)
                // ...
            }
        }
    }

    private String retrieveUserAddress(Context context) {
        // Create an instance of LocationManager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if location services are enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Retrieve the last known location
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation == null) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                // Check if the last known location is available
                if (lastLocation != null) {
                    // Retrieve the latitude and longitude
                    double latitude = lastLocation.getLatitude();
                    double longitude = lastLocation.getLongitude();

                    // Use Geocoder to get the address from the latitude and longitude
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            userAddress = address.getAddressLine(0);
                            // Use the userAddress to specify the branch of the restaurant or perform any other required action
                            // ...
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return userAddress;
    }

    private class RetrieveReviewsRunnable implements Runnable {

        @Override
        public void run() {


            System.setProperty("webdriver.chrome.driver", "C:\\Users\\user\\AndroidStudioProjects\\SpotNView\\chromedriver.exe");
            ChromeOptions chromeOptions = new ChromeOptions();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("chrome");
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            URL seleniumGridUrl = null;
            try {
                seleniumGridUrl = new URL("http://172.23.112.1:4444/wd/hub");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            WebDriver driver = new RemoteWebDriver(seleniumGridUrl, capabilities);

            try {

                // Navigate to Google Maps
                driver.get("https://maps.google.com");

                // Search for the restaurant
                WebElement searchBox = driver.findElement(By.name("q"));
                searchBox.sendKeys("Restaurant Name");
                searchBox.submit();


                // Access the reviews
                WebElement reviewElement = driver.findElement(By.className(""));
                // Iterate over the review elements and extract the desired information

            }finally {
                // Close the driver
                driver.quit();
            }
        }
    }


}

