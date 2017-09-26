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
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.category.AbstractCategory;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.manuzid.systeminfowidget.Constants.BACKGROUND_RESOURCE_METHOD_NAME;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.BATTERY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.NONE;
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

/**
 * Created by Emanuel Zienecker on 22.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class SysInfoMainProvider extends AppWidgetProvider {

    /**
     * Liste der Beobachter
     */
    private Map<String, AbstractCategory> observerMap = new LinkedHashMap<>();

    /**
     * IntentFilter um auf Änderungen die von der Batterie kommen reagieren zu können
     */
    private static final IntentFilter intentFilter = new IntentFilter();

    static {
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    }

    /**
     * Die zuletzt aktivierte Kategorie
     */
    private static String category;

    private SharedPreferences preferences;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        initObserverMap();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        // TODO: Muss ich die Observer aus der Map löschen?
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // TODO: Muss ich die Observer aus der Map löschen?
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = appWidgetIds.length; --i >= 0; ) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);

            // 1 PendingIntent damit wenn der Benutzer die Buttons benutzt auch zwischen den Kategorien navigieren kann.
            List<Integer> buttonIds = Arrays.asList(R.id.btnOne, R.id.btnTwo,
                    R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix);
            int buttonCounter = 0;

            for (Map.Entry<String, AbstractCategory> entry : observerMap.entrySet()) {
                if (buttonCounter < buttonIds.size()) {
                    final AbstractCategory category = entry.getValue();
                    // 1.1 Den Button das entsprechende PendingIntent zuweisen
                    remoteView.setOnClickPendingIntent(buttonIds.get(buttonCounter),
                            preparePendingIntent(context, category.getRequestAction(), appWidgetId, category.getRequestCode(),
                                    PendingIntent.FLAG_UPDATE_CURRENT));

                    // 1.2 Kategorie-Drawable für den Button setzen
                    remoteView.setInt(buttonIds.get(buttonCounter), BACKGROUND_RESOURCE_METHOD_NAME,
                            category.getDefaultButtonDrawable());

                    buttonCounter++;
                }
            }

            // 2 Registrieren der Einstellungs-Activity (Wird aktiviert wenn der Benutzer auf das Widget klickt).
            Intent intent = new Intent(context, ConfigPreferencesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(ConfigPreferencesActivity.class.getName());
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingConfig = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 2.1 An dieser Stelle werden der RemoteView der Einstellungs-PendingIntent zugewiesen.
            remoteView.setOnClickPendingIntent(R.id.relaGeneral, pendingConfig);

            // 3 Prüfen ob sich die Batterie-Kategorie mit unter der Auswahl befindet um den IntentFilter
            // zu registrieren oder eben abzumelden.
            if (observerMap.get(BATTERY) != null) {
                // 3.1 Registrieren des IntentFilter um auf Änderungen die die Batterie betreffen reagieren zu können.
                context.getApplicationContext().registerReceiver(this, intentFilter);
            }
            else {
                context.getApplicationContext().unregisterReceiver(this);
            }

            // 4 Wiederherstellen der Standard-Ansicht
            restoreRemoteViewBackground(context, remoteView);

            // 5 Setzt die RemoteView zu den Widget mit der entsprechenden appWidgetId
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String colorScheme = getStringColorSchemeFromConfiguration(context);
        final int appWidId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        final String intentAction = intent.getAction();
        final AbstractCategory category = observerMap.get(intentAction);

        if (category != null) {
            updateAppWidget(context, handleRemoteViews(context, colorScheme, category));
        } else {
            // TODO: Wie kann man das verbessern? Was ist mit den boolschen Werten?
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction()) || Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
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
    }


    private RemoteViews handleRemoteViews(Context context, String colorScheme, AbstractCategory lCategory) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);
        if (category.equals(lCategory.getRequestAction())) {
            // 1 Keine Kategorie wird angezeigt, die Standard-View wird angezeigt.
            category = NONE;

            // 2 Button der aktuellen Kategorie zurücksetzen.
            restoreAllButtonBackgroundResource(remoteViews);

            // 3 Zurücksetzen und Zurückgeben der Standard-View.
            return restoreStandardView(remoteViews);
        } else {
            // 1 View wiederherstellen damit die Informationen sichtbar werden.
            remoteViews = restoreInformationView(remoteViews);

            // 2 Informationen-View vorbereiten.
            remoteViews = lCategory.prepareRemoteView(remoteViews, context);

            // 3 Default-Status der Buttons setzen.
            restoreAllButtonBackgroundResource(remoteViews);

            // 4 Aktiv-Status des Buttons setzen, abhänig vom Farbschema.
            lCategory.activateButtonBackgroundResource(remoteViews, colorScheme);

            // 5 Aktiv-Status des Hintergrund setzen, abhänig vom Farbschema.
            restoreRemoteViewBackground(context, remoteViews);

            // 6 Aktive Kategorie festlegen.
            category = lCategory.getRequestAction();
            return remoteViews;
        }
    }

    private void restoreAllButtonBackgroundResource(RemoteViews remoteViews) {
        for (Map.Entry<String, AbstractCategory> entry : observerMap.entrySet()) {
            entry.getValue().restoreButtonBackgroundResource(remoteViews);
        }
    }



    private RemoteViews handleMemoryInfo(final Context context, final int sysinfoMain, final String colorShemeForMeth, final int appWidId) {
        RemoteViews memoryView = new RemoteViews(context.getPackageName(), sysinfoMain);
        if (isMemoryViewActive) {
            memoryView = restoreViewDefault(memoryView, context, appWidId);
            memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn);
            isMemoryViewActive = false;
        } else {
            final Resources resources = context.getResources();
            int deviceMem = getPercentForUi(true);
            int usbMem = getPercentForUi(false);

            String totalDeviceMem = getTotalMemory(true);
            String freeDeviceMem = getFreeMemory(true);
            String usedDeviceMem = getBusyMemory(true);

            memoryView = restoreViewInfo(memoryView, context, appWidId);
            memoryView.setTextViewText(R.id.lblFirstInfo, resources.getString(R.string.memory_header_memory));
            memoryView.setTextViewText(R.id.txtFirstInfo, resources.getString(R.string.memory_header_values));
            memoryView.setTextViewText(R.id.lblSecondInfo, resources.getString(R.string.memory_devicememory));
            memoryView.setTextViewText(R.id.txtSecondInfo, getTotalMemory(true) + getFreeMemory(true) + getBusyMemory(true));
            memoryView.setTextViewText(R.id.lblThird, "");
            memoryView.setTextViewText(R.id.txtThird, "");
            memoryView.setTextViewText(R.id.lblFourth, "");
            memoryView.setTextViewText(R.id.txtFourth, "");
            memoryView.setViewVisibility(R.id.devicememory_percent, View.VISIBLE);
            memoryView.setTextViewText(R.id.devicememory_percent, resources.getString(R.string.memory_used) + " " + deviceMem + "%");

            String totalMem = getTotalMemory(false);
            String freeMem = getFreeMemory(false);
            String usedMem = getBusyMemory(false);

            if ((totalMem.equals("") && freeMem.equals("") && usedMem.equals(""))
                    || (totalMem.equals(totalDeviceMem) && freeMem.equals(freeDeviceMem) && usedMem.equals(usedDeviceMem))) {
                memoryView.setTextViewText(R.id.lblFifth, resources.getString(R.string.memory_usb_sd_memory));
                memoryView.setTextViewText(R.id.txtFifth, "");
                memoryView.setTextViewText(R.id.txtSupportedPictureSizes, "");
                memoryView.setTextViewText(R.id.lblSixth, "");
                memoryView.setTextViewText(R.id.txtSixth, "");
                memoryView.setTextViewText(R.id.lblSeventh, "");
                memoryView.setTextViewText(R.id.txtSeventh, "");
                memoryView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
                memoryView.setViewVisibility(R.id.usbmemory_percent, View.VISIBLE);
                memoryView.setTextViewText(R.id.usbmemory_percent, resources.getString(R.string.memory_not_available));
            } else {
                memoryView.setTextViewText(R.id.lblFifth, resources.getString(R.string.memory_usb_sd_memory));
                memoryView.setTextViewText(R.id.txtFifth, getTotalMemory(false) + getFreeMemory(false) + getBusyMemory(false));
                memoryView.setTextViewText(R.id.txtSupportedPictureSizes, "");
                memoryView.setTextViewText(R.id.lblSixth, "");
                memoryView.setTextViewText(R.id.txtSixth, "");
                memoryView.setTextViewText(R.id.lblSeventh, "");
                memoryView.setTextViewText(R.id.txtSeventh, "");
                memoryView.setViewVisibility(R.id.usbmemory_percent, View.VISIBLE);
                memoryView.setTextViewText(R.id.usbmemory_percent, resources.getString(R.string.memory_used) + " " + usbMem + "%");
                memoryView.setViewVisibility(R.id.usbmemory_progressBar, View.VISIBLE);
                memoryView.setInt(R.id.usbmemory_progressBar, "setProgress", usbMem);
            }

            memoryView.setViewVisibility(R.id.devicememory_progressBar, View.VISIBLE);
            memoryView.setInt(R.id.devicememory_progressBar, "setProgress", deviceMem);
            memoryView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_blue);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_red);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_purple);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_orange);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_green);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_black);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                memoryView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn_pressed_blue);
                memoryView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            //-- Restore View
            memoryView.setInt(R.id.btnThree, "setBackgroundResource", R.drawable.general_btn);
            memoryView.setInt(R.id.btnFour, "setBackgroundResource", R.drawable.more_btn);
            memoryView.setInt(R.id.btnOne, "setBackgroundResource", R.drawable.display_btn);
            memoryView.setInt(R.id.btnTwo, "setBackgroundResource", R.drawable.camera_btn);
            memoryView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn);
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
            akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn);
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
            String batteryTemp = getBatteryTemp(temp, resources, context, preferences);

            akkuView = restoreViewInfo(akkuView, context, appWidId);
            akkuView.setTextViewText(R.id.lblFirstInfo, resources.getString(R.string.akku_capacitance));
            akkuView.setTextViewText(R.id.txtFirstInfo, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            akkuView.setTextViewText(R.id.lblSecondInfo, resources.getString(R.string.akku_state));
            akkuView.setTextViewText(R.id.txtSecondInfo, batteryStatus);
            akkuView.setTextViewText(R.id.lblThird, resources.getString(R.string.akku_technology));
            try {
                akkuView.setTextViewText(R.id.txtThird, intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));

            } catch (Exception e) {
                akkuView.setTextViewText(R.id.txtThird, resources.getString(R.string.akku_technology_summ));
            }
            akkuView.setTextViewText(R.id.lblFourth, resources.getString(R.string.akku_voltage));
            akkuView.setTextViewText(R.id.txtFourth, intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) + " mV");
            akkuView.setTextViewText(R.id.lblFifth, resources.getString(R.string.akku_temp));
            akkuView.setTextViewText(R.id.txtFifth, batteryTemp);
            akkuView.setTextViewText(R.id.txtSupportedPictureSizes, "");
            akkuView.setTextViewText(R.id.lblSixth, resources.getString(R.string.akku_connected));
            akkuView.setTextViewText(R.id.txtSixth, connectedString);
            akkuView.setTextViewText(R.id.lblSeventh, resources.getString(R.string.akku_akku_health));
            akkuView.setTextViewText(R.id.txtSeventh, batteryHealth);
            akkuView.setViewVisibility(R.id.devicememory_percent, View.GONE);
            akkuView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
            akkuView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
            akkuView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);

            if (ConfigPreferencesActivity.COLOR_BLUE.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_blue);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            } else if (ConfigPreferencesActivity.COLOR_RED.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_red);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_red);
            } else if (ConfigPreferencesActivity.COLOR_LILA.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_purple);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_purple);
            } else if (ConfigPreferencesActivity.COLOR_ORANGE.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_orange);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_orange);
            } else if (ConfigPreferencesActivity.COLOR_GREEN.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_green);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_green);
            } else if (ConfigPreferencesActivity.COLOR_BLACK.equals(colorShemeForMeth)) {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_black);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_black);
            } else {
                akkuView.setInt(R.id.btnSix, "setBackgroundResource", R.drawable.akku_btn_pressed_blue);
                akkuView.setInt(R.id.relaGeneral, "setBackgroundResource", R.drawable.rela_background_blue);
            }

            akkuView.setInt(R.id.btnThree, "setBackgroundResource", R.drawable.general_btn);
            akkuView.setInt(R.id.btnFour, "setBackgroundResource", R.drawable.more_btn);
            akkuView.setInt(R.id.btnOne, "setBackgroundResource", R.drawable.display_btn);
            akkuView.setInt(R.id.btnTwo, "setBackgroundResource", R.drawable.camera_btn);
            akkuView.setInt(R.id.btnFive, "setBackgroundResource", R.drawable.memory_btn);
            restoreActiveViews(6);
        }
        return akkuView;
    }

    private void updateAppWidget(final Context context, final RemoteViews remoteView) {
        ComponentName myComponentName = new ComponentName(context, SysInfoMainProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myComponentName, remoteView);
    }

    /**
     * Stellt die Informations-Ansicht wieder her. Labels und Text-Felder werden sichtbar.
     *
     * @param remoteViews {@link RemoteViews} die Wiederhergestellt wird.
     * @return Wiederhergestellte {@link RemoteViews}
     */
    private RemoteViews restoreInformationView(RemoteViews remoteViews) {
        return resetViewElements(remoteViews, View.VISIBLE, View.GONE);
    }

    /**
     * Stellt die Standard-View wieder her. Alle Labels und Text-Felder werden "versteckt" und
     * das App-Logo wird angezeigt.
     *
     * @param remoteViews {@link RemoteViews} die Wiederhergestellt wird.
     * @return Wiederhergestellte {@link RemoteViews}
     */
    private RemoteViews restoreStandardView(RemoteViews remoteViews) {
        return resetViewElements(remoteViews, View.GONE, View.VISIBLE);
    }

    private RemoteViews resetViewElements(RemoteViews remoteViews, int infoElementVisibility, int standardElementVisibility) {
        remoteViews.setViewVisibility(R.id.lblFirstInfo, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtFirstInfo, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblSecondInfo, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtSecondInfo, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblThird, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtThird, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblFourth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtFourth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblFifth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtFifth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtSupportedPictureSizes, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblSixth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtSixth, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.lblSeventh, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.txtSeventh, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.devicememory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usbmemory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.devicememory_progressBar, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usbmemory_progressBar, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.imgRestore, standardElementVisibility);
        remoteViews.setViewVisibility(R.id.txtConfigClick, standardElementVisibility);
        return remoteViews;
    }

    // TODO: Schauen ob man hierauf verzichten kann
    public static void updateAppWidget(AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews remoteView) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
    }

    private void restoreRemoteViewBackground(Context context, RemoteViews remoteView) {
        String colorScheme = getStringColorSchemeFromConfiguration(context);
        int relativeBackgroundDrawable = 0;

        switch (colorScheme) {
            case ConfigPreferencesActivity.COLOR_BLUE:
                relativeBackgroundDrawable = R.drawable.rela_background_blue;
                break;
            case ConfigPreferencesActivity.COLOR_RED:
                relativeBackgroundDrawable = R.drawable.rela_background_red;
                break;
            case ConfigPreferencesActivity.COLOR_ORANGE:
                relativeBackgroundDrawable = R.drawable.rela_background_orange;
                break;
            case ConfigPreferencesActivity.COLOR_LILA:
                relativeBackgroundDrawable = R.drawable.rela_background_purple;
                break;
            case ConfigPreferencesActivity.COLOR_GREEN:
                relativeBackgroundDrawable = R.drawable.rela_background_green;
                break;
            case ConfigPreferencesActivity.COLOR_BLACK:
                relativeBackgroundDrawable = R.drawable.rela_background_black;
                break;
        }

        remoteView.setViewVisibility(R.id.relaGeneral, View.VISIBLE);
        remoteView.setInt(R.id.relaGeneral, BACKGROUND_RESOURCE_METHOD_NAME, relativeBackgroundDrawable);
    }

    @NonNull
    private String getStringColorSchemeFromConfiguration(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);
    }

    /**
     * Fügt der Liste der Beobachter einen Observer hinzu
     *
     * @param beobachter - Der Beobachter, der der Liste hinzugefügt werden soll
     */
    public void addObserver(AbstractCategory beobachter) {
        // Hier muss auch die Button-ID erfasst werden
        observerMap.put(beobachter.getRequestAction(), beobachter);
    }

    /**
     * Löscht einen Observer aus der Liste
     *
     * @param observer Der Beobachter, der aus der Liste gelöscht werden soll
     */
    public void removeObserver(AbstractCategory observer) {
        observerMap.remove(observer.getRequestAction());
    }

    // TODO: Implement me
    private void initObserverMap() {
        // 1. Welche Observer sind in den Preferences gesetzt?

        // 2. Diese registrieren
    }

}