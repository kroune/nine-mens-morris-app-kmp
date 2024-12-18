package com.kroune.nine_mens_morris_kmp_app.data.remote

sealed class RatingByIdApiResponses: Exception() {
    data object CredentialsError: RatingByIdApiResponses()
    data object ServerError: RatingByIdApiResponses()
    data object ClientError: RatingByIdApiResponses()
    data object NetworkError: RatingByIdApiResponses()
}

sealed class CheckJwtTokenApiResponses: Exception() {
    data object ServerError: CheckJwtTokenApiResponses()
    data object ClientError: CheckJwtTokenApiResponses()
    data object NetworkError: CheckJwtTokenApiResponses()
}

sealed class CreationDateByIdApiResponses: Exception() {
    data object CredentialsError: CreationDateByIdApiResponses()
    data object ServerError: CreationDateByIdApiResponses()
    data object ClientError: CreationDateByIdApiResponses()
    data object NetworkError: CreationDateByIdApiResponses()
}

sealed class LoginByIdApiResponses: Exception() {
    data object CredentialsError: LoginByIdApiResponses()
    data object NetworkError: LoginByIdApiResponses()
    data object ServerError: LoginByIdApiResponses()
    data object ClientError: LoginByIdApiResponses()
}

sealed class UploadPictureApiResponses: Exception() {
    data object CredentialsError: UploadPictureApiResponses()
    data object NetworkError: UploadPictureApiResponses()
    data object ServerError: UploadPictureApiResponses()
    data object ClientError: UploadPictureApiResponses()
    data class TooLargeImage(val maxWidth: Int, val maxHeight: Int): UploadPictureApiResponses()
}

sealed class LeaderboardApiResponses: Exception() {
    data object CredentialsError: LeaderboardApiResponses()
    data object NetworkError: LeaderboardApiResponses()
    data object ServerError: LeaderboardApiResponses()
    data object ClientError: LeaderboardApiResponses()
}

sealed class AccountIdByJwtTokenApiResponses: Exception() {
    data object CredentialsError: AccountIdByJwtTokenApiResponses()
    data object NetworkError: AccountIdByJwtTokenApiResponses()
    data object ServerError: AccountIdByJwtTokenApiResponses()
    data object ClientError: AccountIdByJwtTokenApiResponses()
}

sealed class AccountPictureByIdApiResponses: Exception() {
    data object CredentialsError: AccountPictureByIdApiResponses()
    data object NetworkError: AccountPictureByIdApiResponses()
    data object ServerError: AccountPictureByIdApiResponses()
    data object ClientError: AccountPictureByIdApiResponses()
}

sealed class LoginApiResponses: Exception() {
    data object NetworkError: LoginApiResponses()
    data object CredentialsError: LoginApiResponses()
    data object ServerError: LoginApiResponses()
    data object ClientError: LoginApiResponses()
}

sealed class RegisterApiResponses: Exception() {
    data object LoginAlreadyInUse: RegisterApiResponses()
    data object NetworkError: RegisterApiResponses()
    data object ServerError: RegisterApiResponses()
    data object ClientError: RegisterApiResponses()
}

sealed class SearchingForGameResponses: Exception() {
    data object NetworkError: SearchingForGameResponses()
    data object ServerError: SearchingForGameResponses()
    data object ClientError: SearchingForGameResponses()
}