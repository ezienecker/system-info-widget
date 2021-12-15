package com.manuzid.systeminfowidget.di.module

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.manuzid.systeminfowidget.R
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface DisplayService {
    val context: Context

    fun getSize(): String

    fun getHeight(): String

    fun getWidth(): String

    fun getDps(): String =
        when (context.resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_HIGH -> context.getString(R.string.display_dps_hdpi)
            DisplayMetrics.DENSITY_MEDIUM -> context.getString(R.string.display_dps_mdpi)
            DisplayMetrics.DENSITY_LOW -> context.getString(R.string.display_dps_ldpi)
            DisplayMetrics.DENSITY_XHIGH -> context.getString(R.string.display_dps_xhdpi)
            DisplayMetrics.DENSITY_TV -> context.getString(R.string.display_dps_tv)
            DisplayMetrics.DENSITY_XXHIGH -> context.getString(R.string.display_dps_xxhdpi)
            else -> context.getString(R.string.display_dps_default)
        }

    fun getFps(): String

    fun getDisplayScale(): String =
        context.resources.displayMetrics.scaledDensity.toString()
}

class DisplayServiceUnderApi30Impl(override val context: Context) : DisplayService {

    private val unknownInformation = context.getString(R.string.general_unknow)
    private var display: Display? =
        (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)!!.defaultDisplay


    override fun getSize(): String {
        return try {
            val metrics = DisplayMetrics()
            display!!.getMetrics(metrics)
            val height = metrics.heightPixels / metrics.xdpi
            val width = metrics.widthPixels / metrics.ydpi
            "${DecimalFormat("#0.0").format(sqrt((height * height + width * width).toDouble()))} ${
                context.getString(
                    R.string.display_display_size_summary
                )
            }"
        } catch (t: Throwable) {
            unknownInformation
        }
    }

    override fun getHeight(): String {
        val size = Point()
        display!!.getSize(size)
        return size.y.toString()
    }

    override fun getWidth(): String {
        val size = Point()
        display!!.getSize(size)
        return size.x.toString()
    }

    override fun getFps(): String = (round(display!!.refreshRate * 100) / 100).toString()


}

@RequiresApi(Build.VERSION_CODES.R)
class DisplayServiceFromApi30Impl(override val context: Context) : DisplayService {
    private var display: Display? = context.display
    private val windowMetrics =
        (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?)!!.currentWindowMetrics

    override fun getSize(): String =
        "${DecimalFormat("#0.0").format(sqrt((windowMetrics.bounds.height() * windowMetrics.bounds.height() + windowMetrics.bounds.width() * windowMetrics.bounds.width()).toDouble()))} ${
            context.getString(
                R.string.display_display_size_summary
            )
        }"

    override fun getHeight(): String = windowMetrics.bounds.height().toString()

    override fun getWidth(): String =
        windowMetrics.bounds.width().toString()

    override fun getFps(): String = (round(display!!.refreshRate * 100) / 100).toString()

}
