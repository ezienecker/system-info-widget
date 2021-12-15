package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.MoreInformationService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class MoreInformationCategory(
    private val context: Context,
    private val moreInformationService: MoreInformationService
) :
    AbstractCategory() {

    override fun getRequestCode(): Int = 1

    override fun getRequestAction(): String = MORE

    override fun getInformationen(): Information = Information(
        context.getString(R.string.more_os_sdk_version), moreInformationService.getSDKVersion(),
        context.getString(R.string.more_os_sdk_codename), moreInformationService.getSDKCodename(),
        context.getString(R.string.more_board), moreInformationService.getBoard(),
        context.getString(R.string.more_bootloader), moreInformationService.getBootloader(),
        context.getString(R.string.more_cpu_i), moreInformationService.getCPU(),
        context.getString(R.string.more_used_ram), moreInformationService.getUsedRAM()
    )

    companion object {
        const val MORE = INTENT_FILTER_PREFIX + "MORE_WIDGET"
    }
}
