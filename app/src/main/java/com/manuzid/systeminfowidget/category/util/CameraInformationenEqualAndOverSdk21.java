package com.manuzid.systeminfowidget.category.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.category.Informationen;

import static com.manuzid.systeminfowidget.Constants.LOG_TAG;

/**
 * Created by Emanuel Zienecker on 02.11.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class CameraInformationenEqualAndOverSdk21 extends AbstractCameraSupport {
    private CameraDevice camera;
    private CameraManager manager;

    @Override
    String getPictureFormat(Context context) {
        return null;
    }

    @Override
    String getPictureSize() {
        return null;
    }

    @Override
    String getPreviewFormat(Context context) {
        return null;
    }

    @Override
    String getPreviewSize() {
        return null;
    }

    @Override
    String getSupportedSizes() {
        return null;
    }

    @Override
    String getSupportedFormats(Context context) {
        return null;
    }

    @Override
    public Informationen getInformationen(Context context) {
        // openCamera();

        String pictureFormat = context.getString(R.string.general_unknow);
        String previewFormat = context.getString(R.string.general_unknow);
        String pictureSize = context.getString(R.string.general_unknow);
        String previewPictureSize = context.getString(R.string.general_unknow);
        String supportedPictureSizes = context.getString(R.string.general_unknow);
        String supportedPictureFormats = context.getString(R.string.general_unknow);
        String faceCam = context.getString(R.string.general_unknow);

        /*
        if (camera != null) {

            if (camera.getParameters() != null) {
                try {
                    pictureFormat = getPictureFormat(context);
                    pictureSize = getPictureSize();
                    previewFormat = getPreviewFormat(context);
                    previewPictureSize = getPreviewSize();
                    supportedPictureSizes = getSupportedSizes();
                    supportedPictureFormats = getSupportedFormats(context);
                    faceCam = isFaceCamAvailable(context);
                } catch (Exception e) {
                    Log.e(LOG_TAG,
                            "Beim sammeln der Informationen über die Kamera ist ein schwerer Fehler aufgetreten.", e);
                }
            }
        }
        */

        Informationen informationen = new Informationen.Builder()
                .first(context.getString(R.string.camera_manufacturer), pictureFormat)
                .second(context.getString(R.string.camera_product), pictureSize)
                .third(context.getString(R.string.camera_model), previewFormat)
                .fourth(context.getString(R.string.camera_brand), previewPictureSize)
                .fifth(context.getString(R.string.camera_serialnumber), supportedPictureSizes)
                .sixth(context.getString(R.string.camera_supported_formats), supportedPictureFormats)
                .seventh(context.getString(R.string.camera_front_camera), faceCam)
                .build();

        // closeCamera();

        return informationen;
    }

    @TargetApi(21)
    public void openCamera(final Context context, final int cameraId) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] cameraIds = manager.getCameraIdList();

            /*

            manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }
            }, null);
            */
        } catch (Exception e) {
            // TODO handle
        }
    }
}
