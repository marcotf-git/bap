package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * This activity has the function of starting the step detail fragment.
 * It will show the description of the step, and the video or thumbnail.
 */
public class StepDetailActivity extends AppCompatActivity {

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    // Final strings to store views visibility state
    private static final String PLAYER_VIEW_VISIBILITY = "player_view_visibility";
    private static final String THUMBNAIL_VIEW_VISIBILITY = "thumbnail_view_visibility";
    private static final String ERROR_VIEW_VISIBILITY = "error_view_visibility";

    // The data vars of the recipe being viewed
    private String stepsJSONString;
    private JSONArray stepsJSON;

    private int mStep;
    private String description;
    private String videoURL;
    private String thumbnailURL;

    // The views variables
    @BindView(R.id.player_container)
    View mPlayerView;
    @BindView(R.id.iv_thumbnail)
    ImageView thumbnailView;
    @BindView(R.id.tv_illustration_not_available_label)
    TextView errorMessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        ButterKnife.bind(this);

        // Recover the views state in case of device rotating
        if (savedInstanceState != null) {
            mPlayerView.setVisibility(savedInstanceState.getInt(PLAYER_VIEW_VISIBILITY));
            thumbnailView.setVisibility(savedInstanceState.getInt(THUMBNAIL_VIEW_VISIBILITY));
            errorMessageView.setVisibility(savedInstanceState.getInt(ERROR_VIEW_VISIBILITY));
        }

        /* Initialize the data vars for this class */

        Intent intentThatStartedThisActivity = getIntent();

        mStep = intentThatStartedThisActivity.getIntExtra("id", 0);
        description = intentThatStartedThisActivity.getStringExtra("description");
        videoURL = intentThatStartedThisActivity.getStringExtra("videoURL");
        thumbnailURL = intentThatStartedThisActivity.getStringExtra("thumbnailURL");
        stepsJSONString = intentThatStartedThisActivity.getStringExtra("stepsJSONString");

        try {
            stepsJSON = new JSONArray(stepsJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* Render the views with the data vars */

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        // only create new fragment when there is no previously saved state
        if (savedInstanceState == null) {
            loadViews();
        }

    }


    private void loadViews() {

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

        Log.v(TAG, "loadViews videoURL:" + videoURL);

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        StepDetailFragment stepDetailFragment = new StepDetailFragment();

        // Set the fragment data
        if (description != null) {
            stepDetailFragment.setDescription(description);
        } else {
            stepDetailFragment.setDescription("No step description available.");
        }
        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();

        // Create a new ExoPlayerFragment instance and display it using FragmentManager
        // or try to load and show the Thumbnail
        if (!videoURL.equals("")) {

            ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
            // Set the fragment data
            exoPlayerFragment.setMediaUrl(videoURL);
            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager playerFragmentManager = getSupportFragmentManager();
            playerFragmentManager.beginTransaction()
                    .add(R.id.player_container, exoPlayerFragment)
                    .commit();
            mPlayerView.setVisibility(View.VISIBLE);

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
                                thumbnailView.setVisibility(View.VISIBLE);
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
            } else {
                errorMessageView.setVisibility(View.VISIBLE);
            }

        }
    }


    // Extract the vars for the step being viewed
    private void loadStepVars() {
        // Extract the vars for the mStep
        try {
            JSONObject jsonObject = stepsJSON.getJSONObject(mStep);
            description = jsonObject.getString("description");
            videoURL = jsonObject.getString("videoURL");
            thumbnailURL = jsonObject.getString("thumbnailURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Called by button Prev
    public void loadPrevStep(View view) {
        mStep--;
        if (mStep < 0) { mStep = 0; }
        loadStepVars();
        loadViews();
    }


    // Called by button Next
    public void loadNextStep(View view) {
        mStep++;
        if (mStep >= stepsJSON.length()) {
            mStep = stepsJSON.length() - 1;
        }
        loadStepVars();
        loadViews();
    }


    // This is for saving the step that is being viewed when the device is rotated
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(PLAYER_VIEW_VISIBILITY, mPlayerView.getVisibility());
        outState.putInt(THUMBNAIL_VIEW_VISIBILITY, thumbnailView.getVisibility());
        outState.putInt(ERROR_VIEW_VISIBILITY, errorMessageView.getVisibility());
        outState.putInt("mStep", mStep);

        super.onSaveInstanceState(outState);
    }


}
