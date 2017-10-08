package com.manuzid.systeminfowidget.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.FloatMath;
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
    public static final String DISPLAY = INTENT_FILTER_PREFIX + "DISPLAY_WIDGET";

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
    public int getRequestCode() {
        return 102;
    }

    @Override
    public String getRequestAction() {
        return DISPLAY;
    }

    @Override
    public int getButtonId() {
        return R.id.btnThird;
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
            Display display = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            informationen = new Informationen.Builder()
                    .first(context.getString(R.string.display_display_size), getDeviceSize(context, display))
                    .second(context.getString(R.string.display_height), String.valueOf(getDisplayHeight(display)))
                    .third(context.getString(R.string.display_width), String.valueOf(getDisplayWidth(display)))
                    .fourth(context.getString(R.string.display_dps), getScreenDps(context.getResources().getDisplayMetrics(), context))
                    .fifth(context.getString(R.string.display_fps), String.valueOf(display.getRefreshRate()))
                    .sixth(context.getString(R.string.display_display_skala), String.valueOf(context.getResources().getDisplayMetrics().scaledDensity))
                    .seventh(context.getString(R.string.display_orientation),
                            getScreenOrientation(context.getResources().getConfiguration().orientation, context))
                    .build();
        }

        return informationen;
    }

    @SuppressLint("FloatMath")
    private String getDeviceSize(final Context context, final Display display) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            float height = metrics.heightPixels / metrics.xdpi;
            float width = metrics.widthPixels / metrics.ydpi;

            DecimalFormat decimalFormat = new DecimalFormat("#0.0");

            String deviceSize = decimalFormat.format(FloatMath.sqrt(height * height + width * width));
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

    @NonNull
    private String getScreenOrientation(final int orientation, final Context context) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return context.getString(R.string.display_orientation_landscape);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return context.getString(R.string.display_orientation_portrait);
        } else {
            return context.getString(R.string.general_unknow);
        }
    }
}
