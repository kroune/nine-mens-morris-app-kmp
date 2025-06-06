package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.di.koinModule
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.allStringResources
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.koin.core.context.GlobalContext.startKoin

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class, ExperimentalResourceApi::class
)
fun main() {
    window.onkeyup = {
        if (it.key == "Escape") {
            BackHandler.onCallback()
        }
    }
    startKoin {
        modules(koinModule)
    }
    val lifecycle = LifecycleRegistry()
    val root = withWebHistory { stateKeeper, _ ->
        val component = DefaultComponentContext(lifecycle, stateKeeper)
        RootComponent(component)
    }
    ComposeViewport(document.body!!) {
        LaunchedEffect(Unit) {
            onLoadFinished()
        }
        App(root)
    }
    // start fetching all resources asynchronously
    with(CoroutineScope(Dispatchers.Default)) {
        Res.allStringResources.forEach { (_, resource) ->
            launch {
                getString(resource)
            }
        }
    }
}

external fun onLoadFinished()