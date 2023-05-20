package com.example.spotnview;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class CacheManager {
    private static final long CACHE_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours
    private static final String CACHE_FILE_NAME = "reviews_cache";

    // Save the list of reviews to the cache
    public static void saveReviews(Context context, List<Review> reviews) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(reviews);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve the list of reviews from the cache
    public static List<Review> getReviews(Context context) {
        List<Review> reviews = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(CACHE_FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            reviews = (List<Review>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Check if the cache is expired
    public static boolean isCacheExpired(Context context) {
        File cacheFile = context.getFileStreamPath(CACHE_FILE_NAME);
        if (cacheFile != null && cacheFile.exists()) {
            long currentTime = System.currentTimeMillis();
            long lastModified = cacheFile.lastModified();
            return currentTime - lastModified > CACHE_INTERVAL;
        }
        return true; // Cache doesn't exist or expired if it exists
    }
}

