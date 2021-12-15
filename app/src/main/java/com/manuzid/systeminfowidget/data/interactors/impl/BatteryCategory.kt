package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.BatteryService
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_BLACK
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_BLUE
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_GREEN
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_LILA
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_ORANGE
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.COLOR_RED

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class BatteryCategory(private val context: Context, private val batteryService: BatteryService) :
    AbstractCategory() {

    override fun getRequestCode(): Int = 5

    override fun getRequestAction(): String = BATTERY

    override fun getDefaultImageForCategory(): Int = R.drawable.battery_btn

    override fun getInformationen(): Information = Information(
        context.getString(R.string.battery_capacitance), batteryService.getCapacity(),
        context.getString(R.string.battery_state), batteryService.getStatus(),
        context.getString(R.string.battery_technology), batteryService.getTechnology(),
        context.getString(R.string.battery_voltage), batteryService.getVoltage(),
        context.getString(R.string.battery_temperature), batteryService.getTemperature(),
        context.getString(R.string.battery_connected), batteryService.getConnectedState()
    )

    companion object {
        const val BATTERY = INTENT_FILTER_PREFIX + "BATTERY_WIDGET"
    }
}
