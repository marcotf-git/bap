package com.example.androidstudio.bakingapp.ui;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;


public class IngredientsFragment extends Fragment {

    private static final String TAG = IngredientsFragment.class.getSimpleName();

    private static final String INGREDIENTS_JSON_STRING = "ingredientsJSONString";
    private static final String SERVINGS = "servings";

    // Variables to store resources that this fragment displays
    private String ingredientsJSONString;
    private Integer servings;

    private IngredientsListAdapter mAdapter;

//    OnIngredientsLoadedListener widgetCallback;

    // Mandatory constructor for instantiating the fragment
    public IngredientsFragment() {
    }

    // Interface for communication with the widget
//    public interface OnIngredientsLoadedListener {
//        void onIngredientsLoaded(String ingredientsJSONString);
//    }

    /**
     * Inflates the fragment layout and sets any view resources
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
            servings = savedInstanceState.getInt(SERVINGS);
        }

        TextView textServings = rootView.findViewById(R.id.tv_servings);
        if (servings != null) {
            String ingredientsLabel = getText(R.string.ingredients) + " (" + servings + " " +
                    getText(R.string.servings) + ")";
            textServings.setText(ingredientsLabel);
        } else {
            String ingredientsLabel = getText(R.string.ingredients) + "";
            textServings.setText(ingredientsLabel);
        }

        mAdapter.setIngredientsData(ingredientsJSONString);

        mIngredientsList.setAdapter(mAdapter);

        // Return root view
        return rootView;

    }

    public void setIngredients(String ingredientsJSONString, Integer servings) {
        this.ingredientsJSONString = ingredientsJSONString;
        this.servings = servings;
//        widgetCallback.onIngredientsLoaded(ingredientsJSONString);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {

        savedInstanceState.putString(INGREDIENTS_JSON_STRING, ingredientsJSONString);
        savedInstanceState.putInt(SERVINGS, servings);

        super.onSaveInstanceState(savedInstanceState);
    }


}
