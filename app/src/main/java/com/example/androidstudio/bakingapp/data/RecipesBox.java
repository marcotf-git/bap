package com.example.androidstudio.bakingapp.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that handles JSON format recipes
 */
public class RecipesBox {

    private JSONArray recipes;

    // Constructor
    public RecipesBox(String recipesJSONString) {

        try {
            recipes = new JSONArray(recipesJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


   public int getNumberOfRecipes() {

        if (null != recipes) {
            return recipes.length();
        }

        return 0;
    }


    public JSONObject getRecipeJSON(int position) {

        JSONObject recipeJSON;

        try {
            recipeJSON = recipes.getJSONObject(position);
            return recipeJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
