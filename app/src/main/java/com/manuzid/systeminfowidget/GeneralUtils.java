package com.manuzid.systeminfowidget;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Sammlung von mehreren Helfer-Methoden.
 * Created by Emanuel Zienecker on 05.11.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class GeneralUtils {
    private GeneralUtils() {}

    /**
     * Prüft ob der Benutzer das Internet aktiviert hat.
     *
     * @param connectivityManager {@link ConnectivityManager}
     * @return True wenn der Benutzer das Internet aktiviert hat, andernfalls false.
     */
    public static boolean isInternetEnabled(final ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
