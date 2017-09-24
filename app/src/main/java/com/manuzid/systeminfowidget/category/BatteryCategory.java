package com.manuzid.systeminfowidget.category;

import android.content.Context;
import android.widget.RemoteViews;

import java.util.Map;

import static com.manuzid.systeminfowidget.util.SystemInfoLib.BATTERY;

/**
 * Created by Emanuel Zienecker on 22.09.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class BatteryCategory extends AbstractCategory {

    @Override
    int initRequestCode() {
        return 105;
    }

    @Override
    String initRequestAction() {
        return BATTERY;
    }

    @Override
    public int getButtonId() {
        return 0;
    }

    @Override
    public int getDefaultButtonDrawable() {
        return 0;
    }

    @Override
    public Map<String, Integer> getActiveColoredButtonsMap() {
        return null;
    }

    @Override
    Informationen getInformationen(Context context) {
        return null;
    }

    @Override
    public RemoteViews prepareRemoteView(RemoteViews remoteView, Context context) {
        return null;
    }
}
