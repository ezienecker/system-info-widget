package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.category.util.AbstractCameraSupport;
import com.manuzid.systeminfowidget.category.util.CameraInformationenUnderSdk21;
import com.manuzid.systeminfowidget.preferences.ConfigPreferencesActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.manuzid.systeminfowidget.util.SystemInfoLib.CAMERA;

/**
 * Zeigt Informationen über die Kamera das Geräts an.
 *
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class CameraCategory extends AbstractCategory {

    private static final Map<String, Integer> activeColoredButtons;

    static {
        HashMap<String, Integer> mActiveColoredButtons = new HashMap<>();
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLUE, R.drawable.camera_btn_pressed_blue);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_RED, R.drawable.camera_btn_pressed_red);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_LILA, R.drawable.camera_btn_pressed_purple);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_ORANGE, R.drawable.camera_btn_pressed_orange);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_GREEN, R.drawable.camera_btn_pressed_green);
        mActiveColoredButtons.put(ConfigPreferencesActivity.COLOR_BLACK, R.drawable.camera_btn_pressed_black);
        activeColoredButtons = Collections.unmodifiableMap(mActiveColoredButtons);
    }

    private Informationen informationen;

    /**
     * Factory-Klasse um sich die Informationen über die Kamera zu holen.
     * Weil die Api geändert wurde muss entsprechend reagiert werden.
     */
    private AbstractCameraSupport cameraSupport;

    public CameraCategory() {
        if (Build.VERSION.SDK_INT < 21) {
            cameraSupport = new CameraInformationenUnderSdk21();
        } else {
            // TODO Camera2 implementieren und benutzen
            cameraSupport = new CameraInformationenUnderSdk21();
        }
    }

    @Override
    int initRequestCode() {
        return 101;
    }

    @Override
    String initRequestAction() {
        return CAMERA;
    }

    @Override
    public int getButtonId() {
        // TODO
        return 0;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return R.drawable.camera_btn;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return activeColoredButtons;
    }

    @Override
    Informationen getInformationen(Context context) {
        if (informationen == null) {
            informationen = cameraSupport.getInformationen(context);
        }

        return informationen;
    }

    @Override
    public RemoteViews prepareRemoteView(RemoteViews remoteView, Context context) {
        final Informationen informationen = getInformationen(context);
        remoteView.setTextViewText(R.id.lblFirstInfo, informationen.firstLabel);
        remoteView.setTextViewText(R.id.txtFirstInfo, informationen.firstValue);

        remoteView.setTextViewText(R.id.lblSecondInfo, informationen.secondLabel);
        remoteView.setTextViewText(R.id.txtSecondInfo, informationen.secondValue);

        remoteView.setTextViewText(R.id.lblThird, informationen.thirdLabel);
        remoteView.setTextViewText(R.id.txtThird, informationen.thirdValue);

        remoteView.setTextViewText(R.id.lblFourth, informationen.fourthLabel);
        remoteView.setTextViewText(R.id.txtFourth, informationen.fourthValue);

        remoteView.setTextViewText(R.id.lblFifth, informationen.fifthLabel);

        // txtSupportedPictureSizes ist ein Textfeld was als Lauftext definiert ist.
        if (informationen.fifthValue.equals(context.getString(R.string.general_unknow))) {
            remoteView.setTextViewText(R.id.txtFifth, informationen.fifthValue);
            remoteView.setTextViewText(R.id.txtSupportedPictureSizes, "");
        }
        else {
            remoteView.setTextViewText(R.id.txtFifth, "");
            remoteView.setTextViewText(R.id.txtSupportedPictureSizes, informationen.fifthValue);
        }

        remoteView.setTextViewText(R.id.lblSixth, informationen.sixthLabel);
        remoteView.setTextViewText(R.id.txtSixth, informationen.sixthValue);

        remoteView.setTextViewText(R.id.lblSeventh, informationen.seventhLabel);
        remoteView.setTextViewText(R.id.txtSeventh, informationen.seventhValue);

        remoteView.setViewVisibility(R.id.devicememory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.imgRestore, View.GONE);

        return remoteView;
    }
}
