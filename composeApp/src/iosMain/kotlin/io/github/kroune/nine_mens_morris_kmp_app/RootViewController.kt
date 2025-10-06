package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.di.koinModule
import io.github.kroune.nine_mens_morris_kmp_app.screen.RootScreen
import org.koin.core.context.startKoin

@Suppress("unused")
fun rootViewController(root: RootComponent) = ComposeUIViewController {
    startKoin {
        modules(koinModule)
    }
    RootScreen(root)
}