package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;

/***
 * This activity has the function of starting the step detail fragment
 */
public class StepDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        // only create new fragment when there is no previously saved state
        if(savedInstanceState == null) {
            Intent intentThatStartedThisActivity = getIntent();

            // Create a new StepDetailFragment instance and display it using the FragmentManager
            StepDetailFragment stepDetailFragment = new StepDetailFragment();

            // Set the fragment data
            if (intentThatStartedThisActivity.hasExtra("id")) {
                stepDetailFragment.setId(intentThatStartedThisActivity.getIntExtra("id", 0));
            } else {
                stepDetailFragment.setId(0);
            }

            if (intentThatStartedThisActivity.hasExtra("description")) {
                stepDetailFragment.setDescription(intentThatStartedThisActivity.getStringExtra("description"));
            } else {
                stepDetailFragment.setDescription("No step description available.");
            }

            if (intentThatStartedThisActivity.hasExtra("videoURL")) {
                stepDetailFragment.setVideoURL(intentThatStartedThisActivity.getStringExtra("videoURL"));
            } else {
                stepDetailFragment.setVideoURL("");
            }

            if (intentThatStartedThisActivity.hasExtra("thumbnailURL")) {
                stepDetailFragment.setThumbnailURL(intentThatStartedThisActivity.getStringExtra("thumbnailURL"));
            } else {
                stepDetailFragment.setThumbnailURL("");
            }


            // Use a FragmentManager and transaction to add the fragment to the screen
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Fragment transaction
            fragmentManager.beginTransaction()
                    .add(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }

    }

}
