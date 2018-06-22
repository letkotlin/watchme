package com.watchme

import android.app.Application
import android.util.Log
import com.kakao.auth.KakaoSDK
import com.watchme.kakao.KakaoSDKAdapter

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i("WATCHME", "App onCreate()")
        KakaoSDK.init(KakaoSDKAdapter())
    }

}
