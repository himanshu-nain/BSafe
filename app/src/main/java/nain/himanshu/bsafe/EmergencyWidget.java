package nain.himanshu.bsafe;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class EmergencyWidget extends AppWidgetProvider {

    private static final int EMERGENCY_REQUEST = 13;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, EmergencyService.class);
        PendingIntent pIntent = PendingIntent.getService(
                context,
                EMERGENCY_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.emergency_widget);
        views.setOnClickPendingIntent(
                R.id.emer_img,
                pIntent
        );

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
}

