package com.manuzid.systeminfowidget.di.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import androidx.core.content.ContextCompat
import com.manuzid.systeminfowidget.R
import java.util.*

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
interface CameraService {
    val context: Context

    fun getPictureFormat(): String

    fun getPictureSize(): String

    fun getPreviewFormat(): String

    fun getPreviewSize(): String

    fun getSupportedSizes(): String

    fun isFaceCamAvailable(): String =
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            context.getString(R.string.camera_face_cam_available)
        } else {
            context.getString(R.string.camera_face_cam_not_available)
        }

}

class CameraServiceImpl(override val context: Context) : CameraService {

    private val unknownInformation = context.getString(R.string.general_unknow)
    private val manager: CameraManager? =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
    private var map: StreamConfigurationMap?

    init {
        map = initCameraCharacteristics()
    }

    override fun getPictureFormat(): String = context.getString(R.string.camera_picture_format_jpeg)

    override fun getPictureSize(): String {
        return map?.let { map ->
            return getPictureSize(
                map.getOutputSizes(ImageFormat.JPEG).sortedWith(sizeByAreaComparator).first()
            )
        } ?: return unknownInformation
    }

    override fun getPreviewFormat(): String = context.getString(R.string.camera_picture_format_jpeg)

    override fun getPreviewSize(): String = getPictureSize()

    override fun getSupportedSizes(): String {
        val supportedFormats = StringBuilder()
        for (outputFormat in map!!.outputFormats) {
            val outputSizes = map!!.getOutputSizes(outputFormat)
            //supportedFormats.append(StringUtils.join(outputSizes, ", ", 0, outputSizes.size))
        }

        return supportedFormats.toString()
    }

    private fun initCameraCharacteristics(): StreamConfigurationMap? {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            return null
        }

        if (manager == null) {
            return null
        }

        val cameraIds: Array<String> = manager.cameraIdList

        return manager.getCameraCharacteristics(
            cameraIds.first()
        )[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]
    }

    private fun getPictureSize(size: Size): String = size.height.toString() + " x " + size.width

    private val sizeByAreaComparator = Comparator<Size> { a, b ->
        when {
            (a == null && b == null) -> 0
            (a == null) -> -1
            else -> java.lang.Long.signum(
                a.width.toLong() * a.height -
                        b.width.toLong() * b.height
            )
        }
    }
}
