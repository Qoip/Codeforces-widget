package com.example.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 * android:textSize="28dp"
 */

public class NewAppWidget extends AppWidgetProvider {
    public static String packageName;
    public static int id;
    public static AppWidgetManager awm;
    public static Context cnt;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.w("work", "** Update called on id " + appWidgetId);
        packageName = context.getPackageName();
        id = appWidgetId;
        awm = appWidgetManager;
        cnt = context;
        HttpClients hc = new HttpClients();
        String name = NewAppWidgetConfigureActivity.loadTitlePref(cnt, id);
        hc.execute("https://codeforces.com/api/user.info?handles=" + name); // bug on restart: long refresh // undel ids

        Intent updateIntent = new Intent(context, NewAppWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setOnClickPendingIntent(R.id.btn, pIntent);
        NewAppWidget.awm.updateAppWidget(id, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e("mass", Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);

            AppWidgetHost host = new AppWidgetHost(context, 1);
            host.deleteAppWidgetId(appWidgetId);
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
}
