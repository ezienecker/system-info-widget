package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.os.Build;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zeigt erweiterte Allgemeine Informationen über das Gerät an.
 * <p>
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class MoreCategory extends AbstractCategory {
    public static final String MORE = INTENT_FILTER_PREFIX + "MORE_WIDGET";

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.more_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.more_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.more_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.more_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.more_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.more_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    private Informationen informationen;

    @Override
    public int getButtonId() {
        return R.id.btnSecond;
    }

    @Override
    public int getRequestCode() {
        return 101;
    }

    @Override
    public String getRequestAction() {
        return MORE;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.more_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @Override
    Informationen getInformationen(Context context) {
        if (informationen == null) {
            informationen = new Informationen.Builder()
                    .first(context.getString(R.string.more_os_sdk_codename), Build.VERSION.CODENAME)
                    .second(context.getString(R.string.more_os_sdk_version), Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT)
                    .third(context.getString(R.string.more_board), Build.BOARD)
                    .fourth(context.getString(R.string.more_bootloader), Build.BOOTLOADER)
                    .fifth(context.getString(R.string.more_cpu_i), System.getProperty("os.arch"))
                    .sixth(context.getString(R.string.more_cpu_ii), Build.CPU_ABI2)
                    .seventh(context.getString(R.string.more_hardware), Build.HARDWARE)
                    .build();
        }

        return informationen;
    }
}