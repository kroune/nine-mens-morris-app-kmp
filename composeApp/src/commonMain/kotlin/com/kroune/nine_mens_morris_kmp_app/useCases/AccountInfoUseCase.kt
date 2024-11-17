package com.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountInfoUseCase(
    val accountId: Long,
    val needName: Boolean = true,
    val needRating: Boolean = true,
    val needCreationDate: Boolean = true,
    val needPicture: Boolean = true,
    val name: MutableState<Result<String>?> = mutableStateOf(null),
    val rating: MutableState<Result<Long>?> = mutableStateOf(null),
    val creationDate: MutableState<Result<Triple<Int, Int, Int>>?> = mutableStateOf(null),
    val accountPicture: MutableState<Result<ByteArray>?> = mutableStateOf(null)
) {
    val scope = CoroutineScope(Dispatchers.Default)

    fun reloadName() {
        if (needName) {
            scope.launch {
                name.value = accountInfoInteractor.getAccountLoginById(accountId)
            }
        }
    }

    fun reloadRating() {
        if (needRating) {
            scope.launch {
                rating.value = accountInfoInteractor.getAccountRatingById(accountId)
            }
        }
    }

    fun reloadCreationDate() {
        if (needCreationDate) {
            scope.launch {
                creationDate.value = accountInfoInteractor.getAccountCreationDateById(accountId)
            }
        }
    }

    fun reloadPicture() {
        if (needPicture) {
            scope.launch {
                accountPicture.value = accountInfoInteractor.getAccountPictureById(accountId)
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