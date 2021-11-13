package com.manuzid.systeminfowidget.category.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.Size;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.StringUtils;
import com.manuzid.systeminfowidget.category.Informationen;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static com.manuzid.systeminfowidget.Constants.LOG_TAG;

/**
 * Created by Emanuel Zienecker on 02.11.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
@TargetApi(21)
public class CameraInformationenEqualAndOverSdk21 extends AbstractCameraSupport {
    private static final String TAG = CameraInformationenEqualAndOverSdk21.class.getSimpleName();

    private StreamConfigurationMap map;

    @Override
    String getPictureFormat(Context context) {
        return context.getString(R.string.camera_picture_format_jpeg);
    }

    @Override
    String getPictureSize() {
        Size pictureSize = Collections.max(
                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());

        return getPictureSize(pictureSize);
    }

    @Override
    String getPreviewFormat(Context context) {
        return context.getString(R.string.camera_picture_format_jpeg);
    }

    @Override
    String getPreviewSize() {
        Size pictureSize = Collections.max(
                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());

        return getPictureSize(pictureSize);
    }

    @Override
    String getSupportedSizes() {
        StringBuilder supportedFormats = new StringBuilder();
        for(Integer outputFormat : map.getOutputFormats()) {
            final Size[] outputSizes = map.getOutputSizes(outputFormat);
            supportedFormats.append(StringUtils.join(outputSizes, ", ", 0, outputSizes.length));
        }

        return supportedFormats.toString();
    }

    @Override
    public Informationen getInformationen(Context context) {
        map = retrieveCameraInformationMap(context);

        String pictureFormat = context.getString(R.string.general_unknow);
        String previewFormat = context.getString(R.string.general_unknow);
        String pictureSize = context.getString(R.string.general_unknow);
        String previewPictureSize = context.getString(R.string.general_unknow);
        String supportedPictureSizes = context.getString(R.string.general_unknow);
        String faceCam = context.getString(R.string.general_unknow);

        if (map != null) {

            try {
                pictureFormat = getPictureFormat(context);
                pictureSize = getPictureSize();
                previewFormat = getPreviewFormat(context);
                previewPictureSize = getPreviewSize();
                supportedPictureSizes = getSupportedSizes();
                faceCam = isFaceCamAvailable(context);
            } catch (Exception e) {
                Log.e(LOG_TAG,
                        "Beim sammeln der Informationen über die Kamera ist ein schwerer Fehler aufgetreten.", e);
            }

        }

        return new Informationen.Builder()
                .first(context.getString(R.string.camera_manufacturer), pictureFormat)
                .second(context.getString(R.string.camera_product), pictureSize)
                .third(context.getString(R.string.camera_model), previewFormat)
                .fourth(context.getString(R.string.camera_brand), previewPictureSize)
                .fifth(context.getString(R.string.camera_serialnumber), supportedPictureSizes)
                .sixth(context.getString(R.string.camera_front_camera), faceCam)
                .build();

    }

    @Nullable
    private StreamConfigurationMap retrieveCameraInformationMap(final Context context) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);

        if (permissionCheck == PackageManager.PERMISSION_DENIED)
            return null;

        try {
            if (manager == null) {
                Log.e(TAG, "Es konnte auf die Kamera nicht zugegriffen werden.");
                return null;
            }

            String[] cameraIds = manager.getCameraIdList();

            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(cameraIds[0]);

            return characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        } catch (Exception e) {
            Log.e(TAG, "Es konnte auf die Kamera nicht zugegriffen werden.");
        }

        return null;
    }

    private String getPictureSize(final Size size) {
        return size.getHeight() + " x " + size.getWidth();
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    private static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
