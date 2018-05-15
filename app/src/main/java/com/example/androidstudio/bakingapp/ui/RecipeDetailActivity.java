package com.example.androidstudio.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import com.example.androidstudio.bakingapp.widget.IngredientsWidgetProvider;
import com.example.androidstudio.bakingapp.widget.ListRemoteViewsFactory;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;


public class RecipeDetailActivity extends AppCompatActivity
    implements StepsFragment.OnItemClickListener {

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // Final string to store state information
    public static final String STEP_NUMBER = "step";
    public static final String CLICKED_ITEM_INDEX = "clickedItemIndex";
    public static final String RECIPE_NAME = "recipeName";
    public static final String INGREDIENTS_JSON_STRING = "ingredientsJSONString";
    public static final String STEPS_JSON_STRING = "stepsJSONString";
    public static final String SERVINGS = "servings";

    // Final strings to store views visibility state
    public static final String PLAYER_VIEW_VISIBILITY = "player_view_visibility";
    public static final String THUMBNAIL_VIEW_VISIBILITY = "thumbnail_view_visibility";
    public static final String ERROR_VIEW_VISIBILITY = "error_view_visibility";

    private int clickedItemIndex;
    private String recipeName;
    private String ingredientsJSONString;
    private String stepsJSONString;
    private int servings;

    private int mStep;

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
        // This LinearLayout will only initially exists in the two-pane tablet case
        mTwoPane = findViewById(R.id.view_tablet_linear_layout) != null;

        // Recover the views state in case of device rotating
        if (savedInstanceState != null && mTwoPane) {
            mPlayerView.setVisibility(savedInstanceState.getInt(PLAYER_VIEW_VISIBILITY));
            thumbnailView.setVisibility(savedInstanceState.getInt(THUMBNAIL_VIEW_VISIBILITY));
            errorMessageView.setVisibility(savedInstanceState.getInt(ERROR_VIEW_VISIBILITY));
        }

        // Initialize the data vars for this class
        if (null != savedInstanceState) {

            // Reload the number of the step that was being viewed, in case of device rotating
            mStep = savedInstanceState.getInt(STEP_NUMBER);

            clickedItemIndex = savedInstanceState.getInt(CLICKED_ITEM_INDEX);
            recipeName = savedInstanceState.getString(RECIPE_NAME);
            ingredientsJSONString = savedInstanceState.getString(INGREDIENTS_JSON_STRING);
            stepsJSONString = savedInstanceState.getString(STEPS_JSON_STRING);
            servings = savedInstanceState.getInt(SERVINGS);

        } else {

            mStep = 0;

            Intent intentThatStartedThisActivity = getIntent();

            clickedItemIndex = intentThatStartedThisActivity.getIntExtra("clickedItemIndex", -1);
            recipeName = intentThatStartedThisActivity.getStringExtra("recipeName");
            ingredientsJSONString = intentThatStartedThisActivity.getStringExtra("ingredientsJSONString");
            stepsJSONString = intentThatStartedThisActivity.getStringExtra("stepsJSONString");
            servings = intentThatStartedThisActivity.getIntExtra("servings", -1);
        }

        Log.v(TAG, "onCreate recipeName:" + recipeName);
        Log.v(TAG, "onCreate ingredientsJSONString:" + ingredientsJSONString);
        Log.v(TAG, "onCreate stepsJSONString:" + stepsJSONString);


        // Render the views with the data vars
        mDisplayName.setText(recipeName);

        if (savedInstanceState == null) {

            /*
             * Create a new IngredientsFragment
             */
            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            // Set the fragment data
            ingredientsFragment.setIngredients(ingredientsJSONString);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.ingredients_container, ingredientsFragment)
                    .commit();

            /*
             * Create a new StepsFragment
             */
            StepsFragment stepsFragment = new StepsFragment();
            // Set the fragment data
            stepsFragment.setSteps(stepsJSONString);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager stepsFragmentManager = getSupportFragmentManager();
            stepsFragmentManager.beginTransaction()
                    .replace(R.id.steps_container, stepsFragment)
                    .commit();
        }

        Log.v(TAG, "updateStepsView mStep:" + mStep);

        // If two-pane screen, show also the StepDetailFragment with the initial step
        // and the video of the step
        if (mTwoPane && savedInstanceState == null) {
            loadDescriptionAndVideoOrThumbnail(mStep);
        }


        // Update the widget
        ListRemoteViewsFactory.setWidgetProviderData(ingredientsJSONString);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        //Now update all widgets
        IngredientsWidgetProvider.updateIngredientsWidgets(this, appWidgetManager, recipeName, appWidgetIds);

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

        // Initialize the variables that will be used
        String description = null;
        String videoURL = null;
        String thumbnailURL = null;

        try {
            JSONArray stepsJSON = new JSONArray(stepsJSONString);
            JSONObject jsonObject = stepsJSON.getJSONObject(mStep);
            description = jsonObject.getString("description");
            videoURL = jsonObject.getString("videoURL");
            thumbnailURL = jsonObject.getString("thumbnailURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(description != null) {
            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            // Set the fragment data
            stepDetailFragment.setDescription(description);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager stepFragmentManager = getSupportFragmentManager();
            stepFragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }


        // Then, try to load the video or the thumbnail
        if (videoURL != null && (!videoURL.equals(""))) {

            ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
            // Set the fragment data
            exoPlayerFragment.setMediaUrl(videoURL);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager playerFragmentManager = getSupportFragmentManager();
            // Use a FragmentManager and transaction to add the fragment to the screen
            playerFragmentManager.beginTransaction()
                    .add(R.id.player_container, exoPlayerFragment)
                    .commit();
            mPlayerView.setVisibility(View.VISIBLE);

        } else {

            if (thumbnailURL != null && (!thumbnailURL.equals(""))) {
                /*
                 * Use the call back of picasso to manage the error in loading thumbnail.
                 */
                Picasso.with(this)
                        .load(thumbnailURL)
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
    public void onStepSelected(int clickedPosition,
                               int stepId,
                               String shortDescription,
                               String description,
                               String videoURL,
                               String thumbnailURL,
                               String stepsJSONString) {

        Log.v(TAG, "onStepSelected clickedPosition:" + clickedPosition);
        Log.v(TAG, "onStepSelected stepId:" + stepId);

        mStep = clickedPosition;

        Context context = RecipeDetailActivity.this;

        if (!mTwoPane) {
            // If one-pane screen, call the StepDetailActivity to show the step detail
            Class destinationActivity = StepDetailActivity.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);
            startChildActivityIntent.putExtra("clickedPosition", clickedPosition);
            startChildActivityIntent.putExtra("id", stepId);
            startChildActivityIntent.putExtra("description", description);
            startChildActivityIntent.putExtra("videoURL", videoURL);
            startChildActivityIntent.putExtra("thumbnailURL", thumbnailURL);
            startChildActivityIntent.putExtra("stepsJSONString", stepsJSONString);
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

        savedInstanceState.putInt(CLICKED_ITEM_INDEX, clickedItemIndex);
        savedInstanceState.putString(RECIPE_NAME, recipeName);
        savedInstanceState.putString(INGREDIENTS_JSON_STRING, ingredientsJSONString);
        savedInstanceState.putString(STEPS_JSON_STRING, stepsJSONString);
        savedInstanceState.putInt(SERVINGS, servings);

        if(mTwoPane) {
            savedInstanceState.putInt(PLAYER_VIEW_VISIBILITY, mPlayerView.getVisibility());
            savedInstanceState.putInt(THUMBNAIL_VIEW_VISIBILITY, thumbnailView.getVisibility());
            savedInstanceState.putInt(ERROR_VIEW_VISIBILITY, errorMessageView.getVisibility());
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onDestroy() {

        // Clear the widget
        ListRemoteViewsFactory.setWidgetProviderData("");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        //Now update all widgets
        IngredientsWidgetProvider.updateIngredientsWidgets(this, appWidgetManager, "", appWidgetIds);

        super.onDestroy();
    }


}
