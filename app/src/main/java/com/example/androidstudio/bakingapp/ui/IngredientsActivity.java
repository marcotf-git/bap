package com.example.androidstudio.bakingapp.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.data.Ingredient;
import com.example.androidstudio.bakingapp.data.RecipesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsActivity extends AppCompatActivity {

    private static final String TAG = IngredientsActivity.class.getSimpleName();

    public static final String EXTRA_RECIPE_NAME = "com.example.androidstudio.bakingapp.extra.RECIPE_NAME";
    public static final String EXTRA_INGREDIENTS_JSON = "com.example.androidstudio.bakingapp.extra.INGREDIENTS_JSON";

    private String mRecipeName;
    private String mIngredientsJSON;

    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();

    @BindView(R.id.tv_recipe_name) TextView mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        ButterKnife.bind(this);

        mRecipeName = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        mIngredientsJSON = getIntent().getStringExtra(EXTRA_INGREDIENTS_JSON);

        // Render the views
        mDisplayName.setText(mRecipeName);

        // Convert the string to the JSON object and update the view
        try {
            JSONArray ingredientsJSON = new JSONArray(mIngredientsJSON);
            updateIngredientsView(ingredientsJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * This helper function will load the IngredientsFragment, to show the ingredients in a view list.
     */
    public void updateIngredientsView (JSONArray ingredientsJSON) {

        Log.v(TAG, "updateIngredientsView ingredientsJSON:" + ingredientsJSON.toString());

        int nIngredients = ingredientsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nIngredients; i++) {

            int ingredientQuantity;
            String ingredientMeasure;
            String ingredientName;
            JSONObject jsonObject;

            try {
                jsonObject = ingredientsJSON.getJSONObject(i);
                ingredientQuantity = jsonObject.getInt("quantity");
                ingredientMeasure = jsonObject.getString("measure");
                ingredientName = jsonObject.getString("ingredient");
                //Ingredient ingredient = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);
                //ingredients.add(ingredient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // At this point, we have an Array with the ingredients information
        Log.v(TAG, "updateIngredientsView ingredients:" + ingredients.toString());

        // Create a new IngredientsFragment instance and display it using the FragmentManager
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        // Set the fragment data
        //ingredientsFragment.setIngredients(ingredients);
        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ingredients_container, ingredientsFragment)
                .commit();
    }

}
