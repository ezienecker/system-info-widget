package com.manuzid.systeminfowidget.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.manuzid.systeminfowidget.Constants.LOG_TAG;

/**
 * Zeigt Informationen über das Display des Geräts an.
 * <p>
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class DisplayCategory extends AbstractCategory {
    public static final String DISPLAY = INTENT_FILTER_PREFIX + "3_DISPLAY_WIDGET";

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

    @Override
    public int getRequestCode() {
        return 102;
    }

    @Override
    public String getRequestAction() {
        return DISPLAY;
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
        final WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return new Informationen.Builder()
                    .first(context.getString(R.string.battery_capacitance), context.getString(R.string.general_unknow))
                    .second(context.getString(R.string.battery_state), context.getString(R.string.general_unknow))
                    .third(context.getString(R.string.battery_technology), context.getString(R.string.general_unknow))
                    .fourth(context.getString(R.string.battery_voltage), context.getString(R.string.general_unknow))
                    .fifth(context.getString(R.string.battery_temp), context.getString(R.string.general_unknow))
                    .sixth(context.getString(R.string.battery_connected), context.getString(R.string.general_unknow))
                    .build();
        }
        else {
            Display display = windowManager.getDefaultDisplay();
            return new Informationen.Builder()
                    .first(context.getString(R.string.display_display_size), getDeviceSize(context, display))
                    .second(context.getString(R.string.display_height), String.valueOf(getDisplayHeight(display)))
                    .third(context.getString(R.string.display_width), String.valueOf(getDisplayWidth(display)))
                    .fourth(context.getString(R.string.display_dps), getScreenDps(context.getResources().getDisplayMetrics(), context))
                    .fifth(context.getString(R.string.display_fps), String.valueOf(display.getRefreshRate()))
                    .sixth(context.getString(R.string.display_display_scale), String.valueOf(context.getResources().getDisplayMetrics().scaledDensity))
                    .build();
        }
    }

    @SuppressLint("FloatMath")
    private String getDeviceSize(final Context context, final Display display) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            float height = metrics.heightPixels / metrics.xdpi;
            float width = metrics.widthPixels / metrics.ydpi;

            DecimalFormat decimalFormat = new DecimalFormat("#0.0");

            String deviceSize = decimalFormat.format(Math.sqrt(height * height + width * width));
            deviceSize = deviceSize.replace(",", ".");
            deviceSize = deviceSize + " " + context.getString(R.string.display_display_size_summary);
            return deviceSize;
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Es ist ein Fehler beim berechnen der Gerätegröße aufgetreten.", t);
            return context.getString(R.string.general_unknow);
        }
    }

    private int getDisplayHeight(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    private int getDisplayWidth(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @NonNull
    private String getScreenDps(final DisplayMetrics displayMetrics, final Context context) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                return context.getString(R.string.display_dps_hdpi);
            case DisplayMetrics.DENSITY_MEDIUM:
                return context.getString(R.string.display_dps_mdpi);
            case DisplayMetrics.DENSITY_LOW:
                return context.getString(R.string.display_dps_ldpi);
            case DisplayMetrics.DENSITY_XHIGH:
                return context.getString(R.string.display_dps_xhdpi);
            case DisplayMetrics.DENSITY_TV:
                return context.getString(R.string.display_dps_tv);
            case DisplayMetrics.DENSITY_XXHIGH:
                return context.getString(R.string.display_dps_xxhdpi);
            default:
                return context.getString(R.string.display_dps_default);
        }
    }
}
