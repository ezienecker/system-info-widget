package com.manuzid.systeminfowidget.di.module

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import com.manuzid.systeminfowidget.R
import java.io.RandomAccessFile
import java.text.DecimalFormat

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface MoreInformationService {
    fun getSDKVersion(): String

    fun getSDKCodename(): String

    fun getBoard(): String

    fun getReleaseVersion(): String

    fun getCPU(): String

    fun getUsedRAM(): String
}

class MoreInformationServiceImpl(val context: Context) : MoreInformationService {
    override fun getSDKVersion(): String = Build.VERSION.SDK_INT.toString()

    override fun getSDKCodename(): String {
        val fields = VERSION_CODES::class.java.fields
        return fields[Build.VERSION.SDK_INT + 1].name
    }

    override fun getBoard(): String = Build.BOARD

    override fun getReleaseVersion(): String = Build.VERSION.RELEASE

    override fun getCPU(): String = Build.SUPPORTED_ABIS[0]

    override fun getUsedRAM(): String =
            RandomAccessFile("/proc/meminfo", "r").use { reader ->
                val memoryInfo = reader.readLine()
                val memory: String = (Regex.fromLiteral("""(\d+)""").find(memoryInfo)?.groups?.first()
                        ?: context.getString(R.string.general_unknow)) as String

                if (!isNumber(memory)) {
                    return memory
                }

                val totalRAM = memory.toDouble()
                val ramMibibyte = totalRAM / 1024.0
                val ramGibibyte = totalRAM / 1048576.0
                val decimalFormat = DecimalFormat("#.##")

                return when {
                    ramGibibyte > 1 -> {
                        "${decimalFormat.format(ramGibibyte)} +  GB"
                    }
                    ramMibibyte > 1 -> {
                        "${decimalFormat.format(ramMibibyte)} +  MB"
                    }
                    else -> {
                        "${decimalFormat.format(totalRAM)} +  KB"
                    }
                }
            }

    private fun isNumber(input: String): Boolean {
        var dotOccurred = 0
        return input.all { it in '0'..'9' || it == '.' && dotOccurred++ < 1 }
    }

}
