package com.example.androidstudio.bakingapp.utilities;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.androidstudio.bakingapp.data.RecipesContract;

public class DatabaseUtil {

    public static void deleteDatabase(SQLiteDatabase db){

        if(db == null){
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete (RecipesContract.RecipeslistEntry.TABLE_NAME,null,null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }

    }
}