package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.event.ScreenAEvent

class ScreenAComponent(
    componentContext: ComponentContext,
    private val onNavigateToScreenB: (String) -> Unit
) : ComponentContext by componentContext {
    private val _text = mutableStateOf("idk")

    val text: State<String>
        get() = _text

    fun onEvent(event: ScreenAEvent) {
        when (event) {
            is ScreenAEvent.ClickButton -> {
                onNavigateToScreenB(text.value)
            }
            is ScreenAEvent.UpdateText -> {
                _text.value = event.string
            }
        }
    }
}