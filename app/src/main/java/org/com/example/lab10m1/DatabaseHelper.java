package org.com.example.lab10m1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "UniversityDB";

    // Table name
    public static final String TABLE_UNIVERSITY = "universities";

    // University Table Columns
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CITY = "city";
    public static final String KEY_YEAR_FOUNDED = "year_founded";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create universities table with "_id" instead of "id"
        String CREATE_UNIVERSITIES_TABLE = "CREATE TABLE " + TABLE_UNIVERSITY + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT," // Changed "id" to "_id"
                + KEY_NAME + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_YEAR_FOUNDED + " INTEGER" + ")";
        db.execSQL(CREATE_UNIVERSITIES_TABLE);
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNIVERSITY);

        // Create tables again
        onCreate(db);
    }

    // Insert, update, delete methods would be called from MainActivity and are not included here
}
