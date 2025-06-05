package io.github.kroune.nine_mens_morris_kmp_app.component.other

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.event.other.ViewAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ViewAccountScreenComponent(
    private val onNavigationBack: () -> Unit,
    accountId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    private val _state = MutableStateFlow(
        ViewAccountScreenState(
            null,
            null,
            null,
            null
        )
    )
    val state: StateFlow<ViewAccountScreenState>
        get() = _state

    private val accountInfoUseCase = AccountInfoUseCase(
        accountId = accountId,
        onLoginResult = { result ->
            _state.update {
                it.copy(
                    accountLoginResult = result
                )
            }
        },
        onRatingResult = { result ->
            _state.update {
                it.copy(
                    accountRatingResult = result
                )
            }
        },
        needCreationDate = { result ->
            _state.update {
                it.copy(
                    accountCreationDateResult = result
                )
            }
        },
        needPicture = { result ->
            _state.update {
                it.copy(
                    accountPictureResult = result
                )
            }
        },
        scope = componentScope
    )

    fun onEvent(event: ViewAccountScreenEvent) {
        when (event) {
            ViewAccountScreenEvent.Logout -> {
                jwtTokenInteractor.logout()
                onNavigationBack()
            }

            ViewAccountScreenEvent.ReloadCreationDate -> {
                accountInfoUseCase.reloadCreationDate()
            }

            ViewAccountScreenEvent.ReloadIcon -> {
                accountInfoUseCase.reloadPicture()
            }

            ViewAccountScreenEvent.ReloadName -> {
                accountInfoUseCase.reloadName()
            }

            ViewAccountScreenEvent.ReloadRating -> {
                accountInfoUseCase.reloadRating()
            }

            ViewAccountScreenEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(ViewAccountScreenEvent.Back)
    }
}

data class ViewAccountScreenState(
    val accountLoginResult: LoginByIdApiResponses?,
    val accountRatingResult: RatingByIdApiResponses?,
    val accountCreationDateResult: CreationDateByIdApiResponses?,
    val accountPictureResult: AccountPictureByIdApiResponses?
)