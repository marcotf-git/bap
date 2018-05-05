package com.example.androidstudio.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Step;

import java.util.ArrayList;


public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Variables to store resources that this fragment displays
    private int mId;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;

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

        // Inflate the Steps fragment layout
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        mDisplayStepDescription = rootView.findViewById(R.id.tv_step_description);
        mDisplayStepDescription.setText(description);

        // Return root view
        return rootView;
    }


    public void setId(int id) {
        this.mId = id;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
