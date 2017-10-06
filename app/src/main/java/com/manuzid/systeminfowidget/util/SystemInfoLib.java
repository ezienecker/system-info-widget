package com.manuzid.systeminfowidget.util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class SystemInfoLib {
    public static final String GENERAL = "com.manuzid.systeminfowidget.GENERAL_WIDGET";
    public static final String MORE = "com.manuzid.systeminfowidget.MORE_WIDGET";
    public static final String DISPLAY = "com.manuzid.systeminfowidget.DISPLAY_WIDGET";
    public static final String CAMERA = "com.manuzid.systeminfowidget.CAMERA_WIDGET";
    public static final String MEMORY = "com.manuzid.systeminfowidget.MEMORY_WIDGET";
    public static final String BATTERY = "com.manuzid.systeminfowidget.BATTERY_WIDGET";

    public static final String NONE = "com.manuzid.systeminfowidget.NONE";

    public static PendingIntent preparePendingIntent(Context context, String reqAction, int appWidgetId, int reqCode, int flag) {
        Intent preparedIntent = new Intent();
        preparedIntent.setAction(reqAction);
        preparedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, reqCode, preparedIntent, flag);
    }


}