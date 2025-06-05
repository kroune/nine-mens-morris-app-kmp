package io.github.kroune.nine_mens_morris_kmp_app.useCases

import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AccountInfoUseCase(
    val accountId: Long,
    private val onLoginResult: ((LoginByIdApiResponses) -> Unit)? = null,
    private val onRatingResult: ((RatingByIdApiResponses) -> Unit)? = null,
    private val needCreationDate: ((CreationDateByIdApiResponses) -> Unit)? = null,
    private val needPicture: ((AccountPictureByIdApiResponses) -> Unit)? = null,
    private val scope: CoroutineScope,
) {
    fun reloadName() {
        if (onLoginResult == null)
            return
        scope.launch {
            onLoginResult(accountInfoInteractor.getAccountLoginById(accountId))
        }
    }

    fun reloadRating() {
        if (onRatingResult == null)
            return
        scope.launch {
            onRatingResult(accountInfoInteractor.getAccountRatingById(accountId))
        }
    }

    fun reloadCreationDate() {
        if (needCreationDate == null)
            return
        scope.launch {
            needCreationDate(accountInfoInteractor.getAccountCreationDateById(accountId))
        }
    }

    fun reloadPicture() {
        if (needPicture == null)
            return
        scope.launch {
            needPicture(accountInfoInteractor.getAccountPictureById(accountId))
        }
    }

    init {
        reloadName()
        reloadRating()
        reloadCreationDate()
        reloadPicture()
    }
}