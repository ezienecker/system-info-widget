package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.manuzid.systeminfowidget.Constants.BACKGROUND_RESOURCE_METHOD_NAME;
import static com.manuzid.systeminfowidget.Constants.LOG_TAG;

/**
 * Eine abstrakte Darstellung von einer Kategorie.
 * <p>
 * Created by Emanuel Zienecker on 18.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public abstract class AbstractCategory {
    private int requestCode;
    private String requestAction;

    AbstractCategory() {
        this.requestCode = initRequestCode();
        this.requestAction = initRequestAction();
    }

    /**
     * Für den PendingIntent wird eine entsprechende Code gebraucht.
     *
     * @return Request-Code
     */
    abstract int initRequestCode();

    /**
     * Für den PendingIntent wird eine entsprechende Action gebraucht.
     *
     * @return Request-Action
     */
    abstract String initRequestAction();

    /**
     * Liefert die Id des Button der zur Kategorie gehört.
     *
     * @return Id des Buttons
     */
    public abstract int getButtonId();

    /**
     * Liefert die Standard-Grafik der Kategorie.
     *
     * @return Drawable der Standard-Grafik der Kategorie
     */
    public abstract int getDefaultButtonDrawable();

    /**
     * Liefert die möglichen aktiven Buttons in einer Map.
     *
     * @return eine Map<String, Integer>, erste Wert ist das Farbschema und
     * der zweite die dazugehörige Drawable
     */
    public abstract Map<String, Integer> getActiveColoredButtonsMap();

    /**
     * Alle möglichen Farbschemata müssen in einer Map gespeichert werden und dann bei
     * dieser Methode hier zur Verfügung stehen. {@link AbstractCategory#getActiveColoredButtonsMap()}.
     * <br/>
     * Bsp: map.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.general_btn_pressed_blue) ->
     * R.drawable.general_btn_pressed_blue = map.get(colorScheme);
     * <br/>
     * Somit kann die Drawable die zum entsprechenden Farbschema gehört, geholt werden.
     * <br/>
     * Wird der Eintrag nicht gefunden wird die Drawable von {@link AbstractCategory#getDefaultButtonDrawable()}
     * zurück gegeben und der Fehler wird protokolliert.
     *
     * @param colorScheme - Farbschema welches der Benutzer ausgewählt hat.
     * @return Drawable die zum entsprechenden Farbschema gehört
     */
    private int getActiveButtonDrawableByColorScheme(String colorScheme) {
        if (StringUtils.isBlank(colorScheme)) {
            Log.d(LOG_TAG,
                    "Es wurde zu dem entsprechenden Farbschema: " + colorScheme + " keine passende Drawable gefunden.");
            getDefaultButtonDrawable();
        }

        return getActiveColoredButtonsMap().get(colorScheme);
    }

    /**
     * Liefert eine Preferences-Instanz, enthalten ist die Id des ViewElement
     * und der entsprechenden Wert von den anzuzeigenden Eigenschaften
     * der entsprechenden Kategorie.
     *
     * @param context - Application resources
     * @return {@link LinkedHashMap}
     */
    abstract Informationen getInformationen(Context context);

    /**
     * Kümmert sich darum dass die entsprechende View mit den korrekten Informationen befüllt wird.
     *
     * @param remoteView - Application context
     * @param context  - Application resources
     * @return {@link RemoteViews}
     */
    public abstract RemoteViews prepareRemoteView(RemoteViews remoteView, Context context);

    /**
     * Setzt das Aktiv-Bild abhängig vom Farbschema für die Kategorie.
     *
     * @param colorScheme Farbschema für die Kategorie
     */
    public void activateButtonBackgroundResource(RemoteViews remoteViews, String colorScheme) {
        remoteViews.setInt(getButtonId(), BACKGROUND_RESOURCE_METHOD_NAME,
                getActiveButtonDrawableByColorScheme(colorScheme));
    }

    /**
     * Setzt das Standard-Bild für die Kategorie.
     */
    public void restoreButtonBackgroundResource(RemoteViews remoteViews) {
        remoteViews.setInt(getButtonId(), BACKGROUND_RESOURCE_METHOD_NAME, getDefaultButtonDrawable());
    }

    /**
     * Liefert den Request-Code der von der SubClass gesetzt werden muss.
     *
     * @return den Request-Code
     */
    public int getRequestCode() {
        if (requestCode == 0) {
            throw new IllegalStateException("RequestCode muss von der SubClass implementiert werden.");
        }

        return requestCode;
    }

    /**
     * Liefert die Request-Action die von der SubClass gesetzt werden muss.
     *
     * @return die Request-Action
     */
    public String getRequestAction() {
        if (StringUtils.isBlank(requestAction)) {
            throw new IllegalStateException("RequestAction muss von der SubClass implementiert werden.");
        }

        return requestAction;
    }
}