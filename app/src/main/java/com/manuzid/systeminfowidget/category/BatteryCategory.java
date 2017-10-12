package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Zeigt Informationen über die Batterie an.
 * <p>
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class BatteryCategory extends AbstractCategory {
    public static final String BATTERY = INTENT_FILTER_PREFIX + "BATTERY_WIDGET";

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.akku_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.akku_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.akku_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.akku_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.akku_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.akku_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    @Override
    public int getRequestCode() {
        return 105;
    }

    @Override
    public String getRequestAction() {
        return BATTERY;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.battery_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @Override
    Informationen getInformationen(Context context) {
        Intent mIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (mIntent == null) {
            return new Informationen.Builder()
                    .first(context.getString(R.string.battery_capacitance), context.getString(R.string.general_unknow))
                    .second(context.getString(R.string.battery_state), context.getString(R.string.general_unknow))
                    .third(context.getString(R.string.akku_technology), context.getString(R.string.general_unknow))
                    .fourth(context.getString(R.string.akku_voltage), context.getString(R.string.general_unknow))
                    .fifth(context.getString(R.string.akku_temp), context.getString(R.string.general_unknow))
                    .sixth(context.getString(R.string.akku_connected), context.getString(R.string.general_unknow))
                    .seventh(context.getString(R.string.akku_akku_health), context.getString(R.string.general_unknow))
                    .build();
        } else {

            int status = mIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            int extra = mIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int health = mIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            int temp = mIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            String technology;
            try {
                technology = mIntent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

            } catch (Exception e) {
                technology = context.getString(R.string.akku_technology_summ);
            }

            return new Informationen.Builder()
                    .first(context.getString(R.string.battery_capacitance), mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%")
                    .second(context.getString(R.string.battery_state), getBatteryStatusForUi(status, context))
                    .third(context.getString(R.string.akku_technology), technology)
                    .fourth(context.getString(R.string.akku_voltage), mIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) + " mV")
                    .fifth(context.getString(R.string.akku_temp), getBatteryTemp(temp, context))
                    .sixth(context.getString(R.string.akku_connected), getConnectedState(context, extra))
                    .seventh(context.getString(R.string.akku_akku_health), getBatteryHealthForUi(health, context))
                    .build();
        }
    }

    private String getBatteryStatusForUi(final int status, final Context context) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return context.getString(R.string.battery_state_charging);
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return context.getString(R.string.battery_state_dis_charging);
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return context.getString(R.string.battery_state_not_charging);
            case BatteryManager.BATTERY_STATUS_FULL:
                return context.getString(R.string.battery_state_full);
            default:
                return context.getString(R.string.general_unknow);
        }
    }

    private String getConnectedState(final Context context, final int connectedState) {
        switch (connectedState) {
            case 0:
                return context.getString(R.string.battery_connected_akku);
            case BatteryManager.BATTERY_PLUGGED_USB:
                return context.getString(R.string.battery_connected_usb);
            case BatteryManager.BATTERY_PLUGGED_AC:
                return context.getString(R.string.battery_connected_ac);
            default:
                return context.getString(R.string.general_unknow);
        }
    }

    private String getBatteryTemp(final int temp, final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String tempFormat = prefs.getString(ConfigPreferencesActivity.TEMP_FORMAT, ConfigPreferencesActivity.TEMP_CELSIUS);

        String batteryTemp = context.getString(R.string.general_unknow);
        if (temp > 0) {
            if (ConfigPreferencesActivity.TEMP_CELSIUS.equals(tempFormat)) {
                batteryTemp = temp / 10 + " °C";
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                batteryTemp = decimalFormat.format(((temp / 10) * 1.8) + 32) + " °F";
            }
        }

        return batteryTemp;
    }

    private String getBatteryHealthForUi(final int health, final Context context) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return context.getString(R.string.battery_health_cold);
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return context.getString(R.string.battery_health_dead);
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return context.getString(R.string.battery_health_good);
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return context.getString(R.string.battery_health_overheat);
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return context.getString(R.string.battery_health_over_voltage);
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return context.getString(R.string.battery_health_unspecified_failure);
            default:
                return context.getString(R.string.general_unknow);
        }
    }

}
