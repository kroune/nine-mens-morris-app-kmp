package io.github.kroune.nine_mens_morris_kmp_app.component.other.aboutScreenComponent

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.AppInfo
import io.github.kroune.nine_mens_morris_kmp_app.model.event.other.AboutScreenEvent
import kotlinx.coroutines.flow.MutableStateFlow

class AboutScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val _state = MutableStateFlow(
        AboutScreenState(
            AppInfo(
                "https://t.me/LichnyiSvetM",
            ),
            AppInfo(
                "https://github.com/kroune/nine-mens-morris-app-kmp",
            ),
            AppInfo(
                "https://github.com/kroune/nine-mens-morris-app-kmp/issues/new",
            ),
        )
    )
    val state
        get() = _state

    override fun onBackPressed() {
        onEvent(AboutScreenEvent.OnBackPressed)
    }

    fun onEvent(event: AboutScreenEvent) {
        when (event) {
            AboutScreenEvent.OnBackPressed -> {
                onNavigationBack()
            }
            AboutScreenEvent.OnNavigationToCreatorTelegram -> {}
            AboutScreenEvent.OnNavigationToReportAnIssue -> {}
            AboutScreenEvent.OnNavigationToSourceCode -> {}
        }
    }
}

data class AboutScreenState(
    val telegram: AppInfo,
    val github: AppInfo,
    val githubIssue: AppInfo
)
