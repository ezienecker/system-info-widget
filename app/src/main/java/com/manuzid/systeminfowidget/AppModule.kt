package com.manuzid.systeminfowidget

import android.os.Build
import com.manuzid.systeminfowidget.data.interactors.impl.*
import com.manuzid.systeminfowidget.di.module.*
import org.koin.dsl.module

/**
 * Created by Emanuel Zienecker on 12.12.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
val appModule = module {
    single<BatteryService> { BatteryServiceImpl(get()) }
    single { BatteryCategory(get(), get()) }

    single<CameraService> { CameraServiceImpl(get()) }
    single { CameraCategory(get(), get()) }

    single {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            DisplayServiceFromApi30Impl(get())
        } else {
            DisplayServiceUnderApi30Impl(get())
        }
    }

    single { DisplayCategory(get(), get()) }

    single<GeneralInformationService> { GeneralInformationServiceImpl() }
    single { GeneralInformationCategory(get(), get()) }

    single<MoreInformationService> { MoreInformationServiceImpl() }
    single { MoreInformationCategory(get(), get()) }

    single<NetworkService> { NetworkServiceImpl(get()) }
    single { NetworkCategory(get(), get()) }
}
