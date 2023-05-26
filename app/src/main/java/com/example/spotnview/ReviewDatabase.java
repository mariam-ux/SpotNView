package com.example.spotnview;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.RatingBar;

import java.util.ArrayList;
import java.util.List;

public class ReviewDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "review_database";
    public static final String TABLE_NAME = "reviews";
    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "name";
    public static final String KEY_TEXT = "comment";

    public static final String KEY_RATING = "rating";
    public static final String KEY_DATE = "date";
    public Context context;
    public ReviewDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_TEXT + " TEXT,"
                + KEY_RATING + " REAL,"
                + KEY_DATE + " TEXT"
                +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addReview(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, review.getUserName());
        values.put(KEY_TEXT, review.getReviewText());
        values.put(KEY_RATING, review.getRatingBar().getRating());
        values.put(KEY_DATE, review.getReviewDate());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Review> getAllReviews(Context context) {
        List<Review> reviewList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String userName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                @SuppressLint("Range") String reviewText = cursor.getString(cursor.getColumnIndex(KEY_TEXT));
                @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex(KEY_RATING));
                @SuppressLint("Range") String reviewDate = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                RatingBar ratingBar = new RatingBar(context); // Provide the appropriate context
                ratingBar.setRating(rating);
                Review review = new Review(userName, reviewText, ratingBar, reviewDate);
                reviewList.add(review);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reviewList;
    }

    public void deleteReview(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{String.valueOf(review.getUserName())});
        db.close();
    }
}
