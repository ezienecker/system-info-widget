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
import android.util.SparseIntArray;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.manuzid.systeminfowidget.Constants.BACKGROUND_RESOURCE_METHOD_NAME;
import static com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity.CATEGORY_SELECTION;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.BATTERY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.CAMERA;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.DISPLAY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.GENERAL;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.MEMORY;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.MORE;
import static com.manuzid.systeminfowidget.util.SystemInfoLib.NONE;
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
     * Alle zur Auswahl Verfügbaren Kategorien
     */
    private static final Map<String, AbstractCategory> availableCategories;

    static {
        HashMap<String, AbstractCategory> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(GENERAL, new GeneralCategory());
        mActiveColoredButtons.put(MORE, new MoreCategory());
        mActiveColoredButtons.put(DISPLAY, new DisplayCategory());
        mActiveColoredButtons.put(CAMERA, new CameraCategory());
        mActiveColoredButtons.put(MEMORY, new MemoryCategory());
        mActiveColoredButtons.put(BATTERY, new BatteryCategory());
        availableCategories = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    /**
     * Alle Widget-Buttons die belegt werden können
     */
    private static final SparseIntArray availableButtons = new SparseIntArray();

    static {
        availableButtons.append(1, R.id.btnFirst);
        availableButtons.append(2, R.id.btnSecond);
        availableButtons.append(3, R.id.btnThird);
        availableButtons.append(4, R.id.btnFourth);
        availableButtons.append(5, R.id.btnFifth);
        availableButtons.append(6, R.id.btnSixth);
    }

    /**
     * Die zuletzt aktivierte Kategorie
     */
    private static String category;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        initObserverMap(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        destroyObserverMap();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = appWidgetIds.length; --i >= 0; ) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);

            // 1 PendingIntent damit wenn der Benutzer die Buttons benutzt auch zwischen den Kategorien navigieren kann.
            List<Integer> buttonIds = Arrays.asList(R.id.btnFirst, R.id.btnSecond,
                    R.id.btnThird, R.id.btnFourth, R.id.btnFifth, R.id.btnSixth);
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
            remoteView.setOnClickPendingIntent(R.id.relative_general, pendingConfig);

            // 3 Prüfen ob sich die Batterie-Kategorie mit unter der Auswahl befindet um den IntentFilter
            // zu registrieren oder eben abzumelden.
            if (observerMap.get(BATTERY) != null) {
                // 3.1 Registrieren des IntentFilter um auf Änderungen die die Batterie betreffen reagieren zu können.
                context.getApplicationContext().registerReceiver(this, intentFilter);
            } else {
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
            /*
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction()) || Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                if (BATTERY.equals(category))
                if (isBatteryViewActive) {
                    RemoteViews remoteView = handleAkkuInfo(context, R.layout.sysinfo_main, intent, 2, colorScheme, appWidId);
                    updateAppWidget(context, remoteView);
                }
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) || Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())
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

    /**
     * Wiederherstellen des entsprechenden View-Zustands.
     *
     * @param remoteViews               {@link RemoteViews}
     * @param infoElementVisibility     entweder {@link View#GONE} oder {@link View#VISIBLE}
     * @param standardElementVisibility entweder {@link View#GONE} oder {@link View#VISIBLE}
     * @return {@link RemoteViews}
     */
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
        remoteViews.setViewVisibility(R.id.device_memory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usb_memory_percent, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.device_memory_progress_bar, infoElementVisibility);
        remoteViews.setViewVisibility(R.id.usb_memory_progress_bar, infoElementVisibility);
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

    /**
     * Fügt der Liste der Beobachter einen Observer hinzu
     *
     * @param beobachter - Der Beobachter, der der Liste hinzugefügt werden soll
     */
    public void addObserver(AbstractCategory beobachter) {
        observerMap.put(beobachter.getRequestAction(), beobachter);
    }

    /**
     * Initialisierung der Map, wird zu Beginn aufgerufen und wenn der Benutzer
     * in den Einstellungen war.
     *
     * @param context {@link Context}
     */
    private void initObserverMap(Context context) {
        // 1. Welche Observer sind in den Preferences gesetzt?
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> categorySelection = prefs.getStringSet(CATEGORY_SELECTION, null);

        if (categorySelection == null) {
            categorySelection = new HashSet<>(Arrays.asList(GENERAL, MORE, DISPLAY, CAMERA, MEMORY, BATTERY));
        }

        // 2. Diese registrieren
        int i = 0;
        for (String category : categorySelection) {
            i++;
            AbstractCategory lCategory = availableCategories.get(category);
            lCategory.setButtonId(availableButtons.get(i));
            addObserver(lCategory);
        }
    }

    /**
     * Wird aufgerufen wenn das Widget gelöscht wird oder der Benutzer von den Einstellungen kommt.
     */
    private void destroyObserverMap() {
        observerMap.clear();
    }

}