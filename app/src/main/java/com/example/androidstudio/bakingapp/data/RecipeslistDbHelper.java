package com.example.androidstudio.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidstudio.bakingapp.data.RecipeslistContract.*;

public class RecipeslistDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "recipeslist.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public RecipeslistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the recipe data
        final String SQL_CREATE_RECIPESLIST_TABLE = "CREATE TABLE " +
                RecipeslistEntry.TABLE_NAME + " (" +
                RecipeslistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeslistEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL," +
                RecipeslistEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL," +
                RecipeslistEntry.COLUMN_RECIPE_INGREDIENTS_JSON + " TEXT," +
                RecipeslistEntry.COLUMN_RECIPE_STEPS_JSON + " TEXT," +
                RecipeslistEntry.COLUMN_RECIPE_SERVINGS + " INTEGER," +
                RecipeslistEntry.COLUMN_RECIPE_IMAGE + " BLOB," +
                /*
                 * To ensure this table can only contain one recipe id, we declare
                 * the id column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a recipe id entry, we replace the old.
                 */
                " UNIQUE (" + RecipeslistEntry.COLUMN_RECIPE_ID + ") ON CONFLICT REPLACE);";

        // Create the database
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPESLIST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeslistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
