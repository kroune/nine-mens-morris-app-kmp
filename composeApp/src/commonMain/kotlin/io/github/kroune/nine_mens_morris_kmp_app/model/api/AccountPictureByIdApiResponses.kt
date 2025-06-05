package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed class AccountPictureByIdApiResponses: Exception() {
    data class Success(val picture: ByteArray): AccountPictureByIdApiResponses()
    data object CredentialsError: AccountPictureByIdApiResponses()
    data object NetworkError: AccountPictureByIdApiResponses()
    data object ServerError: AccountPictureByIdApiResponses()
    data object UnknownError: AccountPictureByIdApiResponses()
}