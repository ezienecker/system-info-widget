package com.manuzid.systeminfowidget.category;

import android.content.Context;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.category.util.ConnectivityUtil;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.manuzid.systeminfowidget.category.util.ConnectivityUtil.getWifiName;

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
        String mobileConnectionState = ConnectivityUtil.isConnectedMobile(context) ? context.getString(R.string.network_wlan_connection_state_connected) :
                context.getString(R.string.network_wlan_connection_state_disconnected);

        String mobileType = ConnectivityUtil.getNetworkClass(context);

        String wifiName = ConnectivityUtil.getWifiName(context);

        if (wifiName == null) {
            wifiName = context.getString(R.string.general_unknow);
        } else {
            wifiName = wifiName.endsWith("\"") ? wifiName.substring(0, wifiName.length() - 1) : wifiName;
            wifiName = wifiName.startsWith("\"") ? wifiName.substring(1, wifiName.length()) : wifiName;
        }

        String wifiConnectionState = ConnectivityUtil.isConnectedWifi(context) ? context.getString(R.string.network_wlan_connection_state_connected) :
                context.getString(R.string.network_wlan_connection_state_disconnected);

        String wifiConnectionStrength = ConnectivityUtil.getWifiSignalStrength(context);

        return new Informationen.Builder()
                .first(context.getString(R.string.network_mobile), "")
                .second(context.getString(R.string.network_mobile_state), mobileConnectionState)
                .third(context.getString(R.string.network_mobile_connection_type), mobileType)
                .fourth(context.getString(R.string.network_wlan), "")
                .fifth(context.getString(R.string.network_wlan_name), wifiName)
                .sixth(context.getString(R.string.network_wlan_state), wifiConnectionState)
                .seventh(context.getString(R.string.network_wlan_signal_strength), wifiConnectionStrength)
                .build();
    }

}
