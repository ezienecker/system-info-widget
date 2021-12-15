package com.manuzid.systeminfowidget.data.interactors.impl

import android.content.Context
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.interactors.AbstractCategory
import com.manuzid.systeminfowidget.data.models.Information
import com.manuzid.systeminfowidget.di.module.CameraService

/**
 * Created by Emanuel Zienecker on 14.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class CameraCategory(private val context: Context, private val cameraService: CameraService) :
    AbstractCategory() {

    override fun getRequestCode(): Int = 3

    override fun getRequestAction(): String = CAMERA

    override fun getInformationen(): Information = Information(
        context.getString(R.string.camera_picture_format), cameraService.getPictureFormat(),
        context.getString(R.string.camera_picture_size), cameraService.getPictureSize(),
        context.getString(R.string.camera_preview_format), cameraService.getPreviewFormat(),
        context.getString(R.string.camera_preview_size), cameraService.getPreviewSize(),
        context.getString(R.string.camera_supported_sizes), cameraService.getSupportedSizes(),
        context.getString(R.string.camera_front_camera), cameraService.isFaceCamAvailable()
    )

    companion object {
        const val CAMERA = INTENT_FILTER_PREFIX + "CAMERA_WIDGET"
    }
}
