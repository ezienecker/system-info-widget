package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.manuzid.systeminfowidget.util.SystemInfoLib.DISPLAY;

/**
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class DisplayCategory extends AbstractCategory {

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.display_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.display_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.display_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.display_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.display_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.display_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    private Informationen informationen;

    @Override
    int initRequestCode() {
        return 106;
    }

    @Override
    String initRequestAction() {
        return DISPLAY;
    }

    @Override
    public int getButtonId() {
        // TODO
        return 0;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.display_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

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
                    .seventh(context.getString(R.string.general_timezone), TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT))
                    .build();
        }

        return informationen;
    }

    @Override
    public RemoteViews prepareRemoteView(RemoteViews remoteView, Context context) {
        return null;
    }
}
