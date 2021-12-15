package com.manuzid.systeminfowidget.di.module

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface GeneralInformationService {
    fun getManufacturer(): String

    fun getModel(): String

    fun getProduct(): String

    fun getBrand(): String

    fun getFingerprint(): String

    fun getDeviceId(): String
}

class GeneralInformationServiceImpl : GeneralInformationService {
    override fun getManufacturer(): String = android.os.Build.MANUFACTURER

    override fun getModel(): String = android.os.Build.MODEL

    override fun getProduct(): String = android.os.Build.PRODUCT

    override fun getBrand(): String = android.os.Build.BRAND

    override fun getFingerprint(): String = android.os.Build.FINGERPRINT

    override fun getDeviceId(): String = android.os.Build.ID
}
