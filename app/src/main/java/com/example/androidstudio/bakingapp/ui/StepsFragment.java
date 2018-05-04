package com.example.androidstudio.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Ingredient;
import com.example.androidstudio.bakingapp.utilities.Step;

import java.util.ArrayList;


public class StepsFragment extends Fragment {

    private static final String TAG = StepsFragment.class.getSimpleName();


    // The array for storing information about the steps
    private final ArrayList<Step> mSteps = new ArrayList<>();

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
        float listItemHeight = getResources().getDimension(R.dimen.ingredient_list_item_height);
        Log.v(TAG, " list item height:" + listItemHeight);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int totalHeight = Math.round(listItemHeight) * (mSteps.size());
        params.height = totalHeight;
        Log.v(TAG, " total list item height:" + totalHeight);
        listView.setLayoutParams(params);

        // Return root view
        return rootView;

    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps.addAll(steps);
    }

}
