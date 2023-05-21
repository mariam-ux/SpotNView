package com.example.spotnview;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;


public class ReviewDaoImp implements ReviewDao {

    private ReviewDatabase dbHelper;

    public ReviewDaoImp(Context context) {
        dbHelper = new ReviewDatabase(context);
    }

    @Override
    public void addReview(Review review) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReviewDatabase.KEY_NAME, review.getUserName());
        values.put(ReviewDatabase.KEY_TEXT, review.getReviewText());
        values.put(ReviewDatabase.KEY_RATING, review.getRatingBar().getRating());
        values.put(ReviewDatabase.KEY_DATE, review.getReviewDate());

        db.insert(ReviewDatabase.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ReviewDatabase.TABLE_NAME, null);
        Context context = dbHelper.context;
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(ReviewDatabase.KEY_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ReviewDatabase.KEY_NAME));
                @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex(ReviewDatabase.KEY_TEXT));
                @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex(ReviewDatabase.KEY_RATING));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(ReviewDatabase.KEY_DATE));

                RatingBar ratingBar = new RatingBar(context);
                ratingBar.setRating(rating);
                Review review = new Review(name, comment, ratingBar, date);
                reviewList.add(review);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return reviewList;
    }





}
