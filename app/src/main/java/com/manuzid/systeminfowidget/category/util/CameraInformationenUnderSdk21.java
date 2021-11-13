package com.manuzid.systeminfowidget.category.util;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import androidx.annotation.NonNull;
import android.util.Log;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.category.Informationen;

import java.util.List;

import static android.hardware.Camera.open;
import static com.manuzid.systeminfowidget.Constants.LOG_TAG;

/**
 * Mit dieser Klasse können sich die Informationen über der Kamera geholt werden.
 * Diese Klasse unterstützt alles unterhalb von SDK-Version 21.
 * <p>
 * Created by Emanuel Zienecker on 24.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
@SuppressWarnings("deprecation")
public class CameraInformationenUnderSdk21 extends AbstractCameraSupport {
    private Camera camera;

    @Override
    public Informationen getInformationen(Context context) {
        openCamera();

        String pictureFormat = context.getString(R.string.general_unknow);
        String previewFormat = context.getString(R.string.general_unknow);
        String pictureSize = context.getString(R.string.general_unknow);
        String previewPictureSize = context.getString(R.string.general_unknow);
        String supportedPictureSizes = context.getString(R.string.general_unknow);
        String faceCam = context.getString(R.string.general_unknow);

        if (camera != null) {

            if (camera.getParameters() != null) {
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
        }

        Informationen informationen = new Informationen.Builder()
                .first(context.getString(R.string.camera_manufacturer), pictureFormat)
                .second(context.getString(R.string.camera_product), pictureSize)
                .third(context.getString(R.string.camera_model), previewFormat)
                .fourth(context.getString(R.string.camera_brand), previewPictureSize)
                .fifth(context.getString(R.string.camera_serialnumber), supportedPictureSizes)
                .sixth(context.getString(R.string.camera_front_camera), faceCam)
                .build();

        closeCamera();

        return informationen;
    }

    @Override
    String getPictureFormat(Context context) {
        return getPictureFormatForUI(camera.getParameters().getPictureFormat(), context);
    }

    @Override
    String getPictureSize() {
        return getPictureSize(camera.getParameters().getPictureSize());
    }

    @Override
    String getPreviewFormat(Context context) {
        return getPictureFormatForUI(camera.getParameters().getPreviewFormat(), context);
    }

    @Override
    String getPreviewSize() {
        return getPictureSize(camera.getParameters().getPreviewSize());
    }

    @Override
    String getSupportedSizes() {
        return getPreviewSizes(camera.getParameters().getSupportedPictureSizes());
    }

    @NonNull
    private String getPictureFormatForUI(final int pictureInt, final Context context) {
        switch (pictureInt) {
            case ImageFormat.JPEG:
                return context.getString(R.string.camera_picture_format_jpeg);
            case ImageFormat.NV16:
                return context.getString(R.string.camera_picture_format_nv);
            case ImageFormat.NV21:
                return context.getString(R.string.camera_picture_format_nv_second);
            case ImageFormat.RGB_565:
                return context.getString(R.string.camera_picture_format_rgb);
            case ImageFormat.YUY2:
                return context.getString(R.string.camera_picture_format_yuy);
            case ImageFormat.YV12:
                return context.getString(R.string.camera_picture_format_yv);
            default:
                return context.getString(R.string.general_unknow);
        }
    }

    private String getPictureSize(final Camera.Size size) {
        return size.height + " x " + size.width;
    }

    @NonNull
    private String getPreviewSizes(final List<Camera.Size> supportedPictureSizes) {
        StringBuilder sb = new StringBuilder();
        for (Camera.Size size : supportedPictureSizes) {
            sb.append(getPictureSize(size));
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        return sb.toString();
    }

    private void openCamera() {
        camera = open();
    }

    private void closeCamera() {
        camera.release();
    }
}
