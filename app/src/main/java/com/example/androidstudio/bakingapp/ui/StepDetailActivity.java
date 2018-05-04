package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;

public class StepDetailActivity extends AppCompatActivity {


    private TextView mDisplayStepDescription;

    private String stepDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        mDisplayStepDescription = findViewById(R.id.tv_step_description);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("stepDescription")) {

            stepDescription = intentThatStartedThisActivity.getStringExtra("stepDescription");
            mDisplayStepDescription.setText(stepDescription);

        }

    }
}
