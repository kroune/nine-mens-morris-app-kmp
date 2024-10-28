package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenRepositoryInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.CreationDateByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.LoginByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.PictureByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RatingByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ClientErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ServerErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.UnknownErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase

class ViewAccountScreenComponent(
    val onNavigationToWelcomeScreen: () -> Unit,
    val isOwnAccount: Boolean,
    accountId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val accountInfo = AccountInfoUseCase(accountId)

    var accountName by mutableStateOf<String?>(null)
    var accountRating by mutableStateOf<Long?>(null)
    var accountCreationDate by mutableStateOf<Triple<Int, Int, Int>?>(null)
    var accountPicture by mutableStateOf<ByteArray?>(null)

    var popupToDraw by mutableStateOf(
        @Composable {
        }
    )

    fun onEvent(event: ViewAccountScreenEvent) {
        when (event) {
            ViewAccountScreenEvent.Logout -> {
                jwtTokenRepositoryInteractor.logout()
                onNavigationToWelcomeScreen()
            }
        }
    }

    init {
        accountInfo.gettingNameJob.invokeOnCompletion {
            when (it) {
                null -> {
                    accountName = accountInfo.name!!.getOrThrow()
                }


                is LoginByIdApiResponses -> {
                    when (it) {
                        LoginByIdApiResponses.ClientError -> {
                            popupToDraw = @Composable {
                                ClientErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        LoginByIdApiResponses.CredentialsError -> TODO()
                        LoginByIdApiResponses.ServerError -> {
                            popupToDraw = @Composable {
                                ServerErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        LoginByIdApiResponses.NetworkError -> TODO()
                    }
                }

                else -> {
                    popupToDraw = @Composable {
                        UnknownErrorPopUp()
                    }
                }
            }
        }
        accountInfo.gettingRatingJob.invokeOnCompletion {
            when (it) {
                null -> {
                    accountRating = accountInfo.rating!!.getOrThrow()
                }

                is RatingByIdApiResponses -> {
                    when (it) {
                        RatingByIdApiResponses.ClientError -> {
                            popupToDraw = @Composable {
                                ClientErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        RatingByIdApiResponses.CredentialsError -> TODO()
                        RatingByIdApiResponses.ServerError -> {
                            popupToDraw = @Composable {
                                ServerErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        RatingByIdApiResponses.NetworkError -> TODO()
                    }
                }

                else -> {
                    popupToDraw = @Composable {
                        UnknownErrorPopUp()
                    }
                }
            }
        }
        accountInfo.gettingCreationDateJob.invokeOnCompletion {
            when (it) {
                null -> {
                    accountCreationDate = accountInfo.creationDate!!.getOrThrow()
                }

                is CreationDateByIdApiResponses -> {
                    when (it) {
                        CreationDateByIdApiResponses.ClientError -> {
                            popupToDraw = @Composable {
                                ClientErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        CreationDateByIdApiResponses.CredentialsError -> TODO()
                        CreationDateByIdApiResponses.ServerError -> {
                            popupToDraw = @Composable {
                                ServerErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        CreationDateByIdApiResponses.NetworkError -> TODO()
                    }
                }

                else -> {
                    popupToDraw = @Composable {
                        UnknownErrorPopUp()
                    }
                }
            }
        }
        accountInfo.gettingPictureJob.invokeOnCompletion {
            when (it) {
                null -> {
                    accountPicture = accountInfo.accountPicture!!.getOrThrow()
                }

                is PictureByIdApiResponses -> {
                    when (it) {
                        PictureByIdApiResponses.ClientError -> {
                            popupToDraw = @Composable {
                                ClientErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        PictureByIdApiResponses.CredentialsError -> TODO()
                        PictureByIdApiResponses.ServerError -> {
                            popupToDraw = @Composable {
                                ServerErrorPopUp()
                            }
                            return@invokeOnCompletion
                        }

                        PictureByIdApiResponses.NetworkError -> TODO()
                    }
                }

                else -> {
                    popupToDraw = @Composable {
                        UnknownErrorPopUp()
                    }
                }
            }
        }
    }
}