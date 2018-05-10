package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Ingredient;
import com.example.androidstudio.bakingapp.utilities.Step;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;


public class RecipeDetailActivity extends AppCompatActivity
    implements StepsFragment.OnItemClickListener {

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // Final string to store state information
    public static final String STEP_NUMBER = "step";

    // Final strings to store views visibility state
    public static final String PLAYER_VIEW_VISIBILIBITY = "player_view_visibility";
    public static final String THUMBNAIL_VIEW_VISIBILIBITY = "thumbnail_view_visibility";
    public static final String ERROR_VIEW_VISIBILIBITY = "error_view_visibility";

    // The data vars of the recipe being viewed
    private String recipeStringJSON;
    private String recipeName = "";

    // The step being viewed
    private int mStep;
    private JSONArray ingredientsJSON;
    private JSONArray stepsJSON;


    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();

    // The array for storing information about the steps
    private final ArrayList<Step> steps = new ArrayList<>();

    // A single-pane display refers to phone screens, and two-pane to tablet screens
    private boolean mTwoPane;

    // The views variables
    @BindView(R.id.tv_recipe_name) TextView mDisplayName;
    @Nullable @BindView(R.id.player_container) View mPlayerView;
    @Nullable @BindView(R.id.iv_thumbnail) ImageView thumbnailView;
    @Nullable @BindView(R.id.tv_illustration_not_available_label) TextView errorMessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);

        // Determine if you are creating a two-pane or single-pane display
        if(findViewById(R.id.view_tablet_linear_layout) != null) {
            // This LinearLayout will only initially exists in the two-pane tablet case
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        // Reload the number of the step that was being viewed, in case of device rotating
        if (savedInstanceState == null) {
            mStep = 0;
        } else {
            mStep = savedInstanceState.getInt(STEP_NUMBER);
        }


        // Recover the views state in case of device rotating
        if (savedInstanceState != null && mTwoPane) {
            mPlayerView.setVisibility(savedInstanceState.getInt(PLAYER_VIEW_VISIBILIBITY));
            thumbnailView.setVisibility(savedInstanceState.getInt(THUMBNAIL_VIEW_VISIBILIBITY));
            errorMessageView.setVisibility(savedInstanceState.getInt(ERROR_VIEW_VISIBILIBITY));
        }

        // Initialize the data vars for this class
        // This loads the recipes JSON string, with all the data about the recipes
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("recipeStringJSON")) {
            recipeStringJSON = intentThatStartedThisActivity.getStringExtra("recipeStringJSON");
        }

        // Render the views with the data vars
        updateView(recipeStringJSON, savedInstanceState);

    }


    // This will render the views with the data vars
    public void updateView(String recipeStringJSON, Bundle savedInstanceState){

        // Prepare the data and render the views
        try {

            // Convert the string to the JSON object
            JSONObject recipeJSON = new JSONObject(recipeStringJSON);

            // Extract the recipe name
            recipeName = recipeJSON.getString("name");
            // Extract the ingredients
            ingredientsJSON = recipeJSON.getJSONArray("ingredients");
            // Extract the steps
            stepsJSON = recipeJSON.getJSONArray("steps");

            // Render the views
            mDisplayName.setText(recipeName);
            updateIngredientsView(ingredientsJSON);
            updateStepsView(stepsJSON, savedInstanceState);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This helper function will load the IngredientsFragment, to show the ingredients in a view list.
     */
    public void updateIngredientsView (JSONArray ingredientsJSON) {

        Log.v(TAG, "updateIngredientsView ingredientsJSON:" + ingredientsJSON.toString());

        int nIngredients = ingredientsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nIngredients; i++) {

            int ingredientQuantity;
            String ingredientMeasure;
            String ingredientName;
            JSONObject jsonObject;

            try {
                jsonObject = ingredientsJSON.getJSONObject(i);
                ingredientQuantity = jsonObject.getInt("quantity");
                ingredientMeasure = jsonObject.getString("measure");
                ingredientName = jsonObject.getString("ingredient");
                Ingredient ingredient = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);
                ingredients.add(ingredient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // At this point, we have an Array with the ingredients information
        Log.v(TAG, "updateIngredientsView ingredients:" + ingredients.toString());

        // Create a new IngredientsFragment instance and display it using the FragmentManager
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        // Set the fragment data
        ingredientsFragment.setIngredients(ingredients);
        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ingredients_container, ingredientsFragment)
                .commit();
    }


    /**
     * This helper function will load the StepsFragment, to show the steps in a view list.
     */
    public void updateStepsView (JSONArray stepsJSON, Bundle savedInstanceState) {

        Log.v(TAG, "updateStepsView stepsJSON:" + stepsJSON.toString());

        int nSteps = stepsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nSteps; i++) {

            int id;
            String shortDescription;
            String description;
            String videoURL;
            String thumbnailURL;
            JSONObject jsonObject;

            try {
                jsonObject = stepsJSON.getJSONObject(i);
                id = jsonObject.getInt("id");
                shortDescription = jsonObject.getString("shortDescription");
                description = jsonObject.getString("description");
                videoURL = jsonObject.getString("videoURL");
                thumbnailURL = jsonObject.getString("thumbnailURL");
                Step step = new Step(id, shortDescription, description, videoURL, thumbnailURL);
                steps.add(step);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // At this point, we have an Array with the steps information
        Log.v(TAG, "updateStepsView steps:" + steps.toString());

        // Create a new StepsFragment instance and display it using the FragmentManager
        StepsFragment stepsFragment = new StepsFragment();
        // Set the fragment data
        stepsFragment.setSteps(steps);
        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager stepsFragmentManager = getSupportFragmentManager();
        stepsFragmentManager.beginTransaction()
                .replace(R.id.steps_container, stepsFragment)
                .commit();

        Log.v(TAG, "updateStepsView mStep:" + mStep);

        // If two-pane screen, show also the StepDetailFragment with the initial step
        // and the video of the step
        if (mTwoPane && savedInstanceState == null) {
            loadDescriptionAndVideoOrThumbnail(mStep);
        }
    }


    /**
     * Helper method for loading the step description, and also its video or thumbnail
     *
     */
    private void loadDescriptionAndVideoOrThumbnail (int stepNumber) {

        // Set initial state of the player and thumbnail views (this method is only called in two pane)
        errorMessageView.setVisibility(View.GONE);
        mPlayerView.setVisibility(View.GONE);
        thumbnailView.setVisibility(View.GONE);

        // Remove previously loaded fragments
        FragmentManager myFragmentManager = getSupportFragmentManager();
        Fragment fragment = myFragmentManager.findFragmentById(R.id.step_detail_container);
        if (null != fragment) {
            myFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = myFragmentManager.findFragmentById(R.id.player_container);
        if (null != fragment) {
            myFragmentManager.beginTransaction().remove(fragment).commit();
        }

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        // Set the fragment data
        stepDetailFragment.setDescription(steps.get(stepNumber).getDescription());
        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager stepFragmentManager = getSupportFragmentManager();
        stepFragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();

        // Then, try to load a new one
        String mStepVideoURL = steps.get(stepNumber).getVideoURL();

        if (mStepVideoURL != null && (!mStepVideoURL.equals(""))) {

            PlayerFragment playerFragment = new PlayerFragment();
            // Set the fragment data
            playerFragment.setMediaUrl(mStepVideoURL);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager playerFragmentManager = getSupportFragmentManager();
            // Use a FragmentManager and transaction to add the fragment to the screen
            playerFragmentManager.beginTransaction()
                    .add(R.id.player_container, playerFragment)
                    .commit();
            mPlayerView.setVisibility(View.VISIBLE);

        } else {

            // In case of no video, try to show the thumbnail
            String mStepThumbnailURL = steps.get(stepNumber).getThumbnailURL();

            if (mStepThumbnailURL != null && (!mStepThumbnailURL.equals(""))) {
                /*
                 * Use the call back of picasso to manage the error in loading thumbnail.
                 */
                Picasso.with(this)
                        .load(mStepThumbnailURL)
                        .into(thumbnailView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.v(TAG, "Thumbnail loaded");
                                thumbnailView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.e(TAG, "Error in loading thumbnail");
                                if (mPlayerView.getVisibility() == GONE) {
                                    errorMessageView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }  else {
                errorMessageView.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * This is the listener that receives communication from the StepsFragment
     */
    @Override
    public void onStepSelected(int position) {

        Log.v(TAG, "onStepSelected:" + position);

        mStep = position;
        Context context = RecipeDetailActivity.this;

        if (!mTwoPane) {
            // If one-pane screen, call the StepDetailActivity to show the step detail
            Class destinationActivity = StepDetailActivity.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);
            startChildActivityIntent.putExtra("mStep", mStep);
            startChildActivityIntent.putExtra("stepsJSONtoString", stepsJSON.toString());
            startActivity(startChildActivityIntent);
        } else {
            // If two-pane screen, show also the StepDetailFragment with the initial step
            // and the video of the step
            loadDescriptionAndVideoOrThumbnail(mStep);
        }
    }


    // This method is saving the step being viewed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STEP_NUMBER, mStep);
        if(mTwoPane) {
            savedInstanceState.putInt(PLAYER_VIEW_VISIBILIBITY, mPlayerView.getVisibility());
            savedInstanceState.putInt(THUMBNAIL_VIEW_VISIBILIBITY, thumbnailView.getVisibility());
            savedInstanceState.putInt(ERROR_VIEW_VISIBILIBITY, errorMessageView.getVisibility());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
