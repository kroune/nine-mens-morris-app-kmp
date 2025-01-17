package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import com.kroune.nine_mens_morris_kmp_app.navigation.Url
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class)
fun main() {
    window.onkeyup = {
        if (it.key == "Escape") {
            BackHandler.onCallback()
        }
    }
    val lifecycle = LifecycleRegistry()
    val root: RootComponent
    if (!window.location.host.contains("github")) {
        root = withWebHistory { stateKeeper, deepLink ->
            val component = DefaultComponentContext(lifecycle, stateKeeper)
            RootComponent(
                component,
                deepLinkUrl = deepLink?.let(::Url)
            )
        }
    } else {
        val component = DefaultComponentContext(lifecycle)
        root = RootComponent(
            component,
            deepLinkUrl = null
        )
    }
    ComposeViewport(document.body!!) {
        App(root)
    }
}