package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * This activity has the function of starting the step detail fragment.
 * It will show the description of the step, and the video or thumbnail.
 */
public class StepDetailActivity extends AppCompatActivity {

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    private View mPlayerView;
    private ImageView thumbnailView;
    private View illustrationView;
    private TextView errorMessageView;

    private String stepsJSONtoString;
    private JSONArray stepsJSON;

    private int mStep;
    private JSONObject jsonObject;
    private String stepDescription;
    private String videoURL;
    private String thumbnailURL;

    private boolean isLandscape;

    // Fields for handling the saving and restoring of view state
    private static final String ERROR_MESSAGE_VIEW_STATE = "errorMessageViewState";
    private Parcelable errorMessageViewState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        // Set the views variables
        if(null != findViewById(R.id.view_activity_step_detail_landscape)) {
            isLandscape = true;
        } else {
            isLandscape = false;
        }

        thumbnailView = findViewById(R.id.iv_thumbnail);
        mPlayerView = findViewById(R.id.player_container);
        illustrationView = findViewById(R.id.illustrationView);
        errorMessageView = findViewById(R.id.tv_illustration_not_available_label);

        // Get the variables to initialize this activity
        Intent intentThatStartedThisActivity = getIntent();

        if (null != savedInstanceState) {
            mStep = savedInstanceState.getInt("mStep");
        } else {
            if (intentThatStartedThisActivity.hasExtra("mStep")) {
                mStep = intentThatStartedThisActivity.getIntExtra("mStep", 0);
            }
        }

        if (intentThatStartedThisActivity.hasExtra("stepsJSONtoString")) {
            stepsJSONtoString = intentThatStartedThisActivity.getStringExtra("stepsJSONtoString");
        }

        Log.v(TAG, "onCreate mStep:" + mStep);

        // Extract all the steps
        try {
            stepsJSON = new JSONArray(stepsJSONtoString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Load the variables for the current step mStep
        loadStepVars();

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        // only create new fragment when there is no previously saved state
        if (savedInstanceState == null) {
            loadViews();
        }

        // Set the visibility of the views for error handling
        adjustVisibility();

    }


    private void loadViews() {

        Log.v(TAG, "loadViews videoURL:" + videoURL);

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        StepDetailFragment stepDetailFragment = new StepDetailFragment();

        // Set the fragment data
        if (stepDescription != null) {
            stepDetailFragment.setDescription(stepDescription);
        } else {
            stepDetailFragment.setDescription("No step description available.");
        }

        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();

        // Create a new PlayerFragment instance and display it using FragmentManager
        // or try to load and show the Thumbnail
        if (!videoURL.equals("")) {

            PlayerFragment playerFragment = new PlayerFragment();
            // Set the fragment data
            playerFragment.setMediaUrl(videoURL);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager playerFragmentManager = getSupportFragmentManager();
            // Use a FragmentManager and transaction to add the fragment to the screen
            playerFragmentManager.beginTransaction()
                    .replace(R.id.player_container, playerFragment)
                    .commit();

        } else {

            // Try to load the Thumbnail
            if (!thumbnailURL.equals("")) {

                Log.v(TAG, "thumbnailURL:" + thumbnailURL);
                /*
                 * Use the call back of picasso to manage the error in loading thumbnail.
                 */
                Picasso.with(this)
                        .load(thumbnailURL)
                        .into(thumbnailView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.v(TAG, "Thumbnail loaded");
                            }
                            @Override
                            public void onError() {
                                Log.e(TAG, "Error in loading thumbnail");
                                thumbnailView.setVisibility(View.GONE);
                                if(mPlayerView.getVisibility() == View.GONE) {
                                    errorMessageView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }

        }
    }


    private void loadStepVars() {

        try {
            jsonObject = stepsJSON.getJSONObject(mStep);
            stepDescription = jsonObject.getString("description");
            videoURL = jsonObject.getString("videoURL");
            thumbnailURL = jsonObject.getString("thumbnailURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Adjust the visibility of the views for handling errors
    private void adjustVisibility() {

        // Initial values
        errorMessageView.setVisibility(View.GONE);
        mPlayerView.setVisibility(View.GONE);
        thumbnailView.setVisibility(View.GONE);
        illustrationView.setVisibility(View.GONE);

        Log.v(TAG, "adjustVisibility videoURL:" + videoURL);

        // Adjust according to state
        if (videoURL.equals("")) {

            if (thumbnailURL.equals("")) {

                errorMessageView.setVisibility(View.VISIBLE);
                illustrationView.setVisibility(View.VISIBLE);

                Log.v(TAG, "adjustVisibility state 3");

            } else {

                thumbnailView.setVisibility(View.VISIBLE);
                illustrationView.setVisibility(View.VISIBLE);

                Log.v(TAG, "adjustVisibility state 2");
            }

        } else {

            mPlayerView.setVisibility(View.VISIBLE);
            illustrationView.setVisibility(View.VISIBLE);

            Log.v(TAG, "adjustVisibility state 1");

        }
    }


    // Called by button Prev
    public void loadPrevStep(View view) {

        mStep--;

        if (mStep < 0) {
            mStep = 0;
        }

        loadStepVars();
        loadViews();
        adjustVisibility();
    }


    // Called by button Next
    public void loadNextStep(View view) {

        mStep++;

        if (mStep >= stepsJSON.length()) {
            mStep = stepsJSON.length() - 1;
        }

        loadStepVars();
        loadViews();
        adjustVisibility();
    }


    // This is for saving the step that is being viewed when the device is rotated
    @Override
    public void onSaveInstanceState(Bundle outState) {

        Parcelable errorMessageViewState = errorMessageView.onSaveInstanceState();
        outState.putParcelable(ERROR_MESSAGE_VIEW_STATE, errorMessageViewState);

        outState.putInt("mStep", mStep);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mStep = savedInstanceState.getInt("mStep");



        Log.v(TAG, "onRestoreInstanceState mStep:" + mStep);
    }

}
