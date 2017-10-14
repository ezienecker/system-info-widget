package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.StringUtils;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Zeigt Informationen sowohl über den internen als auch den externen Speicher des Geräts an.
 * <p>
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class MemoryCategory extends AbstractCategory {
    public static final String MEMORY = INTENT_FILTER_PREFIX + "5_MEMORY_WIDGET";

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.memory_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.memory_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.memory_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.memory_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.memory_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.memory_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    @Override
    public int getRequestCode() {
        return 104;
    }

    @Override
    public String getRequestAction() {
        return MEMORY;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.memory_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @Override
    Informationen getInformationen(Context context) {
        return null;
    }

    @Override
    public RemoteViews prepareRemoteView(RemoteViews remoteView, Context context) {
        int deviceMemory = getDeviceMemoryInPercentForUi();
        int usbMemory = getUsbMemoryInPercentForUi();

        String totalDeviceMemory = getTotalDeviceMemory();
        String freeDeviceMemory = getFreeDeviceMemory();
        String usedDeviceMemory = getBusyDeviceMemory();
        String totalUsbMemory = getTotalUsbMemory();
        String freeUsbMemory = getFreeUsbMemory();
        String usedUsbMemory = getBusyUsbMemory();

        remoteView.setTextViewText(R.id.lblFirstInfo, context.getString(R.string.memory_header_memory));
        remoteView.setTextViewText(R.id.txtFirstInfo, context.getString(R.string.memory_header_values));

        remoteView.setTextViewText(R.id.lblSecondInfo, context.getString(R.string.memory_devicememory));
        remoteView.setTextViewText(R.id.txtSecondInfo, totalDeviceMemory + freeDeviceMemory + usedDeviceMemory);

        remoteView.setTextViewText(R.id.lblThird, "");
        remoteView.setTextViewText(R.id.txtThird, "");

        remoteView.setTextViewText(R.id.lblFourth, "");
        remoteView.setTextViewText(R.id.txtFourth, "");

        remoteView.setViewVisibility(R.id.device_memory_percent, View.VISIBLE);
        remoteView.setTextViewText(R.id.device_memory_percent, context.getString(R.string.memory_used) + " " + deviceMemory + "%");

        remoteView.setTextViewText(R.id.lblFifth, context.getString(R.string.memory_usb_sd_memory));

        if (isUsbMemoryEmptyOrSameAsDeviceMemory(totalDeviceMemory, freeDeviceMemory, usedDeviceMemory,
                totalUsbMemory, freeUsbMemory, usedUsbMemory)) {
            remoteView.setViewVisibility(R.id.usb_memory_progress_bar, View.GONE);
            remoteView.setViewVisibility(R.id.usb_memory_percent, View.VISIBLE);
            remoteView.setTextViewText(R.id.usb_memory_percent, context.getString(R.string.memory_not_available));
            remoteView.setTextViewText(R.id.txtFifth, "");
        } else {
            remoteView.setTextViewText(R.id.txtFifth, totalUsbMemory + freeUsbMemory + usedUsbMemory);
            remoteView.setViewVisibility(R.id.usb_memory_percent, View.VISIBLE);
            remoteView.setTextViewText(R.id.usb_memory_percent, context.getString(R.string.memory_used) + " " + usbMemory + "%");
            remoteView.setViewVisibility(R.id.usb_memory_progress_bar, View.VISIBLE);
            remoteView.setInt(R.id.usb_memory_progress_bar, "setProgress", usbMemory);
        }

        remoteView.setTextViewText(R.id.txtSupportedPictureSizes, "");

        remoteView.setTextViewText(R.id.lblSixth, "");
        remoteView.setTextViewText(R.id.txtSixth, "");

        remoteView.setTextViewText(R.id.lblSeventh, "");
        remoteView.setTextViewText(R.id.txtSeventh, "");

        remoteView.setViewVisibility(R.id.device_memory_progress_bar, View.VISIBLE);
        remoteView.setInt(R.id.device_memory_progress_bar, "setProgress", deviceMemory);

        remoteView.setViewVisibility(R.id.imgRestore, View.GONE);

        return remoteView;
    }

    private boolean isUsbMemoryEmptyOrSameAsDeviceMemory(String totalDeviceMemory, String freeDeviceMemory,
                                                         String usedDeviceMemory, String totalUsbMemory, String freeUsbMemory, String usedUsbMemory) {
        return (StringUtils.isBlank(totalUsbMemory) && StringUtils.isBlank(freeUsbMemory) && StringUtils.isBlank(usedUsbMemory))
                || (totalUsbMemory.equals(totalDeviceMemory) && freeUsbMemory.equals(freeDeviceMemory) && usedUsbMemory.equals(usedDeviceMemory));
    }

    private int getDeviceMemoryInPercentForUi() {
        return calculateMemoryInPercentForUi(getDeviceStatFsObject());
    }

    private int getUsbMemoryInPercentForUi() {
        return calculateMemoryInPercentForUi(getUsbStatFsObject());
    }

    private String getTotalDeviceMemory() {
        return getTotalMemory(getDeviceStatFsObject());
    }

    private String getTotalUsbMemory() {
        return getTotalMemory(getUsbStatFsObject());
    }

    private String getFreeDeviceMemory() {
        return getFreeMemory(getDeviceStatFsObject());
    }

    private String getFreeUsbMemory() {
        return getFreeMemory(getUsbStatFsObject());
    }

    private String getBusyDeviceMemory() {
        return getBusyMemory(getDeviceStatFsObject());
    }

    private String getBusyUsbMemory() {
        return getBusyMemory(getUsbStatFsObject());
    }

    private String getTotalMemory(StatFs statFs) {
        long total = getTotalMemoryCount(statFs);
        if (total >= 1000) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = total / 1024.0;
            return decimalFormat.format(totalFree) + " GB/";
        } else {
            return String.valueOf(total) + " MB/";
        }
    }

    private String getFreeMemory(StatFs statFs) {
        long free = getFreeMemoryCount(statFs);

        if (free >= 1000) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = free / 1024.0;
            return decimalFormat.format(totalFree) + " GB/";
        } else {
            return String.valueOf(free) + " MB/";
        }
    }

    private String getBusyMemory(StatFs statFs) {
        long total = getTotalMemoryCount(statFs);
        long free = getFreeMemoryCount(statFs);
        long busy = total - free;
        if (busy >= 1000) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = busy / 1024.0;
            return decimalFormat.format(totalFree) + " GB";
        } else {
            return String.valueOf(busy) + " MB";
        }
    }

    private int calculateMemoryInPercentForUi(StatFs statFs) {
        long total = getTotalMemoryCount(statFs);
        long free = getFreeMemoryCount(statFs);
        double busy = total - free;
        double a = (busy / total) * 100;
        return (int) a;
    }

    private long getTotalMemoryCount(StatFs statFs) {
        if (Build.VERSION.SDK_INT < 18) {
            //noinspection deprecation
            return ((long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;
        } else {
            return (statFs.getBlockCountLong() * statFs.getBlockSizeLong()) / 1048576;
        }
    }

    private long getFreeMemoryCount(StatFs statFs) {
        if (Build.VERSION.SDK_INT < 18) {
            //noinspection deprecation
            return ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
        } else {
            return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong()) / 1048576;
        }
    }

    /**
     * Holt das entsprechende {@link StatFs} Objekt was dem Device zugeordnet ist.
     *
     * @return Device-{@link StatFs}
     */
    private StatFs getDeviceStatFsObject() {
        return new StatFs(Environment.getDataDirectory().getAbsolutePath());
    }

    /**
     * Liefert das entsprechende {@link StatFs} Objekt was der SD-Karte entspricht.
     *
     * @return SD-Karte-{@link StatFs}
     */
    private StatFs getUsbStatFsObject() {
        File externalSdCard = getExternalSDCardDirectory();

        if (externalSdCard != null) {
            return new StatFs(externalSdCard.getAbsolutePath());
        } else {
            return new StatFs(Environment.getDataDirectory().getAbsolutePath());
        }
    }

    /**
     * Liefert das Verzeichnis des externen Speichermedium.
     *
     * @return {@link File} externe Speichermedium
     */
    private File getExternalSDCardDirectory() {
        if (Build.VERSION.SDK_INT >= 19) {
            return getExternalSDCardDirectoryForDevicesWhereSdkIsUnderEqual19();
        } else {
            return getExternalSDCardDirectoryForDevicesWhereSdkIsOver19();
        }
    }

    // TODO: Bitte Refactor mich
    private File getExternalSDCardDirectoryForDevicesWhereSdkIsUnderEqual19() {
        final Pattern DIR_SEPARATOR = Pattern.compile("/");
        final Set<String> rv = new HashSet<>();
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        final String rawSecondaryStorageStr = System.getenv("SECONDARY_STORAGE");
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");

        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {

            if (TextUtils.isEmpty(rawExternalStorage)) {
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            final String rawUserId;
            if (Build.VERSION.SDK_INT < 17) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    int t = Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        if (!TextUtils.isEmpty(rawSecondaryStorageStr)) {
            final String[] rawSecondaryStorage = rawSecondaryStorageStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorage);
        }
        String[] directories = rv.toArray(new String[rv.size()]);

        return new File(directories[0]);
    }

    private File getExternalSDCardDirectoryForDevicesWhereSdkIsOver19() {
        File innerDir = Environment.getExternalStorageDirectory();
        File rootDir = innerDir.getParentFile();
        File firstExtSdCard = innerDir;
        File[] files = rootDir.listFiles();

        for (File file : files) {
            if (file.compareTo(innerDir) != 0) {
                if (file.getAbsolutePath().contains("/storage/extSdCard")) {
                    firstExtSdCard = file;
                    break;
                }
            }
        }

        return firstExtSdCard;
    }
}
