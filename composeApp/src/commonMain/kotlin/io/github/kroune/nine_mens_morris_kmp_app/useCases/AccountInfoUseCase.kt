package io.github.kroune.nine_mens_morris_kmp_app.useCases

import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo.AccountInfoRepositoryI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AccountInfoUseCase(
    private val accountId: Long,
    private val onLoginResult: ((LoginByIdApiResponses) -> Unit)? = null,
    private val onRatingResult: ((RatingByIdApiResponses) -> Unit)? = null,
    private val needCreationDate: ((CreationDateByIdApiResponses) -> Unit)? = null,
    private val needPicture: ((AccountPictureByIdApiResponses) -> Unit)? = null,
    private val scope: CoroutineScope,
    private val accountInfoRepository: AccountInfoRepositoryI,
) {
    fun reloadName() {
        if (onLoginResult == null)
            return
        scope.launch {
            onLoginResult(accountInfoRepository.getAccountLoginById(accountId))
        }
    }

    fun reloadRating() {
        if (onRatingResult == null)
            return
        scope.launch {
            onRatingResult(accountInfoRepository.getAccountRatingById(accountId))
        }
    }

    fun reloadCreationDate() {
        if (needCreationDate == null)
            return
        scope.launch {
            needCreationDate(accountInfoRepository.getAccountCreationDateById(accountId))
        }
    }

    fun reloadPicture() {
        if (needPicture == null)
            return
        scope.launch {
            needPicture(accountInfoRepository.getAccountPictureById(accountId))
        }
    }

    init {
        reloadName()
        reloadRating()
        reloadCreationDate()
        reloadPicture()
    }
}