package com.manuzid.systeminfowidget.category.util;

import android.content.Context;
import android.content.pm.PackageManager;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.category.Informationen;

/**
 * Created by Emanuel Zienecker on 24.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public abstract class AbstractCameraSupport {
    abstract String getPictureFormat(Context context);
    abstract String getPictureSize();
    abstract String getPreviewFormat(Context context);
    abstract String getPreviewSize();
    abstract String getSupportedSizes();
    abstract String getSupportedFormats(Context context);

    String isFaceCamAvailable(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                return context.getString(R.string.camera_facecam_available);
            } else {
                return context.getString(R.string.camera_facecam_not_available);
            }
        } catch (Exception e) {
            return context.getString(R.string.general_unknow);
        }
    }

    public abstract Informationen getInformationen(Context context);
}