package com.manuzid.systeminfowidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import com.manuzid.systeminfowidget.data.interactors.impl.*
import com.manuzid.systeminfowidget.data.interactors.impl.BatteryCategory.Companion.BATTERY
import com.manuzid.systeminfowidget.data.interactors.impl.CameraCategory.Companion.CAMERA
import com.manuzid.systeminfowidget.data.interactors.impl.DisplayCategory.Companion.DISPLAY
import com.manuzid.systeminfowidget.data.interactors.impl.GeneralInformationCategory.Companion.GENERAL
import com.manuzid.systeminfowidget.data.interactors.impl.MoreInformationCategory.Companion.MORE
import com.manuzid.systeminfowidget.data.interactors.impl.NetworkCategory.Companion.NETWORK
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_BLUE
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_GREEN
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_ORANGE
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_RED
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_SCHEME
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_WHITE
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

        val category = when (intent!!.action) {
            PREVIOUS -> {
                categoryState.second
            }
            NEXT -> {
                categoryState.third
            }
            else -> {
                categoryState.first
            }
        }

        val (remoteView, current, previous, next) = when (category) {
            BATTERY -> {
                Quadruple(batteryCategory.getRemoteView(context!!), BATTERY, NETWORK, CAMERA)
            }
            CAMERA -> {
                Quadruple(cameraCategory.getRemoteView(context!!), CAMERA, BATTERY, DISPLAY)
            }
            DISPLAY -> {
                Quadruple(displayCategory.getRemoteView(context!!), DISPLAY, CAMERA, GENERAL)
            }
            GENERAL -> {
                Quadruple(generalInformationCategory.getRemoteView(context!!), GENERAL, DISPLAY, MORE)
            }
            MORE -> {
                Quadruple(moreInformationCategory.getRemoteView(context!!), MORE, GENERAL, NETWORK)
            }
            NETWORK -> {
                Quadruple(networkCategory.getRemoteView(context!!), NETWORK, MORE, BATTERY)
            }
            else -> {
                Quadruple(batteryCategory.getRemoteView(context!!), BATTERY, NETWORK, CAMERA)
            }
        }

        categoryState = Triple(current, previous, next)

        val systemInfoWidgetComponent = ComponentName(context, SystemInfoWidgetMainProvider::class.java)
        AppWidgetManager.getInstance(context).apply {
            updateAppWidget(systemInfoWidgetComponent,
                    setupAppwidget(context, remoteView))
        }
    }

    private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
    ) {
        val views: RemoteViews = setupAppwidget(context,
                RemoteViews(context.packageName, R.layout.system_info_widget_main))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setupAppwidget(context: Context, views: RemoteViews): RemoteViews {
        setupButtonClickListener(context, views)
        return setupColorScheme(context, views)
    }

    private fun setupColorScheme(context: Context, views: RemoteViews): RemoteViews {
        val (color, previous, settings, next) = getAppwidgetColor(context)
        return views.apply {
            setTextColor(R.id.widget_header,
                    context.resources.getColor(color, null))
            setImageViewResource(R.id.widget_previous_category, previous)
            setImageViewResource(R.id.widget_settings, settings)
            setImageViewResource(R.id.widget_next_category, next)
        }
    }

    private fun setupButtonClickListener(context: Context, views: RemoteViews) =
            views.apply {
                setOnClickPendingIntent(R.id.widget_previous_category, getPendingIntent(
                        context, SystemInfoWidgetMainProvider::class.java, PREVIOUS
                ))
                setOnClickPendingIntent(
                        R.id.widget_settings,
                        getPendingConfigurationPreferencesIntent(context)
                )
                setOnClickPendingIntent(R.id.widget_next_category, getPendingIntent(
                        context, SystemInfoWidgetMainProvider::class.java, NEXT
                ))
            }

    private fun <T> getPendingIntent(
            context: Context,
            cls: Class<T>,
            action: String
    ) = PendingIntent.getBroadcast(
            context, 1, Intent().setClass(context, cls).apply {
        this.action = action
    }, PendingIntent.FLAG_IMMUTABLE)

    private fun getPendingConfigurationPreferencesIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(
                    context, 0, Intent(context, SettingsActivity::class.java).apply {
                action = "com.manuzid.systeminfowidget.ui.preferences"
            }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    private fun getAppwidgetColor(context: Context): Quadruple<Int, Int, Int, Int> =
            when (PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(COLOR_SCHEME, COLOR_WHITE)!!) {
                COLOR_BLUE -> {
                    Quadruple(R.color.widget_blue, R.drawable.navigate_before,
                            R.drawable.settings_blue, R.drawable.navigate_next)
                }
                COLOR_GREEN -> {
                    Quadruple(R.color.widget_green, R.drawable.navigate_before,
                            R.drawable.settings_green, R.drawable.navigate_next)
                }
                COLOR_ORANGE -> {
                    Quadruple(R.color.widget_orange, R.drawable.navigate_before,
                            R.drawable.settings_orange, R.drawable.navigate_next)
                }
                COLOR_RED -> {
                    Quadruple(R.color.widget_red, R.drawable.navigate_before,
                            R.drawable.settings_red, R.drawable.navigate_next)
                }
                COLOR_WHITE -> {
                    Quadruple(R.color.white, R.drawable.navigate_before,
                            R.drawable.settings_white, R.drawable.navigate_next)
                }
                else -> {
                    Quadruple(R.color.white, R.drawable.navigate_before,
                            R.drawable.settings_white, R.drawable.navigate_next)
                }
            }


    companion object {
        const val PREVIOUS = "PREVIOUS"
        const val NEXT = "NEXT"

        // Unfortunately, Android doesn't support extras for PendingIntent.
        private var categoryState = Triple(BATTERY, NETWORK, CAMERA)
    }

}

data class Quadruple<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)
