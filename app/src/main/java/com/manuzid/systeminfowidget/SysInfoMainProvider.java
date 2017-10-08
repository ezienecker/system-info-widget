package com.manuzid.systeminfowidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.category.AbstractCategory;
import com.manuzid.systeminfowidget.category.BatteryCategory;
import com.manuzid.systeminfowidget.category.CameraCategory;
import com.manuzid.systeminfowidget.category.DisplayCategory;
import com.manuzid.systeminfowidget.category.GeneralCategory;
import com.manuzid.systeminfowidget.category.MemoryCategory;
import com.manuzid.systeminfowidget.category.MoreCategory;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.manuzid.systeminfowidget.Constants.BACKGROUND_RESOURCE_METHOD_NAME;
import static com.manuzid.systeminfowidget.category.AbstractCategory.NONE;
import static com.manuzid.systeminfowidget.category.BatteryCategory.BATTERY;
import static com.manuzid.systeminfowidget.category.CameraCategory.CAMERA;
import static com.manuzid.systeminfowidget.category.DisplayCategory.DISPLAY;
import static com.manuzid.systeminfowidget.category.GeneralCategory.GENERAL;
import static com.manuzid.systeminfowidget.category.MemoryCategory.MEMORY;
import static com.manuzid.systeminfowidget.category.MoreCategory.MORE;

/**
 * Created by Emanuel Zienecker on 22.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class SysInfoMainProvider extends AppWidgetProvider {

    /**
     * Liste der Beobachter
     */
    private static Map<String, AbstractCategory> observerMap = new LinkedHashMap<>();

    static {
        observerMap.put(GENERAL, new GeneralCategory());
        observerMap.put(MORE, new MoreCategory());
        observerMap.put(DISPLAY, new DisplayCategory());
        observerMap.put(CAMERA, new CameraCategory());
        observerMap.put(MEMORY, new MemoryCategory());
        observerMap.put(BATTERY, new BatteryCategory());
    }

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
    private static String category = NONE;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = appWidgetIds.length; --i >= 0; ) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);

            // 1 PendingIntent damit wenn der Benutzer die Buttons benutzt auch zwischen den Kategorien navigieren kann.
            registerOnClickPendingIntentForCategories(context, remoteView, appWidgetId);

            // 2 Registrieren der Einstellungs-Activity (Wird aktiviert wenn der Benutzer auf das Widget klickt).
            registerOnClickPendingIntentForSettingsActivity(context, remoteView, appWidgetId);

            // 3 Registrieren des IntentFilter um auf Änderungen die die Batterie betreffen reagieren zu können.
            context.getApplicationContext().registerReceiver(this, intentFilter);

            // 4 Wiederherstellen der Standard-Ansicht
            restoreRemoteViewBackground(context, remoteView);

            // 5 Setzt die RemoteView zu den Widget mit der entsprechenden appWidgetId
            appWidgetManager.updateAppWidget(appWidgetId, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String colorScheme = getStringColorSchemeFromConfiguration(context);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        final String intentAction = intent.getAction();
        final AbstractCategory category = observerMap.get(intentAction);

        if (category != null) {
            updateAppWidget(context, handleRemoteViews(context, colorScheme, category, appWidgetId));
        } else if (isIntentActionFromWidgetThatNeedAction(intentAction)) {
            super.onReceive(context, intent);
        } else {
            // TODO: Wie kann man das verbessern? Was ist mit den boolschen Werten?
            // Beim platzieren des Widget kommt folgende IntentAction: android.appwidget.action.APPWIDGET_ENABLED
            // Danach kommt folgende IntentAction: android.appwidget.action.APPWIDGET_UPDATE
            /*
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction()) ||
            Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                if (BATTERY.equals(category))
                if (isBatteryViewActive) {
                    RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 2, colorScheme, appWidId);
                    updateAppWidget(context, remoteView);
                }
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) ||
            Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())
                    || Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
                if (BATTERY.equals(category))
                if (isBatteryViewActive) {
                    RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 0, colorScheme, appWidId);
                    updateAppWidget(context, remoteView);
                }
            } else if (!intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
                if (DISPLAY.equals(category))
                if (isDisplayViewActive) {
                    RemoteViews remoteView = handleDisplayInfo(context, R.layout.sysinfo_main, colorScheme, appWidId);
                    updateAppWidget(context, remoteView);
                } else {
                    super.onReceive(context, intent);
                }
            }
            */
        }
    }

    private void registerOnClickPendingIntentForCategories(Context context, RemoteViews remoteView, int appWidgetId) {
        List<Integer> buttonIds = Arrays.asList(R.id.btnFirst, R.id.btnSecond,
                R.id.btnThird, R.id.btnFourth, R.id.btnFifth, R.id.btnSixth);
        int buttonCounter = 0;

        for (Map.Entry<String, AbstractCategory> entry : observerMap.entrySet()) {
            if (buttonCounter < buttonIds.size()) {
                final AbstractCategory category = entry.getValue();

                // 1 Den Button das entsprechende PendingIntent zuweisen
                remoteView.setOnClickPendingIntent(category.getButtonId(),
                        preparePendingIntent(context, category.getRequestAction(), appWidgetId, category.getRequestCode()));

                // 2 Kategorie-Drawable für den Button setzen
                remoteView.setInt(category.getButtonId(), BACKGROUND_RESOURCE_METHOD_NAME,
                        category.getDefaultButtonDrawable());

                buttonCounter++;
            }
        }
    }

    private void registerOnClickPendingIntentForSettingsActivity(Context context, RemoteViews remoteView, int appWidgetId) {
        Intent intent = new Intent(context, ConfigPreferencesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(ConfigPreferencesActivity.class.getName());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingConfig = PendingIntent.getActivity(context, 106, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteView.setOnClickPendingIntent(R.id.relative_general, pendingConfig);
    }

    private PendingIntent preparePendingIntent(Context context, String reqAction, int appWidgetId, int reqCode) {
        Intent preparedIntent = new Intent();
        preparedIntent.setAction(reqAction);
        preparedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, reqCode, preparedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private RemoteViews handleRemoteViews(Context context, String colorScheme, AbstractCategory lCategory, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);
        if (category.equals(lCategory.getRequestAction())) {
            // 1 Keine Kategorie wird angezeigt, die Standard-View wird angezeigt.
            category = NONE;

            // 2 Button der aktuellen Kategorie zurücksetzen.
            restoreAllButtonBackgroundResource(remoteViews);

            // 3 Zurücksetzen und Zurückgeben der Standard-View.
            return restoreStandardView(context, remoteViews, appWidgetId);
        } else {
            // 1 View wiederherstellen damit die Informationen sichtbar werden.
            remoteViews = restoreInformationView(context, remoteViews, appWidgetId);

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

    private void updateAppWidget(final Context context, final RemoteViews remoteView) {
        ComponentName myComponentName = new ComponentName(context, SysInfoMainProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myComponentName, remoteView);
    }

    /**
     * Stellt die Informations-Ansicht wieder her. Labels und Text-Felder werden sichtbar.
     *
     * @param context     {@link Context}
     * @param remoteViews {@link RemoteViews} die Wiederhergestellt wird.
     * @param appWidgetId id des Widget welches aktualisiert werden soll
     * @return Wiederhergestellte {@link RemoteViews}
     */
    private RemoteViews restoreInformationView(Context context, RemoteViews remoteViews, int appWidgetId) {
        return resetViewElements(context, remoteViews, View.VISIBLE, View.GONE, appWidgetId);
    }

    /**
     * Stellt die Standard-View wieder her. Alle Labels und Text-Felder werden "versteckt" und
     * das App-Logo wird angezeigt.
     *
     * @param context     {@link Context}
     * @param remoteViews {@link RemoteViews} die Wiederhergestellt wird.
     * @param appWidgetId id des Widget welches aktualisiert werden soll
     * @return Wiederhergestellte {@link RemoteViews}
     */
    private RemoteViews restoreStandardView(Context context, RemoteViews remoteViews, int appWidgetId) {
        return resetViewElements(context, remoteViews, View.GONE, View.VISIBLE, appWidgetId);
    }

    /**
     * Wiederherstellen des entsprechenden View-Zustands.
     *
     * @param context                   {@link Context}
     * @param remoteViews               {@link RemoteViews}
     * @param infoElementVisibility     entweder {@link View#GONE} oder {@link View#VISIBLE}
     * @param standardElementVisibility entweder {@link View#GONE} oder {@link View#VISIBLE}
     * @param appWidgetId               id des Widget welches aktualisiert werden soll
     * @return {@link RemoteViews}
     */
    private RemoteViews resetViewElements(Context context, RemoteViews remoteViews, int infoElementVisibility, int standardElementVisibility, int appWidgetId) {
        // 1 Wiederherstellen der View-Elemente
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
        remoteViews.setViewVisibility(R.id.device_memory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usb_memory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.device_memory_progress_bar, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usb_memory_progress_bar, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.imgRestore, standardElementVisibility);
        remoteViews.setViewVisibility(R.id.txtConfigClick, standardElementVisibility);

        // 2 PendingIntent damit wenn der Benutzer die Buttons benutzt auch zwischen den Kategorien navigieren kann.
        registerOnClickPendingIntentForCategories(context, remoteViews, appWidgetId);

        // 3 Registrieren der Einstellungs-Activity (Wird aktiviert wenn der Benutzer auf das Widget klickt).
        registerOnClickPendingIntentForSettingsActivity(context, remoteViews, appWidgetId);

        return remoteViews;
    }

    private void restoreRemoteViewBackground(Context context, RemoteViews remoteView) {
        String colorScheme = getStringColorSchemeFromConfiguration(context);
        int relativeBackgroundDrawable = 0;

        switch (colorScheme) {
            case ConfigPreferencesActivity.COLOR_BLUE:
                relativeBackgroundDrawable = R.drawable.relative_background_blue;
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

        remoteView.setViewVisibility(R.id.relative_general, View.VISIBLE);
        remoteView.setInt(R.id.relative_general, BACKGROUND_RESOURCE_METHOD_NAME, relativeBackgroundDrawable);
    }

    @NonNull
    private String getStringColorSchemeFromConfiguration(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);
    }

    private boolean isIntentActionFromWidgetThatNeedAction(String intentAction) {
        return AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(intentAction) ||
                AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intentAction);
    }

    /**
     * Damit aus den Einstellungen heraus die Änderungen auch wirksam werden (Triggern von den LifeCycle-Methods).
     *
     * @param appWidgetManager {@link AppWidgetManager}
     * @param appWidgetId      id des entsprechenden Widgets welches aktualisiert werden soll
     * @param remoteView       {@link RemoteViews}
     */
    public static void updateAppWidget(AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews remoteView) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
    }

}