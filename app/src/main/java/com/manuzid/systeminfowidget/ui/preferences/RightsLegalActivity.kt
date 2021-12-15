package com.manuzid.systeminfowidget.ui.preferences

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.manuzid.systeminfowidget.R
import java.util.*

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class RightsLegalActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_rights_legal)

        findViewById<TextView>(R.id.config_legal_privat).apply {
            text = getString(
                R.string.config_entries_rights_legal_privat,
                Calendar.getInstance()[Calendar.YEAR]
            )
        }
    }
}
