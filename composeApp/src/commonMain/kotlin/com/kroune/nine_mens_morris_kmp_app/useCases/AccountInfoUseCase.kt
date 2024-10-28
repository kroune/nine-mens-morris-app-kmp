package com.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfoInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountInfoUseCase(
    accountId: Long,
    needName: Boolean = true,
    needRating: Boolean = true,
    needCreationDate: Boolean = true,
    needPicture: Boolean = true
) {
    val scope = CoroutineScope(Dispatchers.Default)

    val gettingNameJob: Job = scope.launch {
        if (needName) {
            name = accountInfoInteractor.getAccountLoginById(accountId)
        }
    }
    var name by mutableStateOf<Result<String>?>(null)
    val gettingRatingJob: Job = scope.launch {
        if (needRating) {
            rating = accountInfoInteractor.getAccountRatingById(accountId)
        }
    }
    var rating by mutableStateOf<Result<Long>?>(null)
    val gettingCreationDateJob: Job = scope.launch {
        if (needCreationDate) {
            creationDate = accountInfoInteractor.getAccountCreationDateById(accountId)
        }
    }
    var creationDate by mutableStateOf<Result<Triple<Int, Int, Int>>?>(null)
    val gettingPictureJob: Job = scope.launch {
        if (needPicture) {
            accountPicture = accountInfoInteractor.getAccountPictureById(accountId)
        }
    }
    var accountPicture by mutableStateOf<Result<ByteArray>?>(null)
}