package com.example.androidstudio.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.utilities.Ingredient;
import com.example.androidstudio.bakingapp.utilities.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity
    implements StepsFragment.OnItemClickListener{

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // The views variables
    private TextView mDisplayName;

    // The data of the recipe being viewed
    private String recipeStringJSON;
    private String recipeName = "";

    // The array for storing information about the ingredients
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();

    // The array for storing information about the steps
    private final ArrayList<Step> steps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mDisplayName = findViewById(R.id.tv_recipe_name);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.hasExtra("recipeStringJSON")){

            recipeStringJSON = intentThatStartedThisActivity.getStringExtra("recipeStringJSON");
            updateView(recipeStringJSON);

        }

    }


    public void updateView(String recipeStringJSON){

        try {

            // Convert the string to the JSON object
            JSONObject recipeJSON = new JSONObject(recipeStringJSON);

            // Extract the recipe name
            recipeName = recipeJSON.getString("name");

            // Update views
            mDisplayName.setText(recipeName);

            JSONArray ingredientsJSON = recipeJSON.getJSONArray("ingredients");
            updateIngredientsView(ingredientsJSON);

            JSONArray stepsJSON = recipeJSON.getJSONArray("steps");
            updateStepsView(stepsJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * This helper function will load the IngredientsFragment, to show the ingredients in a list
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

                Ingredient ingredient = new Ingredient(ingredientQuantity, ingredientMeasure, ingredientName);

                ingredients.add(ingredient);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // At this point, we have an Array with the ingredients information
        Log.v(TAG, "updateIngredientsView ingredients:" + ingredients.toString());

        // Create a new IngredientsFragment instance and display it using the FragmentManager
        IngredientsFragment ingredientsFragment = new IngredientsFragment();

        // Set the fragment data
        ingredientsFragment.setIngredients(ingredients);

        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .add(R.id.ingredients_container, ingredientsFragment)
                .commit();
    }


    /**
     * This helper function will load the StepsFragment, to show the steps in a list
     */
    public void updateStepsView (JSONArray stepsJSON) {

        Log.v(TAG, "updateStepsView stepsJSON:" + stepsJSON.toString());

        int nSteps = stepsJSON.length();

        // Create an ArrayList with the ingredients for the recipe
        for (int i = 0; i < nSteps; i++) {

            int id;
            String shortDescription;
            String description;
            String videoURL;
            String thumbnailURL;

            JSONObject jsonObject;

            try {

                jsonObject = stepsJSON.getJSONObject(i);

                id = jsonObject.getInt("id");
                shortDescription = jsonObject.getString("shortDescription");
                description = jsonObject.getString("description");
                videoURL = jsonObject.getString("videoURL");
                thumbnailURL = jsonObject.getString("thumbnailURL");

                Step step = new Step(id, shortDescription, description, videoURL, thumbnailURL);

                steps.add(step);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // At this point, we have an Array with the steps information
        Log.v(TAG, "updateStepsView steps:" + steps.toString());

        // Create a new StepsFragment instance and display it using the FragmentManager
        StepsFragment stepsFragment = new StepsFragment();

        // Set the fragment data
        stepsFragment.setSteps(steps);

        // Use a FragmentManager and transaction to add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Fragment transaction
        fragmentManager.beginTransaction()
                .add(R.id.steps_container, stepsFragment)
                .commit();
    }


    /**
     * This is the listener that receives communication from the StepsFragment
     */
    @Override
    public void onStepSelected(int position) {


        // Call the StepDetailActivity to show the step detail

        //Toast.makeText(this, "Position= " + position, Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onStepSelected:" + position);

        Step step = steps.get(position);

        Context context = RecipeDetailActivity.this;

        Class destinationActivity = StepDetailActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);

        startChildActivityIntent.putExtra("id", step.getId());
        startChildActivityIntent.putExtra("shortDescription", step.getShortDescription());
        startChildActivityIntent.putExtra("description", step.getDescription());
        startChildActivityIntent.putExtra("videoURL", step.getVideoURL());
        startChildActivityIntent.putExtra("thumbnailURL", step.getThumbnailURL());

        startActivity(startChildActivityIntent);



        // Updates the container with the step detail fragment

//        // Create a new StepDetailFragment instance and display it using the FragmentManager
//        StepDetailFragment stepDetailFragment = new StepDetailFragment();
//
//        // Set the fragment data
//        stepDetailFragment.setDescription(step.getDescription());
//
//        // Use a FragmentManager and transaction to add the fragment to the screen
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        // Fragment transaction
//        fragmentManager.beginTransaction()
//                .add(R.id.step_detail, stepDetailFragment)
//                .commit();


    }
}
