package com.watchme.dagger

import com.watchme.App
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kws on 2017. 11. 29..
 */
@Singleton
@Module
class SettingModule @Inject internal constructor(private val app: App) {

    @Provides
    internal fun preferenceUtil(): PreferenceUtil {
        return PreferenceUtil(app)
    }
}