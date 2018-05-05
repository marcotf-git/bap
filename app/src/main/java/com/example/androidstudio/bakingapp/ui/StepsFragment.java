package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Step;

import java.util.ArrayList;


public class StepsFragment extends Fragment {

    private static final String TAG = StepsFragment.class.getSimpleName();

    // Variables to store resources that this fragment displays
    // The array for storing information about the steps
    private final ArrayList<Step> mSteps = new ArrayList<>();

    // Listener variable
    OnItemClickListener mCallback;

    // Listener for communication with the RecipeDetailActivity
    public interface OnItemClickListener {
        void onStepSelected(int position);
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

        // Inflate the Steps fragment layout
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        // Get a reference to the steps list
        ListView listView = rootView.findViewById(R.id.steps_list);

        // Set the data to display
        StepAdapter stepAdapter = new StepAdapter(getContext(), mSteps);
        listView.setAdapter(stepAdapter);

        // Adjust the size of the list view to show all the steps
        float listItemHeight = getResources().getDimension(R.dimen.step_list_item_height);
        float listItemPadding = getResources().getDimension(R.dimen.list_padding);
        Log.v(TAG, " list item height:" + listItemHeight);
        Log.v(TAG, " mSteps:" + mSteps.size());
        Log.v(TAG, " listItemPadding:" + listItemPadding);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int totalHeight = Math.round((listItemHeight * mSteps.size()) + listItemPadding);
        params.height = totalHeight;
        Log.v(TAG, " total list item height:" + totalHeight);
        listView.setLayoutParams(params);


        // Set a click listener on the list view
        // Set listener to show step description
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mCallback.onStepSelected(position);
//                Step step = mSteps.get(position);
//
//                Class destinationActivity = StepDetailActivity.class;
//                Intent startChildActivityIntent = new Intent(getContext(), destinationActivity);
//
//                startChildActivityIntent.putExtra("stepDescription", step.getDescription());
//
//                startActivity(startChildActivityIntent);
            }
        });


        // Return root view
        return rootView;

    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps.addAll(steps);
    }



}
