package com.fabianbleile.fordigitalimmigrants;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class LastContactWidget extends AppWidgetProvider {

    public static String LAST_CONTACT_ID = "lastContactId";
    public static int lastContactId;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.last_contact_widget);

        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, RemoteViewsService.class);
        intent.putExtra(LAST_CONTACT_ID, lastContactId);
        views.setTextViewText(R.id.tv_title, widgetText);
        views.setRemoteAdapter(R.id.listView, intent);

        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.listView, appPendingIntent);

        // Instruct the widget manager to update the widget
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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), AppWidgetManager.ACTION_APPWIDGET_UPDATE)){

            lastContactId = (int) intent.getIntExtra(LAST_CONTACT_ID, -1);

        } else {
            super.onReceive(context, intent);
        }
    }


}

