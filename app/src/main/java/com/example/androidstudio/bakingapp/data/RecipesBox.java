package com.example.androidstudio.bakingapp.data;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Class that handles JSON format recipes
 */
public class RecipesBox {

    private JSONArray recipes;

    private static List<Recipe> mRecipes;


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

    public static void setRecipes(List<Recipe> recipes){

        if (null != mRecipes) {
            mRecipes.clear();
        }

        mRecipes = recipes;

    }

    public static List<Recipe> getRecipes(){
        return mRecipes;
    }

}
