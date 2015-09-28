package com.manuzid.systeminfowidget.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera.Size;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

public class SystemInfoLib
{
    public static final String DISPLAY = "com.manuzid.systeminfowidget.DISPLAY_WIDGET";
    public static final String CAMERA = "com.manuzid.systeminfowidget.CAMERA_WIDGET";
    public static final String GENERAL = "com.manuzid.systeminfowidget.GENRAL_WIDGET";
    public static final String MORE = "com.manuzid.systeminfowidget.MORE_WIDGET";
    public static final String MEMORY = "com.manuzid.systeminfowidget.MEMORY_WIDGET";
    public static final String AKKU = "com.manuzid.systeminfowidget.AKKU_WIDGET";

    public static RemoteViews restoreViewInfo(final RemoteViews rV, final Context context, final int appWidgetID)
    {
        rV.setViewVisibility(R.id.lblManufacturer, View.VISIBLE);
        rV.setViewVisibility(R.id.txtManufacturer, View.VISIBLE);
        rV.setViewVisibility(R.id.lblModell, View.VISIBLE);
        rV.setViewVisibility(R.id.txtModell, View.VISIBLE);
        rV.setViewVisibility(R.id.lblProduct, View.VISIBLE);
        rV.setViewVisibility(R.id.txtProduct, View.VISIBLE);
        rV.setViewVisibility(R.id.lblBrand, View.VISIBLE);
        rV.setViewVisibility(R.id.txtBrand, View.VISIBLE);
        rV.setViewVisibility(R.id.lblSerialNumber, View.VISIBLE);
        rV.setViewVisibility(R.id.txtSerialnumber, View.VISIBLE);
        rV.setViewVisibility(R.id.txtSupportedPictureSizes, View.VISIBLE);
        rV.setViewVisibility(R.id.lblDeviceId, View.VISIBLE);
        rV.setViewVisibility(R.id.txtDeviceId, View.VISIBLE);
        rV.setViewVisibility(R.id.lblTimeZone, View.VISIBLE);
        rV.setViewVisibility(R.id.txtTimeZone, View.VISIBLE);
        rV.setViewVisibility(R.id.devicememory_percent, View.VISIBLE);
        rV.setViewVisibility(R.id.usbmemory_percent, View.VISIBLE);
        rV.setViewVisibility(R.id.devicememory_progressBar, View.VISIBLE);
        rV.setViewVisibility(R.id.usbmemory_progressBar, View.VISIBLE);
        rV.setViewVisibility(R.id.imgRestore, View.GONE);
        rV.setViewVisibility(R.id.txtConfigClick, View.GONE);

        PendingIntent pendingDisplay = preparePendingIntent(context, DISPLAY, appWidgetID, 100, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingCamera = preparePendingIntent(context, CAMERA, appWidgetID, 101, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingGeneral = preparePendingIntent(context, GENERAL, appWidgetID, 102, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingMore = preparePendingIntent(context, MORE, appWidgetID, 103, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingMemory = preparePendingIntent(context, MEMORY, appWidgetID, 104, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingAkku = preparePendingIntent(context, AKKU, appWidgetID, 105, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(context, ConfigPreferencesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(ConfigPreferencesActivity.class.getName());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        PendingIntent pendingConfig = PendingIntent.getActivity(context, 106, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        rV.setOnClickPendingIntent(R.id.btnDisplay, pendingDisplay);
        rV.setOnClickPendingIntent(R.id.btnCamera, pendingCamera);
        rV.setOnClickPendingIntent(R.id.btnGeneral, pendingGeneral);
        rV.setOnClickPendingIntent(R.id.btnMore, pendingMore);
        rV.setOnClickPendingIntent(R.id.btnMemory, pendingMemory);
        rV.setOnClickPendingIntent(R.id.btnAkku, pendingAkku);
        rV.setOnClickPendingIntent(R.id.relaGeneral, pendingConfig);

        return rV;
    }

    public static PendingIntent preparePendingIntent(Context context, String reqAction, int appWidgetId, int reqCode, int flag)
    {
        Intent preparedIntent = new Intent();
        preparedIntent.setAction(reqAction);
        preparedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, reqCode, preparedIntent, flag);
    }

    public static String getBatteryTemp(final int temp, final Resources resources, final Context contextTemp, SharedPreferences prefs)
    {
        if (prefs == null)
        {
            prefs = PreferenceManager.getDefaultSharedPreferences(contextTemp);
        }

        String tempFormat = prefs.getString(ConfigPreferencesActivity.TEMP_FORMAT, ConfigPreferencesActivity.TEMP_CELSIUS);

        String batteryTempe = resources.getString(R.string.general_unknow);
        if (temp > 0)
        {
            if (ConfigPreferencesActivity.TEMP_CELSIUS.equals(tempFormat))
            {
                batteryTempe = temp / 10 + " °C";
            }
            else
            {
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                batteryTempe = decimalFormat.format(((temp / 10) * 1.8) + 32) + " °F";
            }
        }
        return batteryTempe;
    }

    public static String getBatteryStatusForUi(final int status, final Resources resources)
    {
        String batteryStatus = resources.getString(R.string.general_unknow);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING)
        {
            batteryStatus = resources.getString(R.string.akku_state_charging);
        }
        else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING)
        {
            batteryStatus = resources.getString(R.string.akku_state_dis_charging);
        }
        else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
        {
            batteryStatus = resources.getString(R.string.akku_state_not_charging);
        }
        else if (status == BatteryManager.BATTERY_STATUS_FULL)
        {
            batteryStatus = resources.getString(R.string.akku_state_full);
        }

        return batteryStatus;
    }

    public static String getBatteryHealthForUi(final int health, final Resources resources)
    {
        String batteryHealth = resources.getString(R.string.general_unknow);
        if (health == BatteryManager.BATTERY_HEALTH_COLD)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_cold);
        }
        else if (health == BatteryManager.BATTERY_HEALTH_DEAD)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_dead);
        }
        else if (health == BatteryManager.BATTERY_HEALTH_GOOD)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_good);
        }
        else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_overheat);
        }
        else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_over_voltage);
        }
        else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
        {
            batteryHealth = resources.getString(R.string.akku_akku_health_unspecified_failure);
        }

        return batteryHealth;
    }

    public static String getConnectedState(final Resources resources, final int connectedState)
    {
        String connectedString = resources.getString(R.string.general_unknow);
        if (connectedState == 0)
        {
            connectedString = resources.getString(R.string.akku_connected_akku);
        }
        else if (connectedState == BatteryManager.BATTERY_PLUGGED_USB)
        {
            connectedString = resources.getString(R.string.akku_connected_usb);
        }
        else if (connectedState == BatteryManager.BATTERY_PLUGGED_AC)
        {
            connectedString = resources.getString(R.string.akku_connected_ac);
        }
        return connectedString;
    }

    public static String getPictureFormatForUI(final int pictureInt, final Resources contextResources)
    {
        String preparePictureFormat = contextResources.getString(R.string.general_unknow);
        if (pictureInt == ImageFormat.JPEG)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_jpeg);
        }
        else if (pictureInt == ImageFormat.NV16)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_nv);
        }
        else if (pictureInt == ImageFormat.NV21)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_nvsecond);
        }
        else if (pictureInt == ImageFormat.RGB_565)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_rgb);
        }
        else if (pictureInt == ImageFormat.YUY2)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_yuy);
        }
        else if (pictureInt == ImageFormat.YV12)
        {
            preparePictureFormat = contextResources.getString(R.string.camera_picture_format_yv);
        }

        return preparePictureFormat;
    }

    public static String getPreviewFormats(final List<Integer> supportedPictureForList, final Resources contextResources)
    {
        StringBuilder sb = new StringBuilder();
        if (supportedPictureForList != null)
        {
            for (Integer formats : supportedPictureForList)
            {
                sb.append(getPictureFormatForUI(formats, contextResources));
                sb.append(", ");
            }
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        return sb.toString();
    }

    public static String getPictureSizeForUI(final Size size)
    {
        return size.height + " x " + size.width;
    }

    public static String getPreviewSizes(final List<Size> supportedPictureSizes)
    {
        StringBuilder sb = new StringBuilder();
        for (Size size : supportedPictureSizes)
        {
            sb.append(getPictureSizeForUI(size));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        return sb.toString();
    }

    public static String getFaceCamAvailable(final PackageManager packageMan, final Resources resources)
    {
        String facecamString;
        if (packageMan.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
        {
            facecamString = resources.getString(R.string.camera_facecam_available);
        }
        else
        {
            facecamString = resources.getString(R.string.camera_facecam_not_available);
        }
        return facecamString;
    }

    public static String getScreenDps(final DisplayMetrics displayMetrics, final Resources resources)
    {
        String screenSize = resources.getString(R.string.general_unknow);

        if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_HIGH)
        {
            screenSize = resources.getString(R.string.display_dps_hdpi);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
        {
            screenSize = resources.getString(R.string.display_dps_mdpi);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_LOW)
        {
            screenSize = resources.getString(R.string.display_dps_ldpi);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
        {
            screenSize = resources.getString(R.string.display_dps_xhdpi);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_TV)
        {
            screenSize = resources.getString(R.string.display_dps_tv);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH)
        {
            screenSize = resources.getString(R.string.display_dps_xxhdpi);
        }
        else if (displayMetrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT)
        {
            screenSize = resources.getString(R.string.display_dps_default);
        }

        return screenSize;
    }

    public static String getScreenOrientation(final int orientation, final Resources resources)
    {
        String orientString = resources.getString(R.string.general_unknow);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            orientString = resources.getString(R.string.display_orientation_landscape);
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            orientString = resources.getString(R.string.display_orientation_portrait);
        }

        return orientString;
    }

    public static File getExternalSDCardDirectory()
    {
        if (Build.VERSION.SDK_INT >= 19)
        {
            final Pattern DIR_SEPORATOR = Pattern.compile("/");

            final Set<String> rv = new HashSet<String>();

            final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");

            final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

            final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
            if (TextUtils.isEmpty(rawEmulatedStorageTarget))
            {

                if (TextUtils.isEmpty(rawExternalStorage))
                {
                    rv.add("/storage/sdcard0");
                }
                else
                {
                    rv.add(rawExternalStorage);
                }
            }
            else
            {
                final String rawUserId;
                if (Build.VERSION.SDK_INT < 17)
                {
                    rawUserId = "";
                }
                else
                {
                    final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    final String[] folders = DIR_SEPORATOR.split(path);
                    final String lastFolder = folders[folders.length - 1];
                    boolean isDigit = false;
                    try
                    {
                        Integer.valueOf(lastFolder);
                        isDigit = true;
                    }
                    catch (NumberFormatException ignored)
                    {
                    }
                    rawUserId = isDigit ? lastFolder : "";
                }
                if (TextUtils.isEmpty(rawUserId))
                {
                    rv.add(rawEmulatedStorageTarget);
                }
                else
                {
                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                }
            }
            if (!TextUtils.isEmpty(rawSecondaryStoragesStr))
            {
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            String [] directories = rv.toArray(new String[rv.size()]);

            return new File(directories[0]);
        }
        else
        {
            File innerDir = Environment.getExternalStorageDirectory();
            File rootDir = innerDir.getParentFile();
            File firstExtSdCard = innerDir;
            File[] files = rootDir.listFiles();
            for (File file : files)
            {
                if (file.compareTo(innerDir) != 0)
                {
                    if (file.getAbsolutePath().contains("/storage/extSdCard"))
                    {
                        firstExtSdCard = file;
                        break;
                    }
                }
            }
            return firstExtSdCard;
        }
    }

    public static String getTotalMemory(final boolean deviceMemory)
    {
        String freeExternalMemory = "0 MB/";
        StatFs statFs;
        if (deviceMemory)
        {
            statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        }
        else
        {
            File externalSdCard = getExternalSDCardDirectory();
            if (externalSdCard != null)
            {
                statFs = new StatFs(externalSdCard.getAbsolutePath());
            }
            else
            {
                statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            }
        }
        long total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;
        if (total >= 1000)
        {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = total / (double) 1024.0;
            freeExternalMemory = decimalFormat.format(totalFree) + " GB/";
        }
        else
        {
            freeExternalMemory = String.valueOf(total) + " MB/";
        }
        return freeExternalMemory;
    }

    public static String getFreeMemory(final boolean deviceMemory)
    {
        String freeExternalMemory = "0 MB/";
        StatFs statFs;
        if (deviceMemory)
        {
            statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        }
        else
        {
            File externalSdCard = getExternalSDCardDirectory();
            if (externalSdCard != null)
            {
                statFs = new StatFs(externalSdCard.getAbsolutePath());
            }
            else
            {
                statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            }
        }
        long free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
        if (free >= 1000)
        {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = free / (double) 1024.0;
            freeExternalMemory = decimalFormat.format(totalFree) + " GB/";
        }
        else
        {
            freeExternalMemory = String.valueOf(free) + " MB/";
        }
        return freeExternalMemory;
    }

    public static String getBusyMemory(final boolean deviceMemory)
    {
        String freeExternalMemory = "0 MB/";
        StatFs statFs;
        if (deviceMemory)
        {
            statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        }
        else
        {
            File externalSdCard = getExternalSDCardDirectory();
            if (externalSdCard != null)
            {
                statFs = new StatFs(externalSdCard.getAbsolutePath());
            }
            else
            {
                statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            }
        }
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
        long busy = Total - Free;
        if (busy >= 1000)
        {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            double totalFree = busy / (double) 1024.0;
            freeExternalMemory = decimalFormat.format(totalFree) + " GB";
        }
        else
        {
            freeExternalMemory = String.valueOf(busy) + " MB";
        }
        return freeExternalMemory;
    }

    public static int getPercentForUi(final boolean deviceMemory)
    {
        StatFs statFs;
        if (deviceMemory)
        {
            statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        }
        else
        {
            File externalSdCard = getExternalSDCardDirectory();
            if (externalSdCard != null)
            {
                statFs = new StatFs(externalSdCard.getAbsolutePath());
            }
            else
            {
                statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            }
        }
        double total = ((double) statFs.getBlockCount() * (double) statFs.getBlockSize()) / 1048576;
        double free = (statFs.getAvailableBlocks() * (double) statFs.getBlockSize()) / 1048576;
        double busy = total - free;
        double a = (busy / total) * 100;
        return (int) a;
    }

    @SuppressLint("FloatMath")
    public static String getDeviceSize(final Resources resources, final Display display)
    {
        String deviceSize;
        try
        {

            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            float height = metrics.heightPixels / metrics.xdpi;
            float width = metrics.widthPixels / metrics.ydpi;

            DecimalFormat decimalFormat = new DecimalFormat("#0.0");

            deviceSize = decimalFormat.format(FloatMath.sqrt(height * height + width * width));
            deviceSize.replace(",", ".");

            deviceSize = deviceSize + " " + resources.getString(R.string.display_display_size_summary);

        }
        catch (Throwable t)
        {
            deviceSize = resources.getString(R.string.general_unknow);
        }

        return deviceSize;

    }
}