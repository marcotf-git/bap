package com.example.androidstudio.bakingapp.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.androidstudio.bakingapp.data.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.Toast.LENGTH_LONG;

/**
 * This class is the Controller for the Retrofit.
 *  It was modified for having two options of source: the original internet query or
 *  a local file in the assets folder.
 *  Set the flag LOAD_FROM_FILE as true to load from file.
 */
public class Controller implements Callback<List<Recipe>> {

    private static final String TAG = Controller.class.getSimpleName();
    private static final String BASE_URL = "https://go.udacity.com/";

    // Set to true if for testing (will load from local file in the Assets folder).
    private static final boolean LOAD_FROM_FILE = false;
    private static final String FILE_NAME = "baking.json";

    private OnDataLoadedListener mCallback;

    private Context mContext;

    private static List<Recipe> recipesList = null;


    public interface OnDataLoadedListener {
        void onDataLoaded(List<Recipe> recipes);
    }

    public void start(Context context) {

        mContext = context;

        Log.v(TAG, "start: recipesList is null?:" + recipesList);

        try{
            mCallback = (OnDataLoadedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataLoadedListener");
        }

        // Do not load if already loaded
        if (recipesList != null) {
            // Call the Controller callback
            mCallback.onDataLoaded(recipesList);
            return;
        }

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

        Log.v(TAG, "start: loading from web");

    }

    @Override
    public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {

        Log.v(TAG, "onResponse: end loading from web");

        if(response.isSuccessful()) {

            recipesList = response.body();

            // Will swap the recipesList if LOAD_FROM_FILE is true
            new FileQueryTask().execute(FILE_NAME);

            //mCallback.onDataLoaded(recipesList);

        } else {
            Log.e(TAG, "onResponse:" + response.errorBody());
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {

        Log.v(TAG, "onFailure loading");
        Toast.makeText(mContext, "Error in loading.", LENGTH_LONG).show();

        t.printStackTrace();

        mCallback.onDataLoaded(null);
    }


    // Async Task for loading from file
    public class FileQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String fileName = params[0];
            String fileSearchResults = null;

            try {
                fileSearchResults = FileUtils.getStringFromFile(mContext, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileSearchResults;
        }

        @Override
        protected void onPostExecute(String fileSearchResults) {

            if (fileSearchResults != null && !fileSearchResults.equals("") && LOAD_FROM_FILE) {

                try {

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Recipe>>(){}.getType();
                    recipesList = gson.fromJson(fileSearchResults, listType);

                    Toast.makeText(mContext, "Loading from file.", LENGTH_LONG).show();

                } catch (Exception e) {

                    e.printStackTrace();

                    Toast.makeText(mContext, "Error in loading from file.", LENGTH_LONG).show();

                    if (recipesList != null) {
                        recipesList.clear();
                    }
                }

            } else {
                Toast.makeText(mContext, "Loading from web.", LENGTH_LONG).show();
            }

            // Call the Controller callback
            mCallback.onDataLoaded(recipesList);

        }
    }

    public static void clearRecipesList() {
        if (null != recipesList) {
            recipesList.clear();
        }
        recipesList = null;
    }

}
