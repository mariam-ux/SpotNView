package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ReviewsActivity extends BaseActivity {
    private String targetText;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_reviews;
    }

    private List<Review> reviewList = new ArrayList<>();
    private  ReviewAdapter reviewAdapter;
    private RecyclerView reviewsRecyclerView;
    private String userAddress;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private String detectedText;
    Duration duration = Duration.ofSeconds(60);
    private String searchedText;
    private TextView avgRate;
    private Boolean shouldStartWebDriver;
    private ReviewDaoImp reviewDao;
    private Timer reviewClearTimer;
    private TextView reviewNote;
    private Button addBtn;
    private List<String> detailsList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        reviewNote = findViewById(R.id.reviewNote);
        addBtn = findViewById(R.id.addBtn);
        addBtn.setVisibility(View.VISIBLE);


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

        avgRate = findViewById(R.id.AvgRate);

        Intent intent = getIntent();
        detectedText = intent.getStringExtra("detectedText");

        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", ScanActivity.MODE_PRIVATE);
        shouldStartWebDriver = sharedPrefs.getBoolean("shouldStartWebDriver", true);
        SharedPreferences sharedPrefs2 = getSharedPreferences("MyPrefs", BaseActivity.MODE_PRIVATE);
        shouldStartWebDriver = sharedPrefs2.getBoolean("shouldStartWebDriver", false);
        reviewDao = new ReviewDaoImp(this);

        List<Review> review_db = reviewDao.getAllReviews();

        if (shouldStartWebDriver) {
            reviewNote.setVisibility(View.GONE);
            reviewsRecyclerView.setVisibility(View.VISIBLE);
            avgRate.setVisibility(View.VISIBLE);
            reviewDao.deleteAllReviews();
            performLocationOperation(ReviewsActivity.this);
        } else {
            if (!review_db.isEmpty()) {
                Log.d("reviewCache", "review cache is not null");
                reviewNote.setVisibility(View.GONE);
                avgRate.setVisibility(View.GONE);
                reviewList.addAll(review_db);
                reviewAdapter.setData(reviewList);
                reviewAdapter.notifyDataSetChanged();
            } else {
                reviewNote.setVisibility(View.VISIBLE);
                reviewsRecyclerView.setVisibility(View.GONE);
                avgRate.setVisibility(View.GONE);
            }
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            @SuppressLint("SimpleDateFormat")
            final String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            @Override
            public void onClick(View view) {
                Log.d("addBbtn", "clicked");
                if (currentUser != null) {
                    Log.d("user","not null");
                    final DatabaseReference userRef = database.getReference("users/" + currentUser.getUid());

                    userRef.child("history").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean historyExists = false;

                            for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                                String snapshotHistory = historySnapshot.child("hsitory").getValue(String.class); // Fixed typo
                                if (snapshotHistory != null && snapshotHistory.equals("history")) {
                                    historyExists = true;
                                    break;
                                }
                            }

                            if (historyExists) {
                                // Check if the review already exists
                                boolean reviewExists = false;
                                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                    String reviewTitle = reviewSnapshot.child("reviewTitle").getValue(String.class);
                                    if (reviewTitle != null && reviewTitle.equals(detectedText)) {
                                        reviewExists = true;
                                        break;
                                    }
                                }

                                if (reviewExists) {
                                    Toast.makeText(ReviewsActivity.this, "This review is already in your history.", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference newReviewRef = userRef.child("history").push();
                                    newReviewRef.child("reviewTitle").setValue(detectedText);
                                    newReviewRef.child("userAddress").setValue(userAddress);
                                    newReviewRef.child("date").setValue(timestamp);
                                    newReviewRef.child("reviews").setValue(detailsList);
                                    Toast.makeText(ReviewsActivity.this, "add to the history successfully", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Toast.makeText(ReviewsActivity.this, "No history on firebase", Toast.LENGTH_SHORT).show();
                                DatabaseReference newReviewRef = userRef.child("history").push();
                                newReviewRef.child("reviewTitle").setValue(detectedText);
                                newReviewRef.child("userAddress").setValue(userAddress);
                                newReviewRef.child("date").setValue(timestamp);
                                newReviewRef.child("reviews").setValue(detailsList);
                                Toast.makeText(ReviewsActivity.this, "add to the history successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("firebase database error", databaseError.toString());
                        }
                    });


                } else {
                    Log.d("user","not null");
                    Toast.makeText(ReviewsActivity.this, "null user reference, you have to sign-in", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                 retrieveUserAddress(ReviewsActivity.this, new AddressCallback(){
                     @Override
                     public void onAddressReceived(String address) {
                         Log.d("intialize address3", address);
                         Thread scrapingThread = new Thread(new RetrieveReviewsRunnable());
                         scrapingThread.start();

                     }
                 });

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveUserAddress(Context context, AddressCallback callback) {
        // Create an instance of LocationManager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if location services are enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            // Create a location listener
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Location update received, retrieve the address
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            userAddress = address.getAddressLine(0);
                            Log.d("intialize address", userAddress);

                            if (callback != null) {
                                callback.onAddressReceived(userAddress);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("intialize address2", userAddress);
                    // Remove the location listener as we only need one location update
                    locationManager.removeUpdates(this);

                }


            };


            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

            }

        }


    }
    public interface AddressCallback {
        void onAddressReceived(String address);
    }
    private class RetrieveReviewsRunnable implements Runnable {

        @Override
        public void run() {

            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--lang=en");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("chrome");
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            URL seleniumGridUrl = null;
            try {
                seleniumGridUrl = new URL("http://192.168.0.106:4444/wd/hub");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            WebDriver driver = new RemoteWebDriver(seleniumGridUrl, capabilities);

            try {

                // Navigate to Google Maps
                driver.get("https://maps.google.com/?hl=en");

                // Search for the restaurant
                WebElement searchBox = driver.findElement(By.name("q"));
                WebDriverWait wait = new WebDriverWait(driver, duration);
                searchedText = detectedText /*+ " " + userAddress*/;
                Log.d("userAddress", searchedText);
                searchBox.sendKeys(searchedText);
                searchBox.submit();


                // locate the parent element
                WebElement parentElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"ydp1wd-haAclf\"]")));
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


                // Click on the "Reviews" tab to view the restaurant's reviews
                WebElement reviewsBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[2]/div/div[1]/div/div/div[3]/div/div")));
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
                //get the average reviews rate
                WebElement avgRateElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[2]/div/div[1]/div/div/div[3]/div[2]/div/div[2]/div[1]")));
                avgRate.setText(avgRateElement.getText());
                //display the newest reviews on the web driver
                WebElement sortBTN = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"QA0Szd\"]/div/div/div[1]/div[2]/div/div[1]/div/div/div[3]/div[7]/div[2]/button/span")));
                sortBTN.click();

                WebElement sortContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#action-menu")));
                List<WebElement> sortChild = sortContainer.findElements(By.cssSelector("div.fxNQSd"));
                sortChild.sort(new Comparator<WebElement>() {
                    @Override
                    public int compare(WebElement e1, WebElement e2) {
                        Integer y1 = e1.getLocation().getY();
                        Integer y2 = e2.getLocation().getY();
                        return y1.compareTo(y2);
                    }
                });

                WebElement sortByNewest = sortChild.get(1);
                sortByNewest.click();
                List<WebElement> moreBTNs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("button.w8nwRe.kyuRq")));
                for (WebElement moreBTN : moreBTNs) {
                    moreBTN.click();
                }
                // Print the text of each review
                List<WebElement> reviewsParent = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.jJc9Ad")));
                Log.d("step2", "review parent div success");


                for(WebElement review : reviewsParent){
                    String reviewTextRetrieve = "";
                    WebElement userName = review.findElement(By.cssSelector("div.d4r55"));
                    WebElement rate = review.findElement(By.cssSelector("span.kvMYJc"));
                    WebElement reviewdate = review.findElement(By.cssSelector("span.rsqaWe"));
                    List<WebElement> reviewTextElements = review.findElements(By.cssSelector("span.wiI7pd"));
                    WebElement reviewText = null;

                    if (!reviewTextElements.isEmpty()) {
                        reviewText = reviewTextElements.get(0);
                        reviewTextRetrieve = reviewText.getText();
                        detailsList.add(reviewTextRetrieve);
                    }

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



                    Review reviewItem = new Review(userName.getText(), reviewTextRetrieve, ratingBar, reviewdate.getText() );
                    reviewList.add(reviewItem);
                    if (reviewClearTimer != null) {
                        reviewClearTimer.cancel();
                    }
                    reviewDao.addReview(reviewItem);
                    reviewClearTimer = new Timer();
                    reviewClearTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            reviewDao.deleteAllReviews();
                            Log.d("reviewCache", "Reviews cleared after 2 hours");
                        }
                    }, 2 * 60 * 60 * 1000); // 2 hours in milliseconds
                    Log.d("step4", reviewItem.getReviewText());
                    Log.d("add review to db", reviewItem.getReviewText());
                    //save the reviews to the cache



                    SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", ScanActivity.MODE_PRIVATE);
                    shouldStartWebDriver = false;
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("shouldStartWebDriver", false);
                    editor.apply();
                    Log.d("shouldStartWebDriver", Boolean.toString(shouldStartWebDriver));

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
              /* driver.quit();*/
            }
        }
    }


}

