package com.manuzid.systeminfowidget.di.module

import android.os.Build
import android.os.Build.VERSION_CODES

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface MoreInformationService {
    fun getSDKVersion(): String

    fun getSDKCodename(): String

    fun getBoard(): String

    fun getBootloader(): String

    fun getCPU(): String

    fun getUsedRAM(): String
}

class MoreInformationServiceImpl: MoreInformationService {
    override fun getSDKVersion(): String = Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT

    override fun getSDKCodename(): String {
        val fields = VERSION_CODES::class.java.fields
        return fields[Build.VERSION.SDK_INT + 1].name
    }

    override fun getBoard(): String = Build.BOARD

    override fun getBootloader(): String = Build.BOOTLOADER

    override fun getCPU(): String = Build.SUPPORTED_ABIS[0]

    override fun getUsedRAM(): String {
        return "TODO(Not yet implemented)"
    }

}
