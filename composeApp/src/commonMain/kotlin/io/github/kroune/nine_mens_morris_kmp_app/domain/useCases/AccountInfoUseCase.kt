package io.github.kroune.nine_mens_morris_kmp_app.domain.useCases

import androidx.compose.ui.graphics.ImageBitmap
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap

class AccountInfoUseCase(
    private val accountId: Long,
    private val onLoginResult: ((LoginByIdApiResponses) -> Unit)? = null,
    private val onRatingResult: ((RatingByIdApiResponses) -> Unit)? = null,
    private val needCreationDate: ((CreationDateByIdApiResponses) -> Unit)? = null,
    private val needPicture: ((AccountPictureByIdApiResponses<ImageBitmap>) -> Unit)? = null,
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
            val accountPicture = accountInfoRepository.getAccountPictureById(accountId)
            val decodedAccountPicture = if (accountPicture is AccountPictureByIdApiResponses.Success) {
                AccountPictureByIdApiResponses.Success(accountPicture.picture.decodeToImageBitmap())
            } else {
                @Suppress("UNCHECKED_CAST")
                accountPicture as AccountPictureByIdApiResponses<ImageBitmap>
            }
            needPicture(decodedAccountPicture)
        }
    }

    init {
        reloadName()
        reloadRating()
        reloadCreationDate()
        reloadPicture()
    }
}