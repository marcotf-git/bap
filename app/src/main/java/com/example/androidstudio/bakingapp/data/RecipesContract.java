package com.example.androidstudio.bakingapp.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Thia class will store the database contract schema names
 */

public final class  RecipesContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.androidstudio.bakingapp";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "recipes" directory
    public static final String PATH_RECIPES = "recipes";

    public static final int INVALID_RECIPE_ID = -1;

    // Empty constructor
    private RecipesContract() {}

    public static class RecipeslistEntry implements BaseColumns {

        // Recipes entry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        // Recipes table and column names
        public static final String TABLE_NAME = "recipes";

        // Since RecipeslistEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the "id" below
        public static final String COLUMN_RECIPE_ID = "id";
        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_RECIPE_INGREDIENTS_JSON = "ingredients";
        public static final String COLUMN_RECIPE_STEPS_JSON = "steps";
        public static final String COLUMN_RECIPE_SERVINGS = "servings";
        public static final String COLUMN_RECIPE_IMAGE = "image";

    }

}
