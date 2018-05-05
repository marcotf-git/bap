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

import java.util.ArrayList;


public class IngredientsFragment extends Fragment {

    private static final String TAG = IngredientsFragment.class.getSimpleName();

    // Variables to store resources that this fragment displays
    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> mIngredients = new ArrayList<>();

    // Mandatory constructor for instantiating the fragment
    public IngredientsFragment() {
    }


    /**
     * Inflates the fragment layout and sets any view resources
      */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the Ingredients fragment layout
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        // Get a reference to the ingredients list
        ListView listView = rootView.findViewById(R.id.ingredients_list);

        // Set the data to display
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getContext(), mIngredients);
        listView.setAdapter(ingredientAdapter);

        // Adjust the size of the list view to show all the ingredients
        float listItemHeight = getResources().getDimension(R.dimen.ingredient_list_item_height);
        float listItemPadding = getResources().getDimension(R.dimen.list_padding);
        Log.v(TAG, " list item height:" + listItemHeight);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int totalHeight = Math.round((listItemHeight * mIngredients.size())+ listItemPadding);
        params.height = totalHeight;
        Log.v(TAG, " total list item height:" + totalHeight);
        listView.setLayoutParams(params);

        // Return root view
        return rootView;

    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        mIngredients.addAll(ingredients);
    }

}
