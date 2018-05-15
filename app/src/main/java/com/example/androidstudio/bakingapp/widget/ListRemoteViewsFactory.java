package com.example.androidstudio.bakingapp.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ListRemoteViewsFactory acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class ListRemoteViewsFactory implements
        RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = ListRemoteViewsFactory.class.getSimpleName();

    private static final List<String> mCollection = new ArrayList<>();

    private Context mContext = null;
    private Cursor mCursor;

    public ListRemoteViewsFactory(Context context) {

        mContext = context;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate  mCollection: " + mCollection.toString());
    }

    @Override
    public void onDataSetChanged() {
        Log.v(TAG, "onDataSetChanged  mCollection: " + mCollection.toString());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        view.setTextViewText(android.R.id.text1, mCollection.get(position));

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        // Treat all items in the ListView the same
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    public static void setWidgetProviderData(String ingredientsJSONString) {

        mCollection.clear();

        JSONArray ingredientsJSON = null;
        try {
            ingredientsJSON = new JSONArray(ingredientsJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double quantity;
        String measure;
        String ingredientName;
        String ingredient;

        if (ingredientsJSON != null) {
            for (int i = 0; i < ingredientsJSON.length(); i++) {
                try {

                    JSONObject ingredientJSON = ingredientsJSON.getJSONObject(i);
                    quantity = ingredientJSON.getDouble("quantity");
                    measure = ingredientJSON.getString("measure");
                    ingredientName = ingredientJSON.getString("ingredient");

                    ingredient = quantity + " " + measure + " " + ingredientName;

                    Log.d(TAG, "setWidgetProviderData ingredient:" + ingredient);

                    mCollection.add(ingredient);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
