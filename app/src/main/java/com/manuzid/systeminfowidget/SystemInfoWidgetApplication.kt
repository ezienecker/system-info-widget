package com.manuzid.systeminfowidget

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Created by Emanuel Zienecker on 12.12.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class SystemInfoWidgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@SystemInfoWidgetApplication)
            modules(appModule)
        }
    }
}
