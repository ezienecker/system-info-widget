package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
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
    static final String INTENT_FILTER_PREFIX = "com.manuzid.systeminfowidget.";
    public static final String NONE = INTENT_FILTER_PREFIX + "NONE";

    private int buttonId;

    /**
     * Liefert den Request-Code der von der SubClass gesetzt werden muss.
     * Für den PendingIntent wird eine entsprechende Code gebraucht.
     *
     * @return Request-Code
     */
    public abstract int getRequestCode();

    /**
     * Liefert die Request-Action die von der SubClass gesetzt werden muss.
     * Für den PendingIntent wird eine entsprechende Action gebraucht.
     *
     * @return Request-Action
     */
    public abstract String getRequestAction();

    /**
     * Liefert die Id des Button der zur Kategorie gehört.
     *
     * @return Id des Buttons
     */
    public int getButtonId() {
        return buttonId;
    }

    /**
     * Setzt die Id des Button der zur Kategorie gehört.
     */
    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }

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
    protected abstract Map<String, Integer> getActiveColoredButtonsMap();

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
     * @param context    - Application resources
     * @return {@link RemoteViews}
     */
    public RemoteViews prepareRemoteView(RemoteViews remoteView, Context context) {
        final Informationen informationen = getInformationen(context);
        remoteView.setTextViewText(R.id.lblFirstInfo, informationen.firstLabel);
        remoteView.setTextViewText(R.id.txtFirstInfo, informationen.firstValue);

        remoteView.setTextViewText(R.id.lblSecondInfo, informationen.secondLabel);
        remoteView.setTextViewText(R.id.txtSecondInfo, informationen.secondValue);

        remoteView.setTextViewText(R.id.lblThird, informationen.thirdLabel);
        remoteView.setTextViewText(R.id.txtThird, informationen.thirdValue);

        remoteView.setTextViewText(R.id.lblFourth, informationen.fourthLabel);
        remoteView.setTextViewText(R.id.txtFourth, informationen.fourthValue);

        remoteView.setTextViewText(R.id.lblFifth, informationen.fifthLabel);
        remoteView.setTextViewText(R.id.txtFifth, informationen.fifthValue);

        remoteView.setTextViewText(R.id.lblSixth, informationen.sixthLabel);
        remoteView.setTextViewText(R.id.txtSixth, informationen.sixthValue);

        remoteView.setTextViewText(R.id.lblSeventh, informationen.seventhLabel);
        remoteView.setTextViewText(R.id.txtSeventh, informationen.seventhValue);

        remoteView.setTextViewText(R.id.txtSupportedPictureSizes, "");

        remoteView.setViewVisibility(R.id.device_memory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.usb_memory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.device_memory_progress_bar, View.GONE);
        remoteView.setViewVisibility(R.id.usb_memory_progress_bar, View.GONE);
        remoteView.setViewVisibility(R.id.imgRestore, View.GONE);

        return remoteView;
    }

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
}