package com.example.androidstudio.bakingapp.utilities;

import android.util.Log;

import com.example.androidstudio.bakingapp.data.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Controller implements Callback<List<Recipe>> {

    private static final String TAG = Controller.class.getSimpleName();
    static final String BASE_URL = "https://go.udacity.com/";

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RecipesAPI recipesAPI = retrofit.create(RecipesAPI.class);

        Call<List<Recipe>> call = recipesAPI.loadChanges("status:open");
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if(response.isSuccessful()) {
            List<Recipe> recipesList = response.body();

            for(Recipe recipe: recipesList) {
                Log.v(TAG, "onResponse:" + recipe.toString());
            }

        } else {
            Log.e(TAG, "onResponse:" + response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        t.printStackTrace();
    }

}
