package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import android.widget.RemoteViews
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.DisplayService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class DisplayCategory(private val context: Context, private val displayService: DisplayService) :
    AbstractCategory() {

    override fun getInformationen(): Information = Information(
        context.getString(R.string.display_display_size), displayService.getSize(),
        context.getString(R.string.display_height), displayService.getHeight(),
        context.getString(R.string.display_width), displayService.getWidth(),
        context.getString(R.string.display_dps), displayService.getDps(),
        context.getString(R.string.display_fps), displayService.getFps(),
        context.getString(R.string.display_display_scale), displayService.getDisplayScale()
    )

    override fun getRemoteView(context: Context): RemoteViews {
        return super.getRemoteView(context).apply {
            setTextViewText(R.id.widget_header, "Display")
        }
    }

    companion object {
        const val DISPLAY = INTENT_FILTER_PREFIX + "DISPLAY_WIDGET"
    }
}
