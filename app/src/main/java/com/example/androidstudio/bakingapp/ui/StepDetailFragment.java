package com.example.androidstudio.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;


public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Final Strings to store state information
    public static final String STEP_DESCRIPTION = "description";

    // Variables to store resources that this fragment displays
    private String description;

    // References to the view
    private TextView mDisplayStepDescription;

    // Mandatory constructor for instantiating the fragment
    public StepDetailFragment() {
    }

    /**
     * Inflates the fragment layout and sets any view resources
      */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            description = savedInstanceState.getString(STEP_DESCRIPTION);
        }

        // Inflate the Steps fragment layout
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        mDisplayStepDescription = (TextView) rootView.findViewById(R.id.tv_step_description);
        mDisplayStepDescription.setText(description);

        // Return root view
        return rootView;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STEP_DESCRIPTION, description);
        super.onSaveInstanceState(outState);
    }

}
