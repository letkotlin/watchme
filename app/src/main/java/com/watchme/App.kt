package com.watchme

import android.app.Application
import android.util.Log
import com.kakao.auth.KakaoSDK
import com.watchme.dagger.SettingModule
import com.watchme.kakao.KakaoSDKAdapter
import dagger.Component
import javax.inject.Singleton

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

@Component(modules = arrayOf(SettingModule::class))
@Singleton
interface AppComponent {
    fun inject(app: App)
}