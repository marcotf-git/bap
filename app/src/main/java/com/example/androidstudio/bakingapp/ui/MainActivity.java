package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.FileUtils;
import com.example.androidstudio.bakingapp.utilities.RecipesBox;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements  RecipesListAdapter.ListItemClickListener,
                    LoaderManager.LoaderCallbacks<String>{


    private static final String TAG = MainActivity.class.getSimpleName();

    /* This number will uniquely identify our Loader and is chosen arbitrarily. */
    private static final int RECIPES_LOADER_FROM_FILE_ID = 10;

    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private RecipesListAdapter mAdapter;

    @BindView(R.id.rv_recipes) RecyclerView mRecipesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * We are using the GridLayoutManager.
         */
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
        int loaderId = RECIPES_LOADER_FROM_FILE_ID;
        LoaderManager.LoaderCallbacks<String> callback = MainActivity.this;
        Bundle bundleForLoader = null;

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
            RecipesBox recipesBox = new RecipesBox(recipesStringJSON);
            mAdapter.setRecipesData(recipesBox);

        } else {
            showErrorMessage();
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
}
