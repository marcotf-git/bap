package com.example.androidstudio.bakingapp.data;

/**
 * This class will store the ingredient
 */

public class Ingredient {

    private int mQuantity;
    private String mMeasure;
    private String mIngredient;

    public Ingredient(int quantity, String measure, String ingredient) {
        mQuantity = quantity;
        mMeasure = measure;
        mIngredient = ingredient;
    }

    public int getIngredientQuantity() {
        return mQuantity;
    }

    public String getIngredientMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }

}
