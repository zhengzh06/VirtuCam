package com.wangyiheng.VirtuCam.data.di


import com.wangyiheng.VirtuCam.utils.InfoManager
import org.koin.dsl.module
val appModule = module {
    single { InfoManager(get()) }
}