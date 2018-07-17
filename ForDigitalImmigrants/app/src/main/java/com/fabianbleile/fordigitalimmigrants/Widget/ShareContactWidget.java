package com.fabianbleile.fordigitalimmigrants.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fabianbleile.fordigitalimmigrants.MainActivity;
import com.fabianbleile.fordigitalimmigrants.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ShareContactWidgetConfigureActivity ShareContactWidgetConfigureActivity}
 */
public class ShareContactWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.share_contact_widget);
        views.setImageViewResource(R.id.bt_sendContactFromWidget, R.drawable.ic_circle_primary);
        views.setOnClickPendingIntent(R.id.bt_sendContactFromWidget, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public ShareContactWidget() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

