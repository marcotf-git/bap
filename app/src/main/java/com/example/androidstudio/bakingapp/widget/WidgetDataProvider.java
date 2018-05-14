package com.example.androidstudio.bakingapp.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.data.RecipesContract;
import com.example.androidstudio.bakingapp.ui.IngredientsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    List<String> mCollection = new ArrayList<>();
    Cursor mCursor;
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        loadData();
    }

    @Override
    public void onDataSetChanged() {
        loadData();
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

        mCursor.moveToPosition(position);

        String recipeName = mCursor.getString(
                mCursor.getColumnIndex(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_NAME));
        String ingredientsJSON = mCursor.getString(
                mCursor.getColumnIndex(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_INGREDIENTS_JSON));

        // Fill in the onClick PendingIntent Template using the specific data for each item individually
        Bundle extras = new Bundle();
        extras.putString(IngredientsActivity.EXTRA_RECIPE_NAME, recipeName);
        extras.putString(IngredientsActivity.EXTRA_INGREDIENTS_JSON, ingredientsJSON);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        view.setOnClickFillInIntent(android.R.id.text1, fillInIntent);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
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

    // Load the recipe names from the database
    private void loadData() {

        try {
            ContentResolver resolver = mContext.getContentResolver();
            mCursor = resolver.query(RecipesContract.RecipeslistEntry.CONTENT_URI,
                    null, null, null, null);
            if (null != mCursor) {
                while (mCursor.moveToNext()) {
                    String recipeName = mCursor.getString(
                            mCursor.getColumnIndex(RecipesContract.RecipeslistEntry.COLUMN_RECIPE_NAME));
                    mCollection.add(recipeName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
