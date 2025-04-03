package io.github.kroune.nine_mens_morris_kmp_app.event.other

sealed interface ViewOwnAccountScreenEvent {
    data class UploadNewPicture(val picture: ByteArray): ViewOwnAccountScreenEvent
    data object Back: ViewOwnAccountScreenEvent
    data object Logout: ViewOwnAccountScreenEvent
    data object ReloadIcon: ViewOwnAccountScreenEvent
    data object ReloadName: ViewOwnAccountScreenEvent
    data object ReloadRating: ViewOwnAccountScreenEvent
    data object ReloadCreationDate: ViewOwnAccountScreenEvent
}