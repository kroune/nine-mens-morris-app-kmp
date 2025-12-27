package io.github.kroune.nine_mens_morris_kmp_app

import android.app.Application
import io.github.kroune.nine_mens_morris_kmp_app.di.initKoin

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
