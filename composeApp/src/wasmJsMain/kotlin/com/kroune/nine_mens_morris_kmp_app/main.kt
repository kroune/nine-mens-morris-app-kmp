package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.allStringResources
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class, ExperimentalResourceApi::class
)
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
        LaunchedEffect(Unit) {
            onLoadFinished()
        }
        App(root)
    }
    // start fetching all resources asynchronously
    with(CoroutineScope(Dispatchers.Default)) {
        Res.allStringResources.forEach {
            launch {
                getString(it.value)
            }
        }
    }
}

external fun onLoadFinished()