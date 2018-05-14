package com.example.androidstudio.bakingapp.utilities;

import com.example.androidstudio.bakingapp.data.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RecipesAPI {

    String PATH_URL = "android-baking-app-json";

    @GET(PATH_URL)
    Call<List<Recipe>> loadChanges(@Query("q") String status);

}
