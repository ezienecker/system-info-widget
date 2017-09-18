package com.manuzid.systeminfowidget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.TimeZone;

import static com.manuzid.systeminfowidget.util.SystemInfoLib.AKKU;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.CAMERA;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.DISPLAY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.GENERAL;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.MEMORY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.MORE;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getBatteryHealthForUi;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getBatteryStatusForUi;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getBatteryTemp;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getBusyMemory;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getConnectedState;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getDeviceSize;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getFaceCamAvailable;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getFreeMemory;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getPercentForUi;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getPictureFormatForUI;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getPictureSizeForUI;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getPreviewFormats;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getPreviewSizes;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getScreenDps;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getScreenOrientation;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.getTotalMemory;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.preparePendingIntent;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.restoreViewInfo;

/**
 * Created by Emanuel Zienecker on 22.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class SysInfoMainProvider extends AppWidgetProvider {
    private static boolean isBatteryViewActive = false;
    private static boolean isGeneralViewActive = false;
    private static boolean isMoreViewActive = false;
    private static boolean isDisplayViewActive = false;
    private static boolean isCameraViewActive = false;
    private static boolean isMemoryViewActive = false;
    private SharedPreferences prefs;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = appWidgetIds.length; --i >= 0; ) {
            int appWidgetID = appWidgetIds[i];
            PendingIntent pendingDisplay = preparePendingIntent(context, DISPLAY, appWidgetID, 100, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingCamera = preparePendingIntent(context, CAMERA, appWidgetID, 101, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingGeneral = preparePendingIntent(context, GENERAL, appWidgetID, 102, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingMore = preparePendingIntent(context, MORE, appWidgetID, 103, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingMemory = preparePendingIntent(context, MEMORY, appWidgetID, 104, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingBattery = preparePendingIntent(context, AKKU, appWidgetID, 105, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intent = new Intent(context, ConfigPreferencesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(ConfigPreferencesActivity.class.getName());
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
            PendingIntent pendingConfig = PendingIntent.getActivity(context, 106, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String colorScheme;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            colorScheme = prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);
            remoteView.setOnClickPendingIntent(R.id.btnDisplay, pendingDisplay);
            remoteView.setOnClickPendingIntent(R.id.btnCamera, pendingCamera);
            remoteView.setOnClickPendingIntent(R.id.btnGeneral, pendingGeneral);
            remoteView.setOnClickPendingIntent(R.id.btnMore, pendingMore);
            remoteView.setOnClickPendingIntent(R.id.btnMemory, pendingMemory);
            remoteView.setOnClickPendingIntent(R.id.btnAkku, pendingBattery);
            remoteView.setOnClickPendingIntent(R.id.relaGeneral, pendingConfig);
            remoteView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorScheme)) {
                remoteView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
            intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

            context.getApplicationContext().registerReceiver(this, intentFilter);

            appWidgetManager.updateAppWidget(appWidgetID, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String colorScheme = prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);
        int appWidId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (DISPLAY.equals(intent.getAction())) {
            RemoteViews remoteView = handleDisplayInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (CAMERA.equals(intent.getAction())) {
            RemoteViews remoteView = handleCameraInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (GENERAL.equals(intent.getAction())) {
            RemoteViews remoteView = handleGeneralInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (MORE.equals(intent.getAction())) {
            RemoteViews remoteView = handleMoreInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (MEMORY.equals(intent.getAction())) {
            RemoteViews remoteView = handleMemoryInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (AKKU.equals(intent.getAction())) {
            RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 1, colorScheme, appWidId);
            updateAppWidget(context, remoteView);
        } else if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction()) || Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            if (isBatteryViewActive) {
                RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 2, colorScheme, appWidId);
                updateAppWidget(context, remoteView);
            }
        } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) || Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())
                || Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            if (isBatteryViewActive) {
                RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 0, colorScheme, appWidId);
                updateAppWidget(context, remoteView);
            }
        } else if (!intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
            if (isDisplayViewActive) {
                RemoteViews remoteView = handleDisplayInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
                updateAppWidget(context, remoteView);
            } else {
                super.onReceive(context, intent);
            }
        }
    }

    private RemoteViews handleGeneralInfo(final Context context, final int sysInfoMain, final String colorScheme, final int appWidId) {
        RemoteViews generalView = new RemoteViews(context.getPackageName(), sysInfoMain);

        if (isGeneralViewActive) {
            generalView = restoreViewDefault(generalView, context, appWidId);
            generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            isGeneralViewActive = false;
        } else {
            generalView = restoreViewInfo(generalView, context, appWidId);
            final Resources resources = context.getResources();
            generalView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.general_manufacturer));
            generalView.setTextViewText(R.id.txtManufacturer, android.os.Build.MANUFACTURER);
            generalView.setTextViewText(R.id.lblModell, resources.getString(R.string.general_modell));
            generalView.setTextViewText(R.id.txtModell, android.os.Build.MODEL);
            generalView.setTextViewText(R.id.lblProduct, resources.getString(R.string.general_product));
            generalView.setTextViewText(R.id.txtProduct, android.os.Build.PRODUCT);
            generalView.setTextViewText(R.id.lblBrand, resources.getString(R.string.general_brand));
            generalView.setTextViewText(R.id.txtBrand, android.os.Build.BRAND);
            generalView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.general_serialnumber));
            generalView.setTextViewText(R.id.txtSerialnumber, android.os.Build.SERIAL);
            generalView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            generalView.setTextViewText(R.id.lblDeviceId, resources.getString(R.string.general_device_id));
            generalView.setTextViewText(R.id.txtDeviceId, android.os.Build.ID);
            generalView.setTextViewText(R.id.lblTimeZone, resources.getString(R.string.general_timezone));
            generalView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            generalView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            generalView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            generalView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
            generalView.setViewVisibility(R.id.imgRestore, View.GONE);
            TimeZone timeZone = TimeZone.getDefault();
            generalView.setTextViewText(R.id.txtTimeZone, timeZone.getDisplayName(false, TimeZone.SHORT));

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_blue);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_red);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_purple);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_orange);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_green);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorScheme)) {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_black);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                generalView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn_pressed_blue);
                generalView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            generalView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            generalView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            generalView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            generalView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            generalView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            restoreActiveViews(1);
            isGeneralViewActive = true;
        }
        return generalView;
    }

    private RemoteViews handleMoreInfo(final Context context, final int sysinfoMain, final String colorShemeForMeth, final int appWidId) {
        RemoteViews moreView = new RemoteViews(context.getPackageName(), sysinfoMain);
        if (isMoreViewActive) {
            moreView = restoreViewDefault(moreView, context, appWidId);
            moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            isMoreViewActive = false;
        } else {
            final Resources resources = context.getResources();
            String androidid = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            moreView = restoreViewInfo(moreView, context, appWidId);
            moreView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.more_android_id));
            moreView.setTextViewText(R.id.txtManufacturer, androidid);
            moreView.setTextViewText(R.id.lblModell, resources.getString(R.string.more_os_sdk_version));
            moreView.setTextViewText(R.id.txtModell, android.os.Build.VERSION.RELEASE + "/" + android.os.Build.VERSION.SDK_INT);
            moreView.setTextViewText(R.id.lblProduct, resources.getString(R.string.more_board));
            moreView.setTextViewText(R.id.txtProduct, android.os.Build.BOARD);
            moreView.setTextViewText(R.id.lblBrand, resources.getString(R.string.more_bootloader));
            moreView.setTextViewText(R.id.txtBrand, android.os.Build.BOOTLOADER);
            moreView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.more_cpu_i));
            moreView.setTextViewText(R.id.txtSerialnumber, android.os.Build.CPU_ABI);
            moreView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            moreView.setTextViewText(R.id.lblDeviceId, resources.getString(R.string.more_cpu_ii));
            moreView.setTextViewText(R.id.txtDeviceId, android.os.Build.CPU_ABI2);
            moreView.setTextViewText(R.id.lblTimeZone, resources.getString(R.string.more_hardware));
            moreView.setTextViewText(R.id.txtTimeZone, android.os.Build.HARDWARE);
            moreView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            moreView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            moreView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            moreView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
            moreView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_blue);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_red);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_purple);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_orange);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_green);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_black);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                moreView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn_pressed_blue);
                moreView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            moreView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            moreView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            moreView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            moreView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            moreView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            restoreActiveViews(2);
            isMoreViewActive = true;
        }
        return moreView;
    }

    @TargetApi(13)
    @SuppressWarnings("deprecation")
    private RemoteViews handleDisplayInfo(final Context context, final int sysinfoMain, final String colorShemeForMeth, final int appWidId) {
        RemoteViews displayView = new RemoteViews(context.getPackageName(), sysinfoMain);
        if (isDisplayViewActive) {
            displayView = restoreViewDefault(displayView, context, appWidId);
            displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            isDisplayViewActive = false;
        } else {
            final Resources resources = context.getResources();
            int disHeight = 0;
            int disWidth = 0;
            Display display = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Point size = new Point();
                display.getSize(size);
                disWidth = size.x;
                disHeight = size.y;
            } else {
                disHeight = display.getHeight();
                disWidth = display.getWidth();
            }
            displayView = restoreViewInfo(displayView, context, appWidId);
            displayView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.display_display_size));
            displayView.setTextViewText(R.id.txtManufacturer, getDeviceSize(resources, display));
            displayView.setTextViewText(R.id.lblModell, resources.getString(R.string.display_height));
            displayView.setTextViewText(R.id.txtModell, "" + disHeight);
            displayView.setTextViewText(R.id.lblProduct, resources.getString(R.string.display_width));
            displayView.setTextViewText(R.id.txtProduct, "" + disWidth);
            displayView.setTextViewText(R.id.lblBrand, resources.getString(R.string.display_dps));
            displayView.setTextViewText(R.id.txtBrand, getScreenDps(context.getResources().getDisplayMetrics(), resources));
            displayView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.display_fps));
            displayView.setTextViewText(R.id.txtSerialnumber, "" + display.getRefreshRate());
            displayView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            displayView.setTextViewText(R.id.lblDeviceId, resources.getString(R.string.display_display_skala));
            displayView.setTextViewText(R.id.txtDeviceId, "" + context.getResources().getDisplayMetrics().scaledDensity);
            displayView.setTextViewText(R.id.lblTimeZone, resources.getString(R.string.display_orientation));
            displayView.setTextViewText(R.id.txtTimeZone, getScreenOrientation(resources.getConfiguration().orientation, resources));
            displayView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            displayView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            displayView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            displayView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
            displayView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_blue);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_red);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_purple);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_orange);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_green);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_black);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                displayView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn_pressed_black);
                displayView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            displayView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            displayView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            displayView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            displayView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            displayView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            restoreActiveViews(3);
            isDisplayViewActive = true;
        }
        return displayView;
    }

    private RemoteViews handleCameraInfo(final Context context, final int sysinfoMain, final String colorShemeForMeth, final int appWidId) {
        RemoteViews cameraView = new RemoteViews(context.getPackageName(), sysinfoMain);
        if (isCameraViewActive) {
            cameraView = restoreViewDefault(cameraView, context, appWidId);
            cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            isCameraViewActive = false;
        } else {
            PackageManager packageMan = context.getPackageManager();
            final Resources resources = context.getResources();
            Camera cam = Camera.open();
            String pictureFormat = resources.getString(R.string.general_unknow);
            String previewFormat = resources.getString(R.string.general_unknow);
            String pictureSize = resources.getString(R.string.general_unknow);
            String previewPictureSize = resources.getString(R.string.general_unknow);
            String supportedPictureSizes = resources.getString(R.string.general_unknow);
            String supportedPictureFormats = resources.getString(R.string.general_unknow);
            String facecamString = resources.getString(R.string.general_unknow);
            if (cam != null) {
                Parameters cameraParameters = cam.getParameters();
                if (cameraParameters != null) {
                    try {
                        pictureFormat = getPictureFormatForUI(cameraParameters.getPictureFormat(), resources);
                        pictureSize = getPictureSizeForUI(cameraParameters.getPictureSize());
                        previewFormat = getPictureFormatForUI(cameraParameters.getPreviewFormat(), resources);
                        previewPictureSize = getPictureSizeForUI(cameraParameters.getPreviewSize());
                        supportedPictureSizes = getPreviewSizes(cameraParameters.getSupportedPictureSizes());
                        supportedPictureFormats = getPreviewFormats(cameraParameters.getSupportedPreviewFormats(), resources);
                    } catch (NullPointerException e) {
                        pictureFormat = resources.getString(R.string.general_unknow);
                        previewFormat = resources.getString(R.string.general_unknow);
                        pictureSize = resources.getString(R.string.general_unknow);
                        previewPictureSize = resources.getString(R.string.general_unknow);
                        supportedPictureSizes = resources.getString(R.string.general_unknow);
                        supportedPictureFormats = resources.getString(R.string.general_unknow);
                    }
                }

                cam.release();
            }
            try {
                facecamString = getFaceCamAvailable(packageMan, resources);

            } catch (Exception e) {
                facecamString = resources.getString(R.string.general_unknow);
            }
            cameraView = restoreViewInfo(cameraView, context, appWidId);
            cameraView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.camera_manufacturer));
            cameraView.setTextViewText(R.id.txtManufacturer, pictureFormat);
            cameraView.setTextViewText(R.id.lblModell, resources.getString(R.string.camera_product));
            cameraView.setTextViewText(R.id.txtModell, pictureSize);
            cameraView.setTextViewText(R.id.lblProduct, resources.getString(R.string.camera_modell));
            cameraView.setTextViewText(R.id.txtProduct, previewFormat);
            cameraView.setTextViewText(R.id.lblBrand, resources.getString(R.string.camera_brand));
            cameraView.setTextViewText(R.id.txtBrand, previewPictureSize);
            cameraView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.camera_serialnumber));
            if (supportedPictureSizes.equals(resources.getString(R.string.general_unknow))) {
                cameraView.setTextViewText(R.id.txtSerialnumber, supportedPictureSizes);
                cameraView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            } else {
                cameraView.setTextViewText(R.id.txtSerialnumber, "");
                cameraView.setTextViewText(R.id.txtSupportedPictureSizes, supportedPictureSizes);
            }
            cameraView.setTextViewText(R.id.lblDeviceId, resources.getString(R.string.camera_supportet_formats));
            cameraView.setTextViewText(R.id.txtDeviceId, supportedPictureFormats);
            cameraView.setTextViewText(R.id.lblTimeZone, resources.getString(R.string.camera_timezone));
            cameraView.setTextViewText(R.id.txtTimeZone, facecamString);
            cameraView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            cameraView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            cameraView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            cameraView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
            cameraView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_blue);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_red);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_purple);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_orange);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_green);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_black);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                cameraView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn_pressed_blue);
                cameraView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            cameraView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            cameraView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            cameraView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            cameraView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            cameraView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            restoreActiveViews(4);
            isCameraViewActive = true;
        }
        return cameraView;
    }

    private RemoteViews handleMemoryInfo(final Context context, final int sysinfoMain, final String colorShemeForMeth, final int appWidId) {
        RemoteViews memoryView = new RemoteViews(context.getPackageName(), sysinfoMain);
        if (isMemoryViewActive) {
            memoryView = restoreViewDefault(memoryView, context, appWidId);
            memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            isMemoryViewActive = false;
        } else {
            final Resources resources = context.getResources();
            int deviceMem = getPercentForUi(true);
            int usbMem = getPercentForUi(false);

            String totalDeviceMem = getTotalMemory(true);
            String freeDeviceMem = getFreeMemory(true);
            String usedDeviceMem = getBusyMemory(true);

            memoryView = restoreViewInfo(memoryView, context, appWidId);
            memoryView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.memory_header_memory));
            memoryView.setTextViewText(R.id.txtManufacturer, resources.getString(R.string.memory_header_values));
            memoryView.setTextViewText(R.id.lblModell, resources.getString(R.string.memory_devicememory));
            memoryView.setTextViewText(R.id.txtModell, getTotalMemory(true) + getFreeMemory(true) + getBusyMemory(true));
            memoryView.setTextViewText(R.id.lblProduct, "");
            memoryView.setTextViewText(R.id.txtProduct, "");
            memoryView.setTextViewText(R.id.lblBrand, "");
            memoryView.setTextViewText(R.id.txtBrand, "");
            memoryView.setViewVisibility(R.id.devicememory_percent, View.VISIBLE);
            memoryView.setTextViewText(R.id.devicememory_percent, resources.getString(R.string.memory_used) + " " + deviceMem + "%");

            String totalMem = getTotalMemory(false);
            String freeMem = getFreeMemory(false);
            String usedMem = getBusyMemory(false);

            if ((totalMem.equals("") && freeMem.equals("") && usedMem.equals(""))
                    || (totalMem.equals(totalDeviceMem) && freeMem.equals(freeDeviceMem) && usedMem.equals(usedDeviceMem))) {
                memoryView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.memory_usb_sd_memory));
                memoryView.setTextViewText(R.id.txtSerialnumber, "");
                memoryView.setTextViewText(R.id.txtSupportedPictureSizes, "");
                memoryView.setTextViewText(R.id.lblDeviceId, "");
                memoryView.setTextViewText(R.id.txtDeviceId, "");
                memoryView.setTextViewText(R.id.lblTimeZone, "");
                memoryView.setTextViewText(R.id.txtTimeZone, "");
                memoryView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
                memoryView.setViewVisibility(R.id.usbmemory_percent, View.VISIBLE);
                memoryView.setTextViewText(R.id.usbmemory_percent, resources.getString(R.string.memory_not_available));
            } else {
                memoryView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.memory_usb_sd_memory));
                memoryView.setTextViewText(R.id.txtSerialnumber, getTotalMemory(false) + getFreeMemory(false) + getBusyMemory(false));
                memoryView.setTextViewText(R.id.txtSupportedPictureSizes, "");
                memoryView.setTextViewText(R.id.lblDeviceId, "");
                memoryView.setTextViewText(R.id.txtDeviceId, "");
                memoryView.setTextViewText(R.id.lblTimeZone, "");
                memoryView.setTextViewText(R.id.txtTimeZone, "");
                memoryView.setViewVisibility(R.id.usbmemory_percent, View.VISIBLE);
                memoryView.setTextViewText(R.id.usbmemory_percent, resources.getString(R.string.memory_used) + " " + usbMem + "%");
                memoryView.setViewVisibility(R.id.usbmemory_progressBar, View.VISIBLE);
                memoryView.setInt(R.id.usbmemory_progressBar, "setProgress", usbMem);
            }

            memoryView.setViewVisibility(R.id.devicememory_progressBar, View.VISIBLE);
            memoryView.setInt(R.id.devicememory_progressBar, "setProgress", deviceMem);
            memoryView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_blue);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_red);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_purple);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_orange);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_green);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_black);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                memoryView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn_pressed_blue);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            memoryView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            memoryView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            memoryView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            memoryView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            memoryView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            restoreActiveViews(5);
            isMemoryViewActive = true;
        }
        return memoryView;
    }

    private RemoteViews handleAkkuInfo(final Context context, final int sysinfoMain, Intent intent, final int fromButton,
                                       final String colorShemeForMeth, final int appWidId) {
        RemoteViews akkuView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);

        if (isBatteryViewActive && fromButton == 1) {
            akkuView = restoreViewDefault(akkuView, context, appWidId);
            akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn);
            isBatteryViewActive = false;
        } else {
            final Resources resources = context.getResources();
            if (fromButton == 1 || fromButton == 2) {
                intent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            String batteryStatus = getBatteryStatusForUi(status, resources);

            String connectedString = getConnectedState(resources, intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0));

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            String batteryHealth = getBatteryHealthForUi(health, resources);

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            String batteryTemp = getBatteryTemp(temp, resources, context, prefs);

            akkuView = restoreViewInfo(akkuView, context, appWidId);
            akkuView.setTextViewText(R.id.lblManufacturer, resources.getString(R.string.akku_capacitance));
            akkuView.setTextViewText(R.id.txtManufacturer, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            akkuView.setTextViewText(R.id.lblModell, resources.getString(R.string.akku_state));
            akkuView.setTextViewText(R.id.txtModell, batteryStatus);
            akkuView.setTextViewText(R.id.lblProduct, resources.getString(R.string.akku_technology));
            try {
                akkuView.setTextViewText(R.id.txtProduct, intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));

            } catch (Exception e) {
                akkuView.setTextViewText(R.id.txtProduct, resources.getString(R.string.akku_technology_summ));
            }
            akkuView.setTextViewText(R.id.lblBrand, resources.getString(R.string.akku_voltage));
            akkuView.setTextViewText(R.id.txtBrand, intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) + " mV");
            akkuView.setTextViewText(R.id.lblSerialNumber, resources.getString(R.string.akku_temp));
            akkuView.setTextViewText(R.id.txtSerialnumber, batteryTemp);
            akkuView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            akkuView.setTextViewText(R.id.lblDeviceId, resources.getString(R.string.akku_connected));
            akkuView.setTextViewText(R.id.txtDeviceId, connectedString);
            akkuView.setTextViewText(R.id.lblTimeZone, resources.getString(R.string.akku_akku_health));
            akkuView.setTextViewText(R.id.txtTimeZone, batteryHealth);
            akkuView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            akkuView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            akkuView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            akkuView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_blue);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_red);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_purple);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_orange);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_green);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_black);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                akkuView.setInt(R.id.btnAkku, "setBackgroundResource", R.drawable.akku_btn_pressed_blue);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            akkuView.setInt(R.id.btnGeneral, "setBackgroundResource", R.drawable.general_btn);
            akkuView.setInt(R.id.btnMore, "setBackgroundResource", R.drawable.more_btn);
            akkuView.setInt(R.id.btnDisplay, "setBackgroundResource", R.drawable.display_btn);
            akkuView.setInt(R.id.btnCamera, "setBackgroundResource", R.drawable.camera_btn);
            akkuView.setInt(R.id.btnMemory, "setBackgroundResource", R.drawable.memory_btn);
            restoreActiveViews(6);
        }
        return akkuView;
    }

    private void updateAppWidget(final Context context, final RemoteViews remoteView) {
        ComponentName myComponentName = new ComponentName(context, SysInfoMainProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myComponentName, remoteView);
    }

    private void restoreActiveViews(final int view) {
        switch (view) {
            case 1:
                isGeneralViewActive = true;
                isMoreViewActive = false;
                isDisplayViewActive = false;
                isCameraViewActive = false;
                isMemoryViewActive = false;
                isBatteryViewActive = false;
                break;
            case 2:
                isGeneralViewActive = false;
                isMoreViewActive = true;
                isDisplayViewActive = false;
                isCameraViewActive = false;
                isMemoryViewActive = false;
                isBatteryViewActive = false;
                break;
            case 3:
                isGeneralViewActive = false;
                isMoreViewActive = false;
                isDisplayViewActive = true;
                isCameraViewActive = false;
                isMemoryViewActive = false;
                isBatteryViewActive = false;
                break;
            case 4:
                isGeneralViewActive = false;
                isMoreViewActive = false;
                isDisplayViewActive = false;
                isCameraViewActive = true;
                isMemoryViewActive = false;
                isBatteryViewActive = false;
                break;
            case 5:
                isGeneralViewActive = false;
                isMoreViewActive = false;
                isDisplayViewActive = false;
                isCameraViewActive = false;
                isMemoryViewActive = true;
                isBatteryViewActive = false;
                break;
            case 6:
                isGeneralViewActive = false;
                isMoreViewActive = false;
                isDisplayViewActive = false;
                isCameraViewActive = false;
                isMemoryViewActive = false;
                isBatteryViewActive = true;
                break;
        }
    }

    private RemoteViews restoreViewDefault(RemoteViews rV, final Context context, final int appWidgetID) {
        rV.setViewVisibility(R.id.lblManufacturer, View.GONE);
        rV.setViewVisibility(R.id.txtManufacturer, View.GONE);
        rV.setViewVisibility(R.id.lblModell, View.GONE);
        rV.setViewVisibility(R.id.txtModell, View.GONE);
        rV.setViewVisibility(R.id.lblProduct, View.GONE);
        rV.setViewVisibility(R.id.txtProduct, View.GONE);
        rV.setViewVisibility(R.id.lblBrand, View.GONE);
        rV.setViewVisibility(R.id.txtBrand, View.GONE);
        rV.setViewVisibility(R.id.lblSerialNumber, View.GONE);
        rV.setViewVisibility(R.id.txtSerialnumber, View.GONE);
        rV.setViewVisibility(R.id.txtSupportedPictureSizes, View.GONE);
        rV.setViewVisibility(R.id.lblDeviceId, View.GONE);
        rV.setViewVisibility(R.id.txtDeviceId, View.GONE);
        rV.setViewVisibility(R.id.lblTimeZone, View.GONE);
        rV.setViewVisibility(R.id.txtTimeZone, View.GONE);
        rV.setViewVisibility(R.id.devicememory_percent, View.GONE);
        rV.setViewVisibility(R.id.usbmemory_percent, View.GONE);
        rV.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
        rV.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
        rV.setViewVisibility(R.id.imgRestore, View.VISIBLE);
        rV.setViewVisibility(R.id.txtConfigClick, View.VISIBLE);

        PendingIntent pendingDisplay = preparePendingIntent(context, DISPLAY, appWidgetID, 100, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingCamera = preparePendingIntent(context, CAMERA, appWidgetID, 101, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingGeneral = preparePendingIntent(context, GENERAL, appWidgetID, 102, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingMore = preparePendingIntent(context, MORE, appWidgetID, 103, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingMemory = preparePendingIntent(context, MEMORY, appWidgetID, 104, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingAkku = preparePendingIntent(context, AKKU, appWidgetID, 105, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(context, ConfigPreferencesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(ConfigPreferencesActivity.class.getName());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        PendingIntent pendingConfig = PendingIntent.getActivity(context, 106, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        rV.setOnClickPendingIntent(R.id.btnDisplay, pendingDisplay);
        rV.setOnClickPendingIntent(R.id.btnCamera, pendingCamera);
        rV.setOnClickPendingIntent(R.id.btnGeneral, pendingGeneral);
        rV.setOnClickPendingIntent(R.id.btnMore, pendingMore);
        rV.setOnClickPendingIntent(R.id.btnMemory, pendingMemory);
        rV.setOnClickPendingIntent(R.id.btnAkku, pendingAkku);
        rV.setOnClickPendingIntent(R.id.relaGeneral, pendingConfig);

        return rV;
    }

    public static void updateAppWidget(AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews remoteView) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

}