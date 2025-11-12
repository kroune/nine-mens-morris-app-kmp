package io.github.kroune.nine_mens_morris_kmp_app

import android.app.Application
import io.github.kroune.nine_mens_morris_kmp_app.di.koinModule
import org.koin.core.context.GlobalContext.startKoin

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(koinModule)
        }
    }
}
