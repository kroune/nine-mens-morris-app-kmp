package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase

class ViewAccountScreenComponent(
    val onNavigationToWelcomeScreen: () -> Unit,
    val isOwnAccount: Boolean,
    accountId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val _accountName = mutableStateOf<Result<String>?>(null)
    var accountName by _accountName
    private val _accountRating = mutableStateOf<Result<Long>?>(null)
    var accountRating by _accountRating
    private var _accountCreationDate = mutableStateOf<Result<Triple<Int, Int, Int>>?>(null)
    var accountCreationDate by _accountCreationDate
    private var _accountPicture = mutableStateOf<Result<ByteArray>?>(null)
    var accountPicture by _accountPicture

    init {
        AccountInfoUseCase(
            accountId,
            name = _accountName,
            rating = _accountRating,
            creationDate = _accountCreationDate,
            accountPicture = _accountPicture
        )
    }

    fun onEvent(event: ViewAccountScreenEvent) {
        when (event) {
            ViewAccountScreenEvent.Logout -> {
                jwtTokenInteractor.logout()
                onNavigationToWelcomeScreen()
            }
        }
    }
}