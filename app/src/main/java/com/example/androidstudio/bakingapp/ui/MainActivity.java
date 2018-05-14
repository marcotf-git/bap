package com.example.androidstudio.bakingapp.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Controller;
import com.example.androidstudio.bakingapp.utilities.DatabaseUtil;
import com.example.androidstudio.bakingapp.data.RecipesContract;
import com.example.androidstudio.bakingapp.data.RecipesDbHelper;
import com.example.androidstudio.bakingapp.utilities.FileUtils;
import com.example.androidstudio.bakingapp.data.RecipesBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements  RecipesListAdapter.ListItemClickListener,
                    LoaderManager.LoaderCallbacks<String>{


    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * This is for simulating a delay in the loader (it will set the view with delay)
     * for illustration of Espresso idling resources test use.
     */
    private static final int DELAY_MILLIS = 500;

    /* This number will uniquely identify our Loader and is chosen arbitrarily. */
    private static final int RECIPES_LOADER_FROM_HTTP_ID = 10;

    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_recipes) RecyclerView mRecipesList;

    private RecipesListAdapter mAdapter;


    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        Controller controller = new Controller();
//        controller.start();
//
//        finish();

        // Set the layout manager
        int nColumns = numberOfColumns();
        GridLayoutManager layoutManager = new GridLayoutManager(this, nColumns);
        mRecipesList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecipesList.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new RecipesListAdapter(this);
        mRecipesList.setAdapter(mAdapter);

        // Loads the recipes in the adapter
        int loaderId = RECIPES_LOADER_FROM_HTTP_ID;
        LoaderManager.LoaderCallbacks<String> callback = MainActivity.this;
        Bundle bundleForLoader = null;

        // Get the IdlingResource instance
        getIdlingResource();

        /*
         * The IdlingResource is null in production as set by the @Nullable annotation which means
         * the value is allowed to be null.
         *
         * If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
         */
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

    }


    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle loaderArgs) {

        Log.v(TAG, "onCreateLoader loader id:" + id);

        final Context context = MainActivity.this;

        return new AsyncTaskLoader<String>(this) {

            /* This String will contain the raw JSON from the results of our search */
            private String mStringJson = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {

                if(mStringJson != null){
                    deliverResult(mStringJson);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load the JSON data
             * from themoviedb.org in the background.
             *
             * @return Movies data from themoviedb.org as an String.
             *         null if an error occurs
             */
            @Override
            public String loadInBackground() {

                String fileName = "baking.json";

                try {
                    String recipesJSONString = FileUtils.getStringFromFile(context, fileName);
                    return recipesJSONString;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            @Override
            public void deliverResult(String data) {
                mStringJson = data;
                super.deliverResult(data);
            }
        };
    }

    // When the load is finished, show either the data or an error message if there is no data.
    @Override
    public void onLoadFinished(Loader<String> loader, String recipesStringJSON) {

        Log.v(TAG, "onLoadFinished loader id:" + loader.getId());
        Log.v(TAG, "onLoadFinished recipesJSONString:" + recipesStringJSON);
        Log.v(TAG, "onLoadFinished recipesJSONString size:" + recipesStringJSON.length());

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (recipesStringJSON != null && !recipesStringJSON.equals("")) {

            showRecipesDataView();
            final RecipesBox recipesBox = new RecipesBox(recipesStringJSON);

            // Save the data in a new local database, for being used by the app widget.
            // The widget needs a persistent data storage.
            deleteDatabase();
            insertDataInDatabase(recipesBox);

            //mAdapter.setRecipesData(recipesBox);

            // This is for simulating a delay in the loader (it will set the view with delay)
            // for Espresso illustration of the idling resources use.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setRecipesData(recipesBox);
                    // Set the idling resource for Espresso (now the app is in idle state)
                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(true);
                    }
                }
            }, DELAY_MILLIS);

        } else {
            showErrorMessage();
            // Set the idling resource for Espresso
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }

    }


    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showRecipesDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mRecipesList.setVisibility(View.VISIBLE);
    }


    /**
     * This method will make the error message visible and hide the JSON View.
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        mRecipesList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    /**
     * This is where we receive our callback from the recipe list adapter
     * {@link com.example.androidstudio.bakingapp.ui.RecipesListAdapter.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex, String recipeStringJSON) {

        Log.v(TAG, "onListItemClick clickedItemIndex:" + clickedItemIndex);

        // Start RecipeDetail activity passing the specific recipe JSON string
        Context context = MainActivity.this;
        Class destinationActivity = RecipeDetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra("recipeStringJSON", recipeStringJSON);
        startActivity(startChildActivityIntent);

    }


    // Helper method for calc the number of columns based on screen
    private int numberOfColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // You can change this divider to adjust the size of the recipe card
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 1) return 1;

        return nColumns;
    }


    private void deleteDatabase() {

        RecipesDbHelper dbHelper = new RecipesDbHelper(this);
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        DatabaseUtil.deleteDatabase(mDb);
        mDb.close();
    }


    private void insertDataInDatabase(RecipesBox recipesBox) {

        // Get the content resolver
        ContentResolver resolver = getContentResolver();

        // This is necessary to pass the values
        ContentValues cv = new ContentValues();

        int numberOfRecipes = recipesBox.getNumberOfRecipes();

        for (int i=0; i < numberOfRecipes; i++) {

            JSONObject recipeJSON = recipesBox.getRecipeJSON(i);

            final int recipeId;
            final String name;
            final String ingredientsJSON;
            final String stepsJSON;
            final int servings;
            final String imageURL;

            try {

                recipeId = recipeJSON.getInt("id");
                name = recipeJSON.getString("name");
                ingredientsJSON = recipeJSON.getString("ingredients");
                stepsJSON = recipeJSON.getString("steps");
                servings = recipeJSON.getInt("servings");
                imageURL = recipeJSON.getString("image");

                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_ID, recipeId);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_NAME, name);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_INGREDIENTS_JSON, ingredientsJSON);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_STEPS_JSON, stepsJSON);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_SERVINGS, servings);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_IMAGE, imageURL);

                // Call the insert method on the resolver with the correct Uri from the contract class
                resolver.insert(RecipesContract.RecipeslistEntry.CONTENT_URI, cv);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
