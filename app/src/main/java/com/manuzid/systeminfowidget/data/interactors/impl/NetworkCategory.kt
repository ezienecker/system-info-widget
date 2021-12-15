package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.NetworkService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class NetworkCategory(private val context: Context, private val networkService: NetworkService) :
    AbstractCategory() {

    override fun getRequestCode(): Int = 7

    override fun getRequestAction(): String = NETWORK

    override fun getInformationen(): Information = Information(
        context.getString(R.string.network_mobile), "",
        context.getString(R.string.network_mobile_state), networkService.getMobileConnectionState(),
        context.getString(R.string.network_mobile_connection_type), networkService.getMobileType(),
        context.getString(R.string.network_wlan), "",
        context.getString(R.string.network_wlan_name), networkService.getWiFiName(),
        context.getString(R.string.network_wlan_state) + "/" + context.getString(R.string.network_wlan_signal_strength),
        networkService.getWiFiConnectionState() + "/" + networkService.getWiFiConnectionStrength()
    )

    companion object {
        const val NETWORK = INTENT_FILTER_PREFIX + "NETWORK_WIDGET"
    }
}
