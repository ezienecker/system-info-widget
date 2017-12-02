package com.manuzid.systeminfowidget.category;

import android.annotation.SuppressLint;
import android.content.Context;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zeigt die Allgemeinen Informationen über das Gerät an.
 * <p>
 * Created by Emanuel Zienecker on 19.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class GeneralCategory extends AbstractCategory {
    public static final String GENERAL = INTENT_FILTER_PREFIX + "1_GENERAL_WIDGET";

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
    public int getRequestCode() {
        return 100;
    }

    @Override
    public String getRequestAction() {
        return GENERAL;
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
        if (informationen == null) {
            informationen = new Informationen.Builder()
                    .first(context.getString(R.string.general_manufacturer), android.os.Build.MANUFACTURER)
                    .second(context.getString(R.string.general_model), android.os.Build.MODEL)
                    .third(context.getString(R.string.general_product), android.os.Build.PRODUCT)
                    .fourth(context.getString(R.string.general_brand), android.os.Build.BRAND)
                    .fifth(context.getString(R.string.general_serialnumber), android.os.Build.SERIAL)
                    .sixth(context.getString(R.string.general_device_id), android.os.Build.ID)
                    .build();
        }

        return informationen;
    }
}