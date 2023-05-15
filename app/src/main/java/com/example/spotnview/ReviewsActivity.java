package com.example.spotnview;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    private String detectedText;
    int durationInMinutes = 10;
    Duration duration = Duration.ofMinutes(durationInMinutes);
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
        detectedText = intent.getStringExtra("detectedText");
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
            chromeOptions.addArguments("--lang=en");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("chrome");
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            URL seleniumGridUrl = null;
            try {
                seleniumGridUrl = new URL("http://192.168.0.112:4444/wd/hub");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            WebDriver driver = new RemoteWebDriver(seleniumGridUrl, capabilities);

            try {


                // Navigate to Google Maps
                driver.get("https://maps.google.com/?hl=en");

                // Search for the restaurant
                WebElement searchBox = driver.findElement(By.name("q"));
                searchBox.sendKeys(detectedText + retrieveUserAddress(ReviewsActivity.this));
                searchBox.submit();


                // locate the parent element
                WebDriverWait wait = new WebDriverWait(driver, duration);
                WebElement parentElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='sbsb_b']")));
                // locate the child elements and sort them based on their y-coordinate
                List<WebElement> childElements = parentElement.findElements(By.xpath("./div"));
                childElements.sort(new Comparator<WebElement>() {
                    @Override
                    public int compare(WebElement e1, WebElement e2) {
                        Integer y1 = e1.getLocation().getY();
                        Integer y2 = e2.getLocation().getY();
                        return y1.compareTo(y2);
                    }
                });

                // click the first child element
                WebElement firstChildElement = childElements.get(0);
                firstChildElement.click();

                WebDriverWait wait2 = new WebDriverWait(driver, duration);
                // Click on the "Reviews" tab to view the restaurant's reviews
                WebElement reviewsBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='RWPxGd']")));
                List<WebElement> reviewBarChild = reviewsBar.findElements(By.xpath("./button"));

                reviewBarChild.sort(new Comparator<WebElement>() {
                    @Override
                    public int compare(WebElement e1, WebElement e2) {
                        Integer y1 = e1.getLocation().getY();
                        Integer y2 = e2.getLocation().getY();
                        return y1.compareTo(y2);
                    }
                });

                WebElement reviewBtn = reviewBarChild.get(1);
                reviewBtn.click();
                Log.d("step1", "review button clicked successfully");
                WebDriverWait wait3 = new WebDriverWait(driver, duration);
                // Print the text of each review
                List<WebElement> reviewsParent = wait3.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.jJc9Ad")));
                Log.d("step2", "review parent div success");

                for(WebElement reviewParent : reviewsParent){
                    WebElement userName = reviewParent.findElement(By.cssSelector("div.d4r55"));
                    WebElement reviewText = reviewParent.findElement(By.cssSelector("span.wiI7pd"));
                    WebElement rate = reviewParent.findElement(By.cssSelector("span.kvMYJc"));


                    String ariaLabel = rate.getAttribute("aria-label");
                    Log.d("arial-label", ariaLabel);
                    String ratingValue = ariaLabel.replaceAll("[^\\d.]", "");

                    Log.d("rate", ratingValue);
                    Log.d("step3", "retrieving the reviews");
                    float rating = Float.parseFloat(ratingValue);
                    Log.d("float rating", String.valueOf(rating));

                    RatingBar ratingBar = new RatingBar(ReviewsActivity.this);
                    ratingBar.setRating(rating);
                    ratingBar.setIsIndicator(false);
                    Log.d("get rating", String.valueOf(ratingBar.getRating()));
                    Review review = new Review(userName.getText(), reviewText.getText(), ratingBar );
                    reviewList.add(review);
                    Log.d("step4", review.getReviewText());

                }
                Log.d("list size", String.valueOf(reviewList.size()));

                runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {

                        reviewAdapter.setData(reviewList);
                        reviewAdapter.notifyDataSetChanged();
                    }
                });

            }finally {
                // Close the driver
                /*driver.quit();*/
            }
        }
    }


}

