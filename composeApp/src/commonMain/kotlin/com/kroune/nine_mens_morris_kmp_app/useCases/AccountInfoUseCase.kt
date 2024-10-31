package com.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfoInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res

class AccountInfoUseCase(
    accountId: Long,
    needName: Boolean = true,
    needRating: Boolean = true,
    needCreationDate: Boolean = true,
    needPicture: Boolean = true,
    val name: MutableState<Result<String>?> = mutableStateOf(null),
    val rating: MutableState<Result<Long>?> = mutableStateOf(null),
    val creationDate: MutableState<Result<Triple<Int, Int, Int>>?> = mutableStateOf(null),
    val accountPicture: MutableState<Result<ByteArray>?> = mutableStateOf(null)
) {
    val scope = CoroutineScope(Dispatchers.Default)

    val gettingNameJob: Job = scope.launch {
        if (needName) {
            name.value = accountInfoInteractor.getAccountLoginById(accountId)
        }
    }
    val gettingRatingJob: Job = scope.launch {
        if (needRating) {
            rating.value = accountInfoInteractor.getAccountRatingById(accountId)
        }
    }
    val gettingCreationDateJob: Job = scope.launch {
        if (needCreationDate) {
            creationDate.value = accountInfoInteractor.getAccountCreationDateById(accountId)
        }
    }
    val gettingPictureJob: Job = scope.launch {
        if (needPicture) {
            accountPicture.value = accountInfoInteractor.getAccountPictureById(accountId)
        }
    }

    init {
        gettingPictureJob.start()
        gettingCreationDateJob.start()
        gettingNameJob.start()
        gettingRatingJob.start()
    }
}