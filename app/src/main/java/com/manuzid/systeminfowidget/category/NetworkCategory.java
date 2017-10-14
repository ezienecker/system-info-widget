package com.manuzid.systeminfowidget.category;

import android.content.Context;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emanuel Zienecker on 09.10.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class NetworkCategory extends AbstractCategory {
    public static final String NETWORK = INTENT_FILTER_PREFIX + "7_NETWORK_WIDGET";

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.network_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.network_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.network_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.network_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.network_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.network_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    @Override
    public int getRequestCode() {
        return 107;
    }

    @Override
    public String getRequestAction() {
        return NETWORK;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.network_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @Override
    Informationen getInformationen(Context context) {
        return new Informationen.Builder()
                .first(context.getString(R.string.general_manufacturer), "Network")
                .second(context.getString(R.string.general_model), "Network")
                .third(context.getString(R.string.general_product), "Network")
                .fourth(context.getString(R.string.general_brand), "Network")
                .fifth(context.getString(R.string.general_serialnumber), "Network")
                .sixth(context.getString(R.string.general_device_id), "Network")
                .seventh(context.getString(R.string.general_timezone), "Network")
                .build();
    }
}
