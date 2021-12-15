package com.manuzid.systeminfowidget.di.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.NetworkInfo.DetailedState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.manuzid.systeminfowidget.R

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface NetworkService {
    fun getMobileConnectionState(): String

    fun getMobileType(): String

    fun getWiFiName(): String

    fun getWiFiConnectionState(): String

    fun getWiFiConnectionStrength(): String
}

class NetworkServiceImpl(val context: Context) : NetworkService {

    private var networkInfo: NetworkInfo? =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override fun getMobileConnectionState(): String =
        if (networkInfo != null && networkInfo!!.isConnected && networkInfo!!.type == ConnectivityManager.TYPE_MOBILE) {
            context.getString(R.string.network_connected)
        } else {
            context.getString(R.string.network_disconnected)
        }

    override fun getMobileType(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return context.getString(R.string.general_unknow)
        }

        when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS ->
                return context.getString(R.string.network_wlan_connection_type_gprs)
            TelephonyManager.NETWORK_TYPE_EDGE ->
                return context.getString(R.string.network_wlan_connection_type_edge)
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN ->
                return context.getString(R.string.network_wlan_connection_type_2g)
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP ->
                return context.getString(R.string.network_wlan_connection_type_3g)
            TelephonyManager.NETWORK_TYPE_LTE ->
                return context.getString(R.string.network_wlan_connection_type_lte)
            else ->
                return context.getString(R.string.general_unknow)
        }
    }

    override fun getWiFiName(): String {
        if (!wifiManager.isWifiEnabled) {
            return context.getString(R.string.general_unknow)
        }

        val wifiInfo =
            wifiManager.connectionInfo ?: return context.getString(R.string.general_unknow)

        val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
        if (state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
            return wifiInfo.ssid
        }

        return context.getString(R.string.general_unknow)
    }

    override fun getWiFiConnectionState(): String =
        if (networkInfo != null && networkInfo!!.isConnected && networkInfo!!.type == ConnectivityManager.TYPE_WIFI) {
            context.getString(R.string.network_connected)
        } else {
            context.getString(R.string.network_disconnected)
        }

    override fun getWiFiConnectionStrength(): String =
        when (WifiManager.calculateSignalLevel(wifiManager.connectionInfo.rssi, 5)) {
            1 -> context.getString(R.string.network_wlan_connection_strength_weak)
            2 -> context.getString(R.string.network_wlan_connection_strength_fair)
            3 -> context.getString(R.string.network_wlan_connection_strength_good)
            4 -> context.getString(R.string.network_wlan_connection_strength_excellent)
            5 -> context.getString(R.string.network_wlan_connection_strength_excellent)
            else -> context.getString(R.string.general_unknow)
        }

}
