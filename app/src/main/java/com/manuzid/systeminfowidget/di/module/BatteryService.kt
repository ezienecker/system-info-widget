package com.manuzid.systeminfowidget.di.module

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager.*
import androidx.preference.PreferenceManager
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.ui.preferences.SettingsActivity.Companion.TEMPERATURE_FORMAT
import java.text.DecimalFormat

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface BatteryService {

    fun getStatus(): String

    fun getConnectedState(): String

    fun getTemperature(): String

    fun getCapacity(): String

    fun getTechnology(): String

    fun getVoltage(): String
}

class BatteryServiceImpl(val context: Context) : BatteryService {

    private var intent = context.applicationContext.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val unknownInformation = context.getString(R.string.general_unknow)

    override fun getStatus(): String = when (requestBatteryStatus()) {
        BATTERY_STATUS_CHARGING -> context.getString(R.string.battery_state_charging)
        BATTERY_STATUS_DISCHARGING -> context.getString(R.string.battery_state_dis_charging)
        BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.battery_state_not_charging)
        BATTERY_STATUS_FULL -> context.getString(R.string.battery_state_full)
        BATTERY_STATUS_UNKNOWN -> unknownInformation
        else -> unknownInformation
    }

    private fun requestBatteryStatus(): Int = intent?.getIntExtra(
        EXTRA_STATUS,
        BATTERY_STATUS_UNKNOWN
    ) ?: BATTERY_STATUS_UNKNOWN

    override fun getConnectedState(): String = when (requestBatteryExtra()) {
        BATTERY_CONNECTED -> context.getString(R.string.battery_connected_battery)
        BATTERY_PLUGGED_USB -> context.getString(R.string.battery_connected_usb)
        BATTERY_PLUGGED_AC -> context.getString(R.string.battery_connected_ac)
        else -> unknownInformation
    }

    private fun requestBatteryExtra(): Int = intent?.getIntExtra(EXTRA_PLUGGED, 0) ?: -1

    override fun getTemperature(): String {
        val decimalFormat = DecimalFormat("#0.00")
        val defaultTemperatureFormat = context.getString(R.string.settings_temp_celsius_default)
        val temperatureFormat = sharedPreferences.getString(
            TEMPERATURE_FORMAT, defaultTemperatureFormat
        )

        val temperature = requestBatteryTemperature()

        if (temperature <= 0) {
            return unknownInformation
        }

        return if (temperatureFormat.equals(defaultTemperatureFormat)) {
            decimalFormat.format(temperature / 10) + " °C"
        } else {
            decimalFormat.format(temperature / 10 * 1.8 + 32) + " °F"
        }
    }

    override fun getCapacity(): String {
        val intentExtra = intent?.getIntExtra(EXTRA_LEVEL, 0)
        if (intentExtra == 0) {
            return unknownInformation
        }

        return intentExtra.toString() + "%"
    }

    override fun getTechnology(): String =
        intent?.getStringExtra(EXTRA_TECHNOLOGY) ?: unknownInformation

    override fun getVoltage(): String {
        val intentExtra = intent?.getIntExtra(EXTRA_VOLTAGE, 0)
        if (intentExtra == 0) {
            return unknownInformation
        }

        return intentExtra.toString() + " mV"
    }

    private fun requestBatteryTemperature(): Int = intent?.getIntExtra(EXTRA_TEMPERATURE, 0) ?: 0

    companion object {
        private const val BATTERY_CONNECTED: Int = 0
    }
}

