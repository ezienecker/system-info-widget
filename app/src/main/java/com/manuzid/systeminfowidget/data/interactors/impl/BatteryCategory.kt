package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.BatteryService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class BatteryCategory(private val context: Context, private val batteryService: BatteryService) :
    AbstractCategory() {

    override fun getInformationen(): Information = Information(
        context.getString(R.string.battery_capacitance), batteryService.getCapacity(),
        context.getString(R.string.battery_state), batteryService.getStatus(),
        context.getString(R.string.battery_technology), batteryService.getTechnology(),
        context.getString(R.string.battery_voltage), batteryService.getVoltage(),
        context.getString(R.string.battery_temperature), batteryService.getTemperature(),
        context.getString(R.string.battery_connected), batteryService.getConnectedState()
    )
}
