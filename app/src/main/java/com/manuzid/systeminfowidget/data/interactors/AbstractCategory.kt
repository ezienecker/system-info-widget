package com.manuzid.systeminfowidget.data.interactors

import android.content.Context
import android.widget.RemoteViews
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.data.models.Information

/**
 * Created by Emanuel Zienecker on 13.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
abstract class AbstractCategory {

    private var btnId = 0

    /**
     * Liefert den Request-Code der von der SubClass gesetzt werden muss.
     * Für den PendingIntent wird eine entsprechende Code gebraucht.
     *
     * @return Request-Code
     */
    abstract fun getRequestCode(): Int

    /**
     * Liefert die Request-Action die von der SubClass gesetzt werden muss.
     * Für den PendingIntent wird eine entsprechende Action gebraucht.
     *
     * @return Request-Action
     */
    abstract fun getRequestAction(): String

    abstract fun getInformationen(): Information

    open fun getRemoteView(context: Context): RemoteViews {
        val information = getInformationen()
        return RemoteViews(context.packageName, R.layout.system_info_main).apply {
            setTextViewText(R.id.widget_first_label, information.firstLabel)
            setTextViewText(R.id.widget_first_information, information.firstValue)
            setTextViewText(R.id.widget_second_label, information.secondLabel)
            setTextViewText(R.id.widget_second_information, information.secondValue)
            setTextViewText(R.id.widget_third_label, information.thirdLabel)
            setTextViewText(R.id.widget_third_information, information.thirdValue)
            setTextViewText(R.id.widget_fourth_label, information.fourthLabel)
            setTextViewText(R.id.widget_fourth_information, information.fourthValue)
            setTextViewText(R.id.widget_fifth_label, information.fifthLabel)
            setTextViewText(R.id.widget_fifth_information, information.fifthValue)
            setTextViewText(R.id.widget_sixth_label, information.sixthLabel)
            setTextViewText(R.id.widget_sixth_information, information.sixthValue)
        }
    }

    companion object {
        const val INTENT_FILTER_PREFIX = "com.manuzid.systeminfowidget."
        const val DEFAULT = INTENT_FILTER_PREFIX + "NONE"
    }

}
