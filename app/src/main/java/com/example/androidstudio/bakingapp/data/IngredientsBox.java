package com.example.androidstudio.bakingapp.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IngredientsBox {

    private static JSONArray ingredients;

    private static List<Ingredient> mIngredients;


    // Constructor
    public IngredientsBox(String ingredientsJSONString) {

        Gson gson = new Gson();
        String jsonOutput = ingredientsJSONString;
        Type listType = new TypeToken<List<Ingredient>>(){}.getType();
        mIngredients = gson.fromJson(jsonOutput, listType);

        Log.v("IngredientsBox", "mIngredients:" + mIngredients.toString());
        Log.v("IngredientsBox", "mIngredients:" + getIngredientsListString());

    }


    public static int getNumberOfIngredients() {

        if (null != ingredients) {
            return ingredients.length();
        }

        return 0;
    }

    public static List<String> getIngredientsListString() {

        List<String> ingredients = new ArrayList<>();

        for(int i = 0; i < mIngredients.size(); i++) {
            ingredients.add(
                    mIngredients.get(i).getQuantity() + " " +
                            mIngredients.get(i).getMeasure() + " " +
                            mIngredients.get(i).getIngredient());
        }

        return ingredients;

    }

    public static JSONObject getIngredientJSON(int position) {

        JSONObject ingredientJSON;

        try {
            ingredientJSON = ingredients.getJSONObject(position);
            return ingredientJSON;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setIngredients(List<Ingredient> ingredients){

        if (null != mIngredients) {
            mIngredients.clear();
        }

        mIngredients = ingredients;

    }

    public static List<Ingredient> getIngredients(){

        return mIngredients;
    }


}
