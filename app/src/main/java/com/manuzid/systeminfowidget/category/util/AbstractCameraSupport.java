package com.manuzid.systeminfowidget.category.util;

import android.content.Context;
import android.content.res.Resources;

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
    abstract String isFaceCamAvailable(Context context);

    public abstract Informationen getInformationen(Context context);
}