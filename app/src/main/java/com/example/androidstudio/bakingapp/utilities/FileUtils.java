package com.example.androidstudio.bakingapp.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


public class FileUtils {


    public static String getStringFromFile(Context context, String fileName) throws IOException {

        if (null == fileName) {
            return null;
        }

        String recipesJSONString = null;
        AssetManager assetManager = context.getAssets();

        try{

            InputStream inputStream = assetManager.open(fileName);
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                recipesJSONString = scanner.next();
                inputStream.close();
                return recipesJSONString;
            } else {
                inputStream.close();
                return null;
            }

        } catch (Exception e){
           e.printStackTrace();
        }

        return null;
    }

}
