package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    window.onkeyup = {
        if (it.key == "Escape") {
            BackHandler.onCallback()
        }
    }
    ComposeViewport(document.body!!) {
        val lifecycle = LifecycleRegistry()
        val component = DefaultComponentContext(lifecycle)
        val root = RootComponent(component)
        App(root)
    }
}