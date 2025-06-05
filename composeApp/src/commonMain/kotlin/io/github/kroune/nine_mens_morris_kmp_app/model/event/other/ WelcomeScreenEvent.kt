package io.github.kroune.nine_mens_morris_kmp_app.model.event.other

sealed interface WelcomeScreenEvent {
    data object NavigateToGameWithFriend: WelcomeScreenEvent
    data object NavigateToGameWithBot: WelcomeScreenEvent
    /**
     * It should be invoked when user is already authorized
     * invoking onEvent with this object will read isInAccount status immediately
     */
    data object NavigateToOnlineGame: WelcomeScreenEvent
    /**
     * It should be invoked when user is already authorized
     * invoking onEvent with this object will read isInAccount status immediately
     */
    data object NavigateToLeaderboard: WelcomeScreenEvent

    /**
     * It should be invoked when user is already authorized
     * invoking onEvent with this object will read isInAccount status immediately
     */
    data object NavigateToAccountView: WelcomeScreenEvent
    data object NavigateToAboutScreen: WelcomeScreenEvent
    data object CloseTutorial: WelcomeScreenEvent
    data object NavigateBack: WelcomeScreenEvent
}