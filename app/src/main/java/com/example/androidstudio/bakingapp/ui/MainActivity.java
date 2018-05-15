package com.example.androidstudio.bakingapp.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidstudio.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.data.Recipe;
import com.example.androidstudio.bakingapp.utilities.Controller;
import com.example.androidstudio.bakingapp.utilities.DatabaseUtil;
import com.example.androidstudio.bakingapp.data.RecipesContract;
import com.example.androidstudio.bakingapp.data.RecipesDbHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        RecipesListAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        Controller.OnDataLoadedListener{


    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * This is for simulating a delay in the loader (it will set the view with delay)
     * for illustration of Espresso idling resources test use.
     */
    private static final int DELAY_MILLIS = 500;


    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_recipes) RecyclerView mRecipesList;

    private RecipesListAdapter mAdapter;

    private Cursor mData;

    // Fields for handling the saving and restoring of view state
    private static final String RECYCLER_VIEW_STATE = "recyclerViewState";
    private Parcelable recyclerViewState;

    private static final int ID_RECIPES_LOADER = 44;


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

        // Load data from http with the Retrofit library
        Controller controller = new Controller();
        controller.start(this);

    }


    // This method is saving the position of the recycler view
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Parcelable recyclerViewState = mRecipesList.getLayoutManager().onSaveInstanceState();
        savedInstanceState.putParcelable(RECYCLER_VIEW_STATE, recyclerViewState);
        super.onSaveInstanceState(savedInstanceState);
    }

    // This method is loading the saved position of the recycler view
    // There is also a call on the post execute method in the loader, for updating the view
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        mRecipesList.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Reloading the data", Toast.LENGTH_LONG)
                        .show();
                refreshActivity();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
    public void onListItemClick(int clickedItemIndex,
                                String recipeName,
                                String ingredientsJSONString,
                                String stepsJSONString,
                                int servings) {

        Log.v(TAG, "onListItemClick clickedItemIndex:" + clickedItemIndex);

        // Start RecipeDetail activity passing the specific recipe JSON string
        Context context = MainActivity.this;
        Class destinationActivity = RecipeDetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);

        startChildActivityIntent.putExtra("clickedItemIndex", clickedItemIndex);
        startChildActivityIntent.putExtra("recipeName", recipeName);
        startChildActivityIntent.putExtra("ingredientsJSONString", ingredientsJSONString);
        startChildActivityIntent.putExtra("stepsJSONString", stepsJSONString);
        startChildActivityIntent.putExtra("servings", servings);

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


    private void insertRecipesInDatabase(List<Recipe> recipes) {

        // Get the content resolver
        ContentResolver resolver = getContentResolver();

        // This is necessary to pass the values
        ContentValues cv = new ContentValues();

        int numberOfRecipes = recipes.size();

        for (int i=0; i < numberOfRecipes; i++) {

            Recipe recipe = recipes.get(i);

            int recipeId = recipe.getId();
            String name = recipe.getName();
            String ingredientsJSON = recipe.getIngredientsJSONString();
            String stepsJSON = recipe.getStepsJSONString();
            int servings = recipe.getServings();
            String imageURL = recipe.getImage();

            try {

                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_ID, recipeId);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_NAME, name);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_INGREDIENTS_JSON, ingredientsJSON);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_STEPS_JSON, stepsJSON);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_SERVINGS, servings);
                cv.put(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_IMAGE, imageURL);

                // Call the insert method on the resolver with the correct Uri from the contract class
                resolver.insert(RecipesContract.RecipeslistEntry.CONTENT_URI, cv);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    // This method is called by the Controller when Retrofit finishes the loading
    @Override
    public void onDataLoaded(List<Recipe> recipes) {

        // Try to handle error on loading
        if(recipes == null){
            showErrorMessage();
            return;
        }

        for(Recipe recipe: recipes) {
            Log.v(TAG, "onDataLoaded recipe names:" + recipe.getName());
        }

        // Store the data on the database
        deleteDatabase();
        insertRecipesInDatabase(recipes);

        // Query the database and set the adapter with the cursor data
        getSupportLoaderManager().initLoader(ID_RECIPES_LOADER, null, this);

    }


    /**
     * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader needs to be
     * created. This Activity only uses one loader, so we don't necessarily NEED to check the
     * loaderId, but this is certainly best practice.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        Log.v(TAG, "onCreateLoader");

        switch (loaderId) {

            case ID_RECIPES_LOADER:
                /* URI for all rows of recipes data in our recipes table */
                Uri recipesQueryUri = RecipesContract.RecipeslistEntry.CONTENT_URI;

                return new CursorLoader(this,
                        recipesQueryUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mData = data;
        // Set the data for the adapter
        mAdapter.setRecipesCursorData(data);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        showRecipesDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mAdapter.setRecipesCursorData(null);
    }


    // Reload the activity
    public void refreshActivity() {
        finish();
        startActivity(getIntent());
    }


}
