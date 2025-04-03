package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.launch

class ViewOwnAccountScreenComponent(
    val onNavigationBack: () -> Unit,
    accountId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    var isUploadingNewPictureInProgress = false
    private var _uploadingNewPicture = mutableStateOf<UploadPictureApiResponses?>(null)
    val uploadingNewPicture by _uploadingNewPicture

    private val _accountName = mutableStateOf<LoginByIdApiResponses?>(null)
    val accountName by _accountName
    private val _accountRating = mutableStateOf<RatingByIdApiResponses?>(null)
    val accountRating by _accountRating
    private var _accountCreationDate = mutableStateOf<CreationDateByIdApiResponses?>(null)
    val accountCreationDate by _accountCreationDate
    private var _accountPicture = mutableStateOf<AccountPictureByIdApiResponses?>(null)
    val accountPicture by _accountPicture

    private val accountInfoUseCase = AccountInfoUseCase(
        accountId,
        playerInfo = AccountInfoUseCase.PlayerInfo(
            name = _accountName,
            rating = _accountRating,
            creationDate = _accountCreationDate,
            accountPicture = _accountPicture
        )
    )

    fun onEvent(event: ViewOwnAccountScreenEvent) {
        when (event) {
            ViewOwnAccountScreenEvent.Logout -> {
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

            ViewOwnAccountScreenEvent.Back -> {
                onNavigationBack()
            }

            is ViewOwnAccountScreenEvent.UploadNewPicture -> {
                isUploadingNewPictureInProgress = true
                componentScope.launch {
                    _uploadingNewPicture.value = accountInfoInteractor.uploadPicture(event.picture)
                    isUploadingNewPictureInProgress = false
                }
            }
        }
    }

    override fun onBackPressed() {
        onEvent(ViewOwnAccountScreenEvent.Back)
    }
}