package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface UploadPictureApiResponses {
    data object Success: UploadPictureApiResponses
    data object CredentialsError: UploadPictureApiResponses
    data object NetworkError: UploadPictureApiResponses
    data object ServerError: UploadPictureApiResponses
    data object UnknownError: UploadPictureApiResponses
    data class TooLargeImage(val maxWidth: Int, val maxHeight: Int): UploadPictureApiResponses
}