package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface UploadPictureApiResponses {
    class Success: UploadPictureApiResponses
    class CredentialsError: UploadPictureApiResponses
    class NetworkError: UploadPictureApiResponses
    class ServerError: UploadPictureApiResponses
    class UnknownError: UploadPictureApiResponses
    class TooLargeImage(val maxWidth: Int, val maxHeight: Int): UploadPictureApiResponses
}