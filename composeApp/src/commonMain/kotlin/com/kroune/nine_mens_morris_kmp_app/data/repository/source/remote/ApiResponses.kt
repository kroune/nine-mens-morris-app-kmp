package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote

sealed class RatingByIdApiResponses: Exception() {
    data object CredentialsError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}

sealed class CreationDateByIdApiResponses: Exception() {
    data object CredentialsError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}

sealed class LoginByIdApiResponses: Exception() {
    data object CredentialsError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}

sealed class PictureByIdApiResponses: Exception() {
    data object CredentialsError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}

sealed class RegisterApiResponses: Exception() {
    data object LoginAlreadyInUse
    data object CredentialsError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}