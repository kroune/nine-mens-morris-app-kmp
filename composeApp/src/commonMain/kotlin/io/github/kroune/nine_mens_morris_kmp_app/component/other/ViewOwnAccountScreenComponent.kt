package io.github.kroune.nine_mens_morris_kmp_app.component.other

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ViewOwnAccountScreenComponent(
    private val onNavigationBack: () -> Unit,
    accountId: Long,
    private val accountInfoRepository: AccountInfoRepositoryI,
    private val jwtTokenInteractor: JwtTokenRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
    private val _state = MutableStateFlow(
        ViewOwnAccountScreenState(
            false,
            null,
            null,
            null,
            null,
            null
        )
    )
    val state: StateFlow<ViewOwnAccountScreenState>
        get() = _state

    private val _uploadingNewPictureResult = MutableSharedFlow<UploadPictureApiResponses>()
    val uploadingNewPictureResult
        get() = _uploadingNewPictureResult.asSharedFlow()

    private val componentScope = componentCoroutineScope()

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
        scope = componentScope,
        get()
    )

    fun onEvent(event: ViewOwnAccountScreenEvent) {
        when (event) {
            ViewOwnAccountScreenEvent.OnLogoutPressed -> {
                jwtTokenInteractor.logout()
                onNavigationBack()
            }

            ViewOwnAccountScreenEvent.ReloadCreationDate -> {
                accountInfoUseCase.reloadCreationDate()
            }

            ViewOwnAccountScreenEvent.ReloadIcon -> {
                accountInfoUseCase.reloadPicture()
            }

            ViewOwnAccountScreenEvent.ReloadName -> {
                accountInfoUseCase.reloadName()
            }

            ViewOwnAccountScreenEvent.ReloadRating -> {
                accountInfoUseCase.reloadRating()
            }

            ViewOwnAccountScreenEvent.OnBackPressed -> {
                onNavigationBack()
            }

            is ViewOwnAccountScreenEvent.UploadNewPicture -> {
                _state.update {
                    it.copy(
                        isUploadingNewPictureInProgress = true
                    )
                }
                componentScope.launch {
                    _uploadingNewPictureResult.emit(
                        accountInfoRepository.uploadPicture(event.picture)
                    )
                    _state.update {
                        it.copy(
                            isUploadingNewPictureInProgress = false
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        onEvent(ViewOwnAccountScreenEvent.OnBackPressed)
    }
}

data class ViewOwnAccountScreenState(
    val isUploadingNewPictureInProgress: Boolean,
    val uploadingNewPictureResult: UploadPictureApiResponses?,
    val accountLoginResult: LoginByIdApiResponses?,
    val accountRatingResult: RatingByIdApiResponses?,
    val accountCreationDateResult: CreationDateByIdApiResponses?,
    val accountPictureResult: AccountPictureByIdApiResponses?
)
