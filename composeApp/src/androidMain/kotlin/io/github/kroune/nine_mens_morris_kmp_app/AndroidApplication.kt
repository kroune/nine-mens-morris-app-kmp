package io.github.kroune.nine_mens_morris_kmp_app

import android.app.Application
import android.content.Context

lateinit var appContext: Context

class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}