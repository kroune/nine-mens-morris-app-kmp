package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface AccountPictureByIdApiResponses {
    class Success(val picture: ByteArray): AccountPictureByIdApiResponses
    class CredentialsError: AccountPictureByIdApiResponses
    class NetworkError: AccountPictureByIdApiResponses
    class ServerError: AccountPictureByIdApiResponses
    class UnknownError: AccountPictureByIdApiResponses
}