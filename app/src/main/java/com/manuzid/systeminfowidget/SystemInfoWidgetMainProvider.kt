package com.manuzid.systeminfowidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.RemoteViews
import com.manuzid.systeminfowidget.data.interactors.impl.*
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by Emanuel Zienecker on 15.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class SystemInfoWidgetMainProvider : AppWidgetProvider(), KoinComponent {

    private val batteryCategory: BatteryCategory by inject()
    private val cameraCategory: CameraCategory by inject()
    private val displayCategory: DisplayCategory by inject()
    private val generalInformationCategory: GeneralInformationCategory by inject()
    private val moreInformationCategory: MoreInformationCategory by inject()
    private val networkCategory: NetworkCategory by inject()

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun hasPowerPluginChangedAndBatteryViewIsActive(intentAction: String): Boolean =
            Intent.ACTION_POWER_CONNECTED == intentAction ||
                    Intent.ACTION_POWER_DISCONNECTED == intentAction

    private fun hasBatteryStatusChangedAndBatteryViewIsActive(intentAction: String): Boolean =
            Intent.ACTION_BATTERY_CHANGED == intentAction ||
                    Intent.ACTION_BATTERY_OKAY == intentAction ||
                    Intent.ACTION_BATTERY_LOW == intentAction

    private fun hasNetworkChange(intentAction: String): Boolean =
        "android.net.wifi.supplicant.CONNECTION_CHANGE" == intentAction ||
                "android.net.wifi.RSSI_CHANGED" == intentAction ||
                "android.net.wifi.STATE_CHANGE" == intentAction

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val remoteView = when (intent!!.action) {
            BatteryCategory.BATTERY -> {
                batteryCategory.getRemoteView(context!!)
            }
            CameraCategory.CAMERA -> {
                cameraCategory.getRemoteView(context!!)
            }
            DisplayCategory.DISPLAY -> {
                displayCategory.getRemoteView(context!!)
            }
            GeneralInformationCategory.GENERAL -> {
                generalInformationCategory.getRemoteView(context!!)
            }
            MoreInformationCategory.MORE -> {
                moreInformationCategory.getRemoteView(context!!)
            }
            NetworkCategory.NETWORK -> {
                networkCategory.getRemoteView(context!!)
            }
            else -> {
                networkCategory.getRemoteView(context!!)
                // TODO: Make it work, the view would not be updated. The Click listener doesn't work
                //RemoteViews(context!!.packageName, R.layout.system_info_widget_default)
            }
        }

        val systemInfoWidgetComponent = ComponentName(context, SystemInfoWidgetMainProvider::class.java)
        AppWidgetManager.getInstance(context).apply {
            updateAppWidget(systemInfoWidgetComponent, remoteView)
        }
    }

    private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
    ) {
        val generalCategoryPendingIntent = getPendingIntent(
                context, 0,
                SystemInfoWidgetMainProvider::class.java, GeneralInformationCategory.GENERAL
        )
        val moreCategoryPendingIntent = getPendingIntent(
                context, 1,
                SystemInfoWidgetMainProvider::class.java, MoreInformationCategory.MORE
        )
        val displayCategoryPendingIntent = getPendingIntent(
                context, 2,
                SystemInfoWidgetMainProvider::class.java, DisplayCategory.DISPLAY
        )
        val cameraCategoryPendingIntent = getPendingIntent(
                context, 3,
                SystemInfoWidgetMainProvider::class.java, CameraCategory.CAMERA
        )
        val batteryCategoryPendingIntent = getPendingIntent(
                context, 4,
                SystemInfoWidgetMainProvider::class.java, BatteryCategory.BATTERY
        )
        val networkCategoryPendingIntent = getPendingIntent(
                context, 5,
                SystemInfoWidgetMainProvider::class.java, NetworkCategory.NETWORK
        )

        val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.system_info_main
        ).apply {
            setOnClickPendingIntent(
                    R.id.system_info_widget_container,
                    getPendingConfigurationPreferencesIntent(context)
            )
            setOnClickPendingIntent(R.id.btn_general, generalCategoryPendingIntent)
            setOnClickPendingIntent(R.id.btn_more, moreCategoryPendingIntent)
            setOnClickPendingIntent(R.id.btn_display, displayCategoryPendingIntent)
            setOnClickPendingIntent(R.id.btn_camera, cameraCategoryPendingIntent)
            setOnClickPendingIntent(R.id.btn_memory, networkCategoryPendingIntent)
            setOnClickPendingIntent(R.id.btn_battery, batteryCategoryPendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun <T> getPendingIntent(
            context: Context,
            requestCode: Int,
            cls: Class<T>,
            action: String
    ) = PendingIntent.getBroadcast(
            context, requestCode, Intent(context, cls).apply {
        this.action = action
    },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getPendingConfigurationPreferencesIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(
                    context, 0, Intent(context, SettingsActivity::class.java).apply {
                action = "com.manuzid.systeminfowidget.preferences"
            },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

    companion object {
        private val THIRD_PARTY_INTENT_FILTER = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
    }
}
