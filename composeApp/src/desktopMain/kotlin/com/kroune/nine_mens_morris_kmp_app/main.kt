package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nine men's morris game",
        onKeyEvent = {
            if (it.key == Key.Escape && it.type == KeyEventType.KeyUp) {
                BackHandler.onCallback()
                true
            } else {
                false
            }
        }
    ) {
        val lifecycle = LifecycleRegistry()
        val component = remember {
            RootComponent(DefaultComponentContext(lifecycle))
        }
        App(component)
    }
}