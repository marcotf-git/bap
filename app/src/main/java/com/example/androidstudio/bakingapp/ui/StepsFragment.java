package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidstudio.bakingapp.R;


public class StepsFragment extends Fragment
        implements StepsListAdapter.ListItemClickListener{

    private static final String TAG = StepsFragment.class.getSimpleName();

    public static final String STEPS_JSON_STRING = "stepsJSONString";

    // Variables to store resources that this fragment displays
    private String stepsJSONString;

    private StepsListAdapter mAdapter;

    // Listener variable
    OnItemClickListener mCallback;


    // Listener for communication with the RecipeDetailActivity
    public interface OnItemClickListener {
        void onStepSelected(int stepId,
                            String shortDescription,
                            String description,
                            String videoURL,
                            String thumbnailURL,
                            String stepsJSONString);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    // Mandatory constructor for instantiating the fragment
    public StepsFragment() {
    }

    /**
     * Inflates the fragment layout and sets any view resources
      */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the Ingredients fragment layout
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        // Get a reference to the ingredients list
        RecyclerView mStepsList = rootView.findViewById(R.id.steps_list);

        // Set the layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mStepsList.setLayoutManager(layoutManager);
        mStepsList.setHasFixedSize(true);

        // Set the data to display
        mAdapter = new StepsListAdapter(this);
        mStepsList.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            stepsJSONString = savedInstanceState.getString(STEPS_JSON_STRING);
        }

        mAdapter.setStepsData(stepsJSONString);

        // Return root view
        return rootView;

    }


    public void setSteps(String stepsJSONString) {

        this.stepsJSONString = stepsJSONString;
    }


    @Override
    public void onListItemClick(int stepId,
                                String shortDescription,
                                String description,
                                String videoURL,
                                String thumbnailURL,
                                String stepsJSONString) {

        mCallback.onStepSelected(stepId,
                shortDescription,
                description,
                videoURL,
                thumbnailURL,
                stepsJSONString);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(STEPS_JSON_STRING, stepsJSONString);

        super.onSaveInstanceState(savedInstanceState);
    }


}
