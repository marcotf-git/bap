package com.example.androidstudio.bakingapp.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidstudio.bakingapp.R;


public class IngredientsFragment extends Fragment {

    private static final String TAG = IngredientsFragment.class.getSimpleName();

    public static final String INGREDIENTS_JSON_STRING = "ingredientsJSONString";

    // Variables to store resources that this fragment displays
    private String ingredientsJSONString;

    private IngredientsListAdapter mAdapter;


    // Mandatory constructor for instantiating the fragment
    public IngredientsFragment() {
    }

    /**
     * Inflates the fragment layout and sets any view resources
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v(TAG, "onCreateView savedInstanceState:" + savedInstanceState);

        // Inflate the Ingredients fragment layout
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        // Get a reference to the ingredients list
        RecyclerView mIngredientsList = rootView.findViewById(R.id.ingredients_list);

        // Set the layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mIngredientsList.setLayoutManager(layoutManager);
        mIngredientsList.setHasFixedSize(true);

        // Set the data to display
        mAdapter = new IngredientsListAdapter();
        if(savedInstanceState != null){
            ingredientsJSONString = savedInstanceState.getString(INGREDIENTS_JSON_STRING);
        }
        mAdapter.setIngredientsData(ingredientsJSONString);

        mIngredientsList.setAdapter(mAdapter);

        // Return root view
        return rootView;

    }

    public void setIngredients(String ingredientsJSONString) {
        this.ingredientsJSONString = ingredientsJSONString;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(INGREDIENTS_JSON_STRING, ingredientsJSONString);

        super.onSaveInstanceState(savedInstanceState);
    }


}
