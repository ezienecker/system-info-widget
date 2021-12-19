package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import android.widget.RemoteViews
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

    override fun getInformationen(): Information = Information(
        context.getString(R.string.network_mobile), "",
        context.getString(R.string.network_mobile_state), networkService.getMobileConnectionState(),
        context.getString(R.string.network_mobile_connection_type), networkService.getMobileType(),
        context.getString(R.string.network_wlan), "",
        context.getString(R.string.network_wlan_name), networkService.getWiFiName(),
        context.getString(R.string.network_wlan_state) + "/" + context.getString(R.string.network_wlan_signal_strength),
        networkService.getWiFiConnectionState() + "/" + networkService.getWiFiConnectionStrength()
    )

    override fun getRemoteView(context: Context): RemoteViews {
        return super.getRemoteView(context).apply {
            setTextViewText(R.id.widget_header, "Network")
        }
    }

    companion object {
        const val NETWORK = INTENT_FILTER_PREFIX + "NETWORK_WIDGET"
    }
}
