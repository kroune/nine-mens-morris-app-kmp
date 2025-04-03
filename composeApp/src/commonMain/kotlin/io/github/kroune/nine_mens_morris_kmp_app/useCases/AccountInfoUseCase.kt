package io.github.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountInfoUseCase(
    val accountId: Long,
    private val needName: Boolean = true,
    private val needRating: Boolean = true,
    private val needCreationDate: Boolean = true,
    private val needPicture: Boolean = true,
    val playerInfo: PlayerInfo = PlayerInfo()
) {
    class PlayerInfo(
        val name: MutableState<LoginByIdApiResponses?> = mutableStateOf(null),
        val rating: MutableState<RatingByIdApiResponses?> = mutableStateOf(null),
        val creationDate: MutableState<CreationDateByIdApiResponses?> = mutableStateOf(null),
        val accountPicture: MutableState<AccountPictureByIdApiResponses?> = mutableStateOf(null)
    )

    private val scope = CoroutineScope(Dispatchers.Default)

    fun reloadName() {
        if (needName) {
            scope.launch {
                playerInfo.name.value = accountInfoInteractor.getAccountLoginById(accountId)
            }
        }
    }

    fun reloadRating() {
        if (needRating) {
            scope.launch {
                playerInfo.rating.value = accountInfoInteractor.getAccountRatingById(accountId)
            }
        }
    }

    fun reloadCreationDate() {
        if (needCreationDate) {
            scope.launch {
                playerInfo.creationDate.value = accountInfoInteractor.getAccountCreationDateById(accountId)
            }
        }
    }

    fun reloadPicture() {
        if (needPicture) {
            scope.launch {
                playerInfo.accountPicture.value = accountInfoInteractor.getAccountPictureById(accountId)
            }
        }
    }

    init {
        reloadName()
        reloadRating()
        reloadCreationDate()
        reloadPicture()
    }
}