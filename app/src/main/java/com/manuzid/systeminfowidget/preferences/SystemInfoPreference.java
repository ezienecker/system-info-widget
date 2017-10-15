package com.manuzid.systeminfowidget.preferences;

/**
 * Pojo-Klasse für die Pseudo-Einstellungs-Activity.
 * <p>
 * Created by Emanuel Zienecker on 22.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
class SystemInfoPreference {
    // Titel der Einstellung
    String title;
    // Untertitel (kleine, knappe Beschreibung) der Einstellung
    String summary;
    // Bestimmt ob es sich um eine Header (Kopfzeile) handelt
    boolean header;

    SystemInfoPreference(final String title, final String summary, final boolean header) {
        this.title = title;
        this.summary = summary;
        this.header = header;
    }
}