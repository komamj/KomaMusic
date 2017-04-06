package com.koma.music.base;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by koma on 3/27/17.
 */

public class BaseAppWidget extends AppWidgetProvider {

    protected PendingIntent buildPendingIntent(Context context, final String action,
                                               final ComponentName serviceName) {
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
