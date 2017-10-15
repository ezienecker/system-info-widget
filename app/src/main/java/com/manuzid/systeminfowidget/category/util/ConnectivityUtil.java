package com.manuzid.systeminfowidget.category.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.manuzid.systeminfowidget.R;

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 * @see <a href="https://stackoverflow.com/a/21060552/1809221">SO - Detect if connection is wifi, 3G or EDGE in android?</a>
 */
public class ConnectivityUtil {

    /**
     * Get the network info
     *
     * @param context {@link Context}
     * @return {@link NetworkInfo}
     */
    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context {@link Context}
     * @return is connected with wifi true otherwise false
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = ConnectivityUtil.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context {@link Context}
     * @return is connected with mobile true otherwise false
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = ConnectivityUtil.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Ermittelt den Netzwerk-Type
     *
     * @param context {@link Context}
     * @return den Netzwerk-Typ als {@link String}
     */
    @NonNull
    public static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return context.getString(R.string.network_wlan_connection_type_gprs);
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return context.getString(R.string.network_wlan_connection_type_edge);
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return context.getString(R.string.network_wlan_connection_type_2g);
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return context.getString(R.string.network_wlan_connection_type_3g);
            case TelephonyManager.NETWORK_TYPE_LTE:
                return context.getString(R.string.network_wlan_connection_type_lte);
            default:
                return context.getString(R.string.general_unknow);
        }
    }

    /**
     * Ermitteln des Netzwerknamen mit dem der Benutzer momentan verbunden ist.
     *
     * @param context {@link Context}
     * @return Name des Netzwerks als {@link String} oder null wenn kein Wifi vorhanden ist.
     */
    @Nullable
    public static String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

    /**
     * Ermittelt die Signalstärke des W-Lans mit dem der Benutzer verbunden ist.
     *
     * @param context {@link Context}
     * @return Stärke des W-Lans in einer Staffelung von 5
     */
    @NonNull
    public static String getWifiSignalStrength(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);

        switch (level) {
            case 1:
                return context.getString(R.string.network_wlan_connection_strength_weak);
            case 2:
                return context.getString(R.string.network_wlan_connection_strength_fair);
            case 3:
                return context.getString(R.string.network_wlan_connection_strength_good);
            case 4:
                return context.getString(R.string.network_wlan_connection_strength_excellent);
            case 5:
                return context.getString(R.string.network_wlan_connection_strength_excellent);
            default:
                return context.getString(R.string.general_unknow);
        }
    }
}
