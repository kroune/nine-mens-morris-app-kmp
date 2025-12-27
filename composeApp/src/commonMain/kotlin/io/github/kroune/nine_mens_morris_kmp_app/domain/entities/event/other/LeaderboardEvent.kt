package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other

sealed interface LeaderboardEvent {
    data class NavigateToAccountView(val index: Int): LeaderboardEvent
    data class ReloadIcon(val index: Int): LeaderboardEvent
    data class ReloadName(val index: Int): LeaderboardEvent
    data class ReloadRating(val index: Int): LeaderboardEvent
    data object Back: LeaderboardEvent
}
