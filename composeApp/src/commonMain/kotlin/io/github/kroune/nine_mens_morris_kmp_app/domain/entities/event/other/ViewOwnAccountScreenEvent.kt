package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other

sealed interface ViewOwnAccountScreenEvent {
    class UploadNewPicture(val picture: ByteArray): ViewOwnAccountScreenEvent
    data object OnBackPressed: ViewOwnAccountScreenEvent
    data object OnLogoutPressed: ViewOwnAccountScreenEvent
    data object ReloadIcon: ViewOwnAccountScreenEvent
    data object ReloadName: ViewOwnAccountScreenEvent
    data object ReloadRating: ViewOwnAccountScreenEvent
    data object ReloadCreationDate: ViewOwnAccountScreenEvent
}