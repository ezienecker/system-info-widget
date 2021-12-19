package com.manuzid.systeminfowidget.ui.preferences

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.SystemInfoWidgetMainProvider

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

    override fun onPause() {
        updateAppWidget()
        Toast.makeText(
            this,
            this.resources.getString(R.string.config_save_changes),
            Toast.LENGTH_LONG
        ).show()
        super.onPause()
    }

    private fun updateAppWidget() {
        sendBroadcast(Intent(this, SystemInfoWidgetMainProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        })
    }

    companion object {
        const val TEMPERATURE_FORMAT = "temperature_format"
        const val COLOR_SCHEME = "color_scheme"

        const val COLOR_BLUE = "color_blue"
        const val COLOR_RED = "color_red"
        const val COLOR_ORANGE = "color_orange"
        const val COLOR_GREEN = "color_green"
        const val COLOR_WHITE = "color_white"
    }
}
