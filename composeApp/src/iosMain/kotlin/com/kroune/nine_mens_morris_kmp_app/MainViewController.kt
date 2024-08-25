package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.ui.window.ComposeUIViewController
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent

fun MainViewController(component: RootComponent) = ComposeUIViewController {
    App(component)
}