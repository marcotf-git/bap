package com.example.androidstudio.bakingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.androidstudio.bakingapp.R;
import com.example.androidstudio.bakingapp.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static final String TAG = IngredientsWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String recipeName, int appWidgetId) {

        Log.d(TAG, "updateAppWidget ");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);
        views.setTextViewText(R.id.tv_widget_recipe_name, recipeName);

        // Set the widget title to launch the MainActivity
        Intent appMainIntent = new Intent(context, MainActivity.class);
        PendingIntent appMainPendingIntent = PendingIntent.getActivity(
                context,
                0,
                appMainIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_title, appMainPendingIntent);

        // Set up the collection
        // Set the ListWidgetService intent to act as the adapter for the list view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, ListWidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, ListWidgetService.class));
    }


    public static void updateIngredientsWidgets(Context context, AppWidgetManager appWidgetManager,
                                                String recipeName, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, appWidgetId);
        }
    }

}

