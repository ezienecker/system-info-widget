package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Zeigt erweiterte Allgemeine Informationen über das Gerät an.
 * <p>
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class MoreCategory extends AbstractCategory {
    private static final String TAG = MoreCategory.class.getSimpleName();
    public static final String MORE = INTENT_FILTER_PREFIX + "2_MORE_WIDGET";

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
                    .first(context.getString(R.string.more_os_sdk_version), Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT)
                    .second(context.getString(R.string.more_os_sdk_codename), getOsName(Build.VERSION.SDK_INT))
                    .third(context.getString(R.string.more_board), Build.BOARD)
                    .fourth(context.getString(R.string.more_bootloader), Build.BOOTLOADER)
                    .fifth(context.getString(R.string.more_cpu_i), getCpuAbi())
                    .sixth(context.getString(R.string.more_used_ram), getTotalRam())
                    .seventh(context.getString(R.string.more_hardware), Build.HARDWARE)
                    .build();
        }

        return informationen;
    }

    private String getOsName(int sdkInt) {
        switch (sdkInt) {
            case 17:
            case 18:
                return "Jelly Bean";
            case 19:
                return "KitKat";
            case 21:
            case 22:
                return "Lollipop";
            case 23:
                return "Marshmallow";
            case 24:
            case 25:
                return "Nougat";
            case 26:
            default:
                return "Oreo";
        }
    }

    /**
     * The name of the instruction set (CPU type + ABI convention) of native code.
     *
     * @return CPU-ABI as {@link String}
     * @see <a href="https://developer.android.com/reference/android/os/Build.html">android.os.Build#CPU_ABI</a>
     */
    private String getCpuAbi() {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                //noinspection deprecation
                return Build.CPU_ABI;
            } else {
                return Build.SUPPORTED_ABIS[0];
            }
        } catch (Exception e) {
            return System.getProperty("os.arch");
        }
    }

    /**
     * Liefert den momentan verwendeten RAM.
     *
     * @return momentan verwendeten RAM. {@link String}
     */
    private String getTotalRam() {
        RandomAccessFile reader = null;
        String load;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
            }
            reader.close();

            totRam = Double.parseDouble(value);

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        return lastValue;
    }
}