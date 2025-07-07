package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface AccountPictureByIdApiResponses<T> {
    class Success<T>(val picture: T) : AccountPictureByIdApiResponses<T>
    class CredentialsError<T> : AccountPictureByIdApiResponses<T>
    class NetworkError<T> : AccountPictureByIdApiResponses<T>
    class ServerError<T> : AccountPictureByIdApiResponses<T>
    class UnknownError<T> : AccountPictureByIdApiResponses<T>
}