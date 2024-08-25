package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "nineMensMorrisAppKMP",
    ) {
        val lifecycle = LifecycleRegistry()
        val component = remember {
            RootComponent(DefaultComponentContext(lifecycle))
        }
        App(component)
    }
}