package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.di.koinModule
import io.github.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.screen.RootScreen
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(koinModule)
    }
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
        },
        icon = painterResource(Res.drawable.icon)
    ) {
        val lifecycle = LifecycleRegistry()
        val component = remember {
            RootComponent(componentContext = DefaultComponentContext(lifecycle))
        }
        RootScreen(component)
    }
}