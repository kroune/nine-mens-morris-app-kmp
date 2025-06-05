package io.github.kroune.nine_mens_morris_kmp_app.model.event.other

sealed interface AboutScreenEvent {
    object OnNavigationToSourceCode: AboutScreenEvent
    object OnNavigationToCreatorTelegram: AboutScreenEvent
    object OnNavigationToReportAnIssue: AboutScreenEvent
    object OnBackPressed: AboutScreenEvent
}