package com.kroune.nine_mens_morris_kmp_app.event

sealed interface WelcomeScreenEvent {
    data object ClickGameWithFriendButton: WelcomeScreenEvent
    data object ClickGameWithBotButton: WelcomeScreenEvent
    data object ClickOnlineGameButton: WelcomeScreenEvent
    data object AccountViewButton: WelcomeScreenEvent
    data object CloseTutorial: WelcomeScreenEvent
    data object RetryGettingAccountId: WelcomeScreenEvent
}