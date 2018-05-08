package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.androidstudio.bakingapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/***
 * This activity has the function of starting the step detail fragment
 */
public class StepDetailActivity extends AppCompatActivity {

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    private View mPlayerView;
    private ImageView thumbnailView;

    private String videoURL;
    private String thumbnailURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        thumbnailView = findViewById(R.id.iv_thumbnail);
        mPlayerView = findViewById(R.id.player_container);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("thumbnailURL")) {
            thumbnailURL = intentThatStartedThisActivity.getStringExtra("thumbnailURL");
        }

        if (intentThatStartedThisActivity.hasExtra("videoURL")) {
            videoURL = intentThatStartedThisActivity.getStringExtra("videoURL");
        }

        // Adjust the visibility of the views
        if (videoURL.equals("")) {

            mPlayerView.setVisibility(View.GONE);

            if (videoURL.equals("")) {
                thumbnailView.setVisibility(View.GONE);
            } else {
                thumbnailView.setVisibility(View.VISIBLE);
            }

        } else {

            mPlayerView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.GONE);
        }

        // Create a new StepDetailFragment instance and display it using the FragmentManager
        // only create new fragment when there is no previously saved state
        if (savedInstanceState == null) {

            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();

            // Set the fragment data
            if (intentThatStartedThisActivity.hasExtra("id")) {
                stepDetailFragment.setId(intentThatStartedThisActivity
                        .getIntExtra("id", 0));
            } else {
                stepDetailFragment.setId(0);
            }

            if (intentThatStartedThisActivity.hasExtra("description")) {
                stepDetailFragment.setDescription(intentThatStartedThisActivity
                        .getStringExtra("description"));
            } else {
                stepDetailFragment.setDescription("No step description available.");
            }

            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Fragment transaction
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();


            // Create a new PlayerFragment instance and display it using FragmentManager
            // or load the Thumbnail

            if (!videoURL.equals("")) {

                PlayerFragment playerFragment = new PlayerFragment();
                // Set the fragment data
                playerFragment.setMediaUrl(videoURL);
                // Use a FragmentManager and transaction to add the fragment to the screen
                FragmentManager playerFragmentManager = getSupportFragmentManager();
                playerFragmentManager.beginTransaction()
                        .add(R.id.player_container, playerFragment)
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
                                }
                            });
                }

            }

        }

    }

}
