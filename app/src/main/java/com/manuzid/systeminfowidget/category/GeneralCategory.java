package com.manuzid.systeminfowidget.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.manuzid.systeminfowidget.util.SystemInfoLib.GENERAL;

/**
 * Zeigt die Allgemeinen Informationen über das Gerät an.
 *
 * Created by Emanuel Zienecker on 19.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class GeneralCategory extends AbstractCategory {

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.general_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.general_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.general_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.general_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.general_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.general_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    private Informationen informationen;

    @Override
    int initRequestCode() {
        return 102;
    }

    @Override
    String initRequestAction() {
        return GENERAL;
    }

    @Override
    public int getButtonId() {
        // TODO
        return R.id.btnThree;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.general_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @SuppressLint("HardwareIds")
    @Override
    Informationen getInformationen(Context context) {
        // TODO: Am besten auch bei der erzeugung der Klasse erzeugen und dann nur noch auslesen? Wie bei Akku?
        if (informationen == null) {
            informationen = new Informationen.Builder()
                    .first(context.getString(R.string.general_manufacturer), android.os.Build.MANUFACTURER)
                    .second(context.getString(R.string.general_model), android.os.Build.MODEL)
                    .third(context.getString(R.string.general_product), android.os.Build.PRODUCT)
                    .fourth(context.getString(R.string.general_brand), android.os.Build.BRAND)
                    .fifth(context.getString(R.string.general_serialnumber), android.os.Build.SERIAL)
                    .sixth(context.getString(R.string.general_device_id), android.os.Build.ID)
                    .seventh(context.getString(R.string.general_timezone), TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT))
                    .build();
        }

        return informationen;
    }

    @Override
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

        remoteView.setViewVisibility(R.id.devicememory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.imgRestore, View.GONE);

        return remoteView;
    }
}