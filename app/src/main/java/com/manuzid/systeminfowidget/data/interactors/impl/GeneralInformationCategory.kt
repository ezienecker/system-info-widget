package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.GeneralInformationService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class GeneralInformationCategory(
    private val context: Context,
    private val generalInformationService: GeneralInformationService
) : AbstractCategory() {

    override fun getRequestCode(): Int = 0

    override fun getRequestAction(): String = GENERAL

    override fun getInformationen(): Information = Information(
        context.getString(R.string.general_manufacturer), generalInformationService.getManufacturer(),
        context.getString(R.string.general_model), generalInformationService.getModel(),
        context.getString(R.string.general_product), generalInformationService.getProduct(),
        context.getString(R.string.general_brand), generalInformationService.getBrand(),
        context.getString(R.string.general_fingerprint), generalInformationService.getFingerprint(),
        context.getString(R.string.general_device_id), generalInformationService.getDeviceId()
    )

    companion object {
        const val GENERAL = INTENT_FILTER_PREFIX + "GENERAL_WIDGET"
    }
}
