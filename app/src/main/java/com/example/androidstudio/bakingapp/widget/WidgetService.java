package com.example.androidstudio.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.androidstudio.bakingapp.widget.WidgetDataProvider;

/**
 * WidgetService is the {@link RemoteViewsService} that will return our RemoteViewsFactory
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
