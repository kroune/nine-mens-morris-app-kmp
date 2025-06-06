package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.di.koinModule
import org.koin.core.context.startKoin

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(koinModule)
    }
    val root = remember {
        RootComponent(DefaultComponentContext(LifecycleRegistry()))
    }
    App(root)
}