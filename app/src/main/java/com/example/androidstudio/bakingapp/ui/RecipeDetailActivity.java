package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Ingredient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // The views variables
    private TextView mDisplayName;

    // The data of the recipe being viewed
    private String movieStringJSON;
    private String recipeId = "";
    private String recipeName = "";

    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mDisplayName = findViewById(R.id.tv_recipe_name);

        Intent intentThatStartedThisActivity = getIntent();


        if(intentThatStartedThisActivity.hasExtra("recipeStringJSON")){

            movieStringJSON = intentThatStartedThisActivity.getStringExtra("recipeStringJSON");
            updateView(movieStringJSON);

        }

    }


    public void updateView(String recipeStringJSON){

        Context context = RecipeDetailActivity.this;

        try {

            // Convert the string to the JSON object
            JSONObject recipeJSON = new JSONObject(recipeStringJSON);

            // Extract the recipe id and name
            recipeId = recipeJSON.getString("id");
            recipeName = recipeJSON.getString("name");

            // Update views
            mDisplayName.setText(recipeName);


            JSONArray ingredientsJSON = recipeJSON.getJSONArray("ingredients");

            updateIngredientsView(ingredientsJSON);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


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

                Ingredient ingredient = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);

                ingredients.add(ingredient);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        // At this point, we have an Array with the ingredients information
        Log.v(TAG, "updateIngredientsView inredients:" + ingredients.toString());

        // Set the adapter to show the array on the list view
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, ingredients);
        ListView listView = findViewById(R.id.ingredients_list);
        listView.setAdapter(ingredientAdapter);

        // Adjust the size of the list view to show all the ingredients
        float listItemHeight = getResources().getDimension(R.dimen.ingredient_list_item_height);
        Log.v(TAG, " list item height:" + listItemHeight);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int totalHeight = Math.round(listItemHeight) * (ingredients.size());
        params.height = totalHeight;
        Log.v(TAG, " total list item height:" + totalHeight);
        listView.setLayoutParams(params);


    }




}
