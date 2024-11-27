package com.kroune.nine_mens_morris_kmp_app.event

sealed interface ViewAccountScreenEvent {
    data object Back: ViewAccountScreenEvent
    data object Logout: ViewAccountScreenEvent
    data object ReloadIcon: ViewAccountScreenEvent
    data object ReloadName: ViewAccountScreenEvent
    data object ReloadRating: ViewAccountScreenEvent
    data object ReloadCreationDate: ViewAccountScreenEvent
}